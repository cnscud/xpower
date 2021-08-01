package com.cnscud.xpower.configcenter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkClient;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkNoNodeException;
import com.github.zkclient.exception.ZkNodeExistsException;
import com.cnscud.xpower.Charsets;
import com.cnscud.xpower.configcenter.GlobalConfig.GlobalConfigKey;

/**
 * 配置中心
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012年10月23日
 */
public class ConfigCenter {

    final static Logger logger = LoggerFactory.getLogger(ConfigCenter.class);
    static volatile IZkClient zkclient;
    private static final Charsets charset = Charsets.UTF8;
    private static final ConfigCenter instance = new ConfigCenter();
    static volatile Boolean product;
    static volatile GlobalConfig globalConfig = new GlobalConfig();

    private static void _loadGlobalConfig() {
        logger.info("loading global config ...");
        String globalValue = getInstance().getDataAsString("/xpower/config/global");
        Properties prop = new Properties();
        try {
            prop.load(new StringReader(globalValue));
            globalConfig.putAll(prop);
        } catch (IOException e) {
            logger.error("loading global config failed", e);
        }
    }
    public static GlobalConfig getGlobalConfig() {
        if (globalConfig.isEmpty()) {
            _loadGlobalConfig();
        }
        return globalConfig;
    }
    
    public static ConfigCenter getInstance() {
        return instance;
    }

    public static String getFullConnection() {
        return SystemConfig.getInstance().getString("ZK_HOSTS", "127.0.0.1:2181");
    }
    /**
     * 当前环境是否是生产环境
     * @return true 是生产环境
     * @since 2015年5月19日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static boolean isProduct() {
        return getGlobalConfig().is(GlobalConfigKey.product, false);
    }

    public static IZkClient getZkClient() {
        if (zkclient == null) {
            synchronized (ConfigCenter.class) {
                if (zkclient == null) {
                    zkclient = new ZkClient(getFullConnection(), 30 * 1000);
                    zkclient.subscribeDataChanges("/xpower/config/global", (IZkHandler) ConfigCenter::_loadGlobalConfig);
                }
            }
        }
        return zkclient;
    }

    public boolean deletePath(String path) {
        return getZkClient().delete(path);
    }

    public boolean createPath(String path) {
        return createPath(path, new byte[0]);
    }

    public boolean createPath(String path, String value) {
        return createPath(path, charset.getBytes(value));
    }

    public boolean createPath(String path, byte[] data) {
        try {
            String realPath = getZkClient().create(path, data, CreateMode.PERSISTENT);
            return realPath != null && realPath.equals(path);
        } catch (ZkNodeExistsException e) {
            logger.warn("create path fail. path exists: " + path);
            return false;
        } catch (RuntimeException e) {
            throw new ConfigException(e);
        }
    }

    public byte[] getData(String path) {
        return getZkClient().readData(path, true);
    }

    public List<String> getChildren(String path) {
        return getZkClient().getChildren(path);
    }
    /**
     * 获取某个节点的字符串值（节点不存在返回null)
     * @param path 节点
     * @return null或者字符串值
     */
    public String getDataAsString(String path) {
        String result = SystemConfig.getInstance().getString(path.replace('/', '$'), null);
        if (result == null) {
            byte[] data = getData(path);
            result = data != null ? charset.toString(data) : null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getDataAsString: " + path + ", data=" + result);
        }
        return result;
    }

    public boolean updateData(String path, byte[] data) {
        try {
            getZkClient().writeData(path, data);
            return true;
        } catch (ZkNoNodeException nee) {
            return false;
        }
    }

    public boolean updateData(String path, String value) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("updatePath: path=%s, value=%s", path, value));
        }
        return updateData(path, charset.getBytes(value));
    }

    /**
     * 刷新某个节点的数据为当前时间
     * 
     * @param path
     *            刷新节点（如果节点不存在则自动创建节点)
     * @return 是否刷新成功
     * @since 2013年12月26日
     */
    public boolean reflushStatus(String path) {
        return reflushStatus(path, System.currentTimeMillis());
    }
    /**
     * 刷新某个节点的数据为当前时间
     * 
     * @param path
     *            刷新节点（如果节点不存在则自动创建节点)
     * @return 是否刷新成功
     * @since 2016年12月12日
     */
    public boolean reflushStatus(String path, long status) {
        return updateData(path, String.valueOf(status)) || createPath(path, String.valueOf(status));
    }
    /**
     * 获取某个节点的状态
     * @param path 节点路径，例如 /xpower/status/google-traffic-tasker
     * @return 0或者整数值
     */
    public long getStatus(String path) {
        String data = getDataAsString(path);
        return data !=null&&!data.isEmpty() ? Long.parseLong(data): 0;
    }

    public void subscribeDataChanges(String path, IZkDataListener listener) {
        getZkClient().subscribeDataChanges(path, listener);
    }

    public void subscribeChildChanges(String path, IZkChildListener listener) {
        getZkClient().subscribeChildChanges(path, listener);
    }

    public void unsubscribeChildChanges(String path, IZkChildListener listener) {
        getZkClient().unsubscribeChildChanges(path, listener);
    }

    public void unsubscribeDataChanges(String path, IZkDataListener listener) {
        getZkClient().unsubscribeDataChanges(path, listener);
    }

    /**
     * 获取一个分布式锁
     * 
     * @param lockPath
     *            锁路径，例如 /demo/poa_rebuild
     * @return 分布式锁
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年2月26日
     */
    public IDistributedLock getLock(String lockPath) {
        return new DistributedLockImpl(getZkClient(), lockPath);
    }
    /**
     * 在分布式锁中执行一个任务
     * @param lockPath 锁路径，例如 /lock/yunhu-wechat-lock
     * @param s 具体业务逻辑
     * @return 返回结果
     */
    public <T> T executeInDistributedLock(String lockPath, Supplier<T> s) {
        final IDistributedLock lock = ConfigCenter.getInstance().getLock(lockPath);
        lock.lock();
        try {
            return s.get();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 尝试在分布式锁中执行任务，有可能失败（例如锁被别人占用）
     * @param lockPath 锁路径，例如 /lock/yunhu-wechat-lock
     * @param timeout 锁超时时间
     * @param unit 锁超时时间
     * @param s 执行动作
     * @return 响应结果
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2018年7月11日
     */
    public <T> Optional<T> executeInDistributedTryLock(String lockPath, long timeout, TimeUnit unit, Supplier<T> s) {
        final IDistributedLock lock = ConfigCenter.getInstance().getLock(lockPath);
        try {
            if (lock.tryLock(timeout, unit)) {
                return Optional.ofNullable(s.get());
            }
        } finally {
            lock.unlock();
        }
        return Optional.empty();
    }

    public void executeInDistributedTryLock(String lockPath, long timeout, TimeUnit unit, Runnable task) {
        final IDistributedLock lock = ConfigCenter.getInstance().getLock(lockPath);
        try {
            if (lock.tryLock(timeout, unit)) {
                task.run();
            }
        } finally {
            lock.unlock();
        }
    }
}
