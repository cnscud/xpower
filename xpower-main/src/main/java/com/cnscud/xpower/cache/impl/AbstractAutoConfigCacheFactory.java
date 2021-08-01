package com.cnscud.xpower.cache.impl;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.zkclient.IZkDataListener;
import com.cnscud.xpower.cache.ICache;
import com.cnscud.xpower.configcenter.ServiceLocation;
import com.cnscud.xpower.management.CoreMbeanServer;

/**
 * 自动配置的抽象Cache实现
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-08
 */
public abstract class AbstractAutoConfigCacheFactory<T extends ICache> extends AbstractCacheFactory<T> {
    final String rootPath = "/xpower/cache";
    final ServiceLocation location = new ServiceLocation(rootPath);
    final String rootPwdPath = "/xpower/cache_pwd";
    final ServiceLocation locationPwd = new ServiceLocation(rootPwdPath);
    final Map<String, IZkDataListener> listeners = new HashMap<String, IZkDataListener>();
    protected String getServiceUrl(String name) {
        String address = location.getConfigValue(name);
        if(address != null) {
            return address.trim();
        }
        throw new RuntimeException("not found service address for cache:" + name);
    }


    protected String getServiceUrlPwd(String name) {
        String address = locationPwd.getConfigValue(name);
        if(address != null) {
            return address.trim();
        }
        return null;//不需要密码
    }


    
    @Override
    public void newCacheAfter(final String id, T cache) {
        final IZkDataListener listener = new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                destroyCache(id);
            }
            public void handleDataChange(String dataPath, byte[] data) throws Exception {
                rebuildCache(id);
                rebuildSingCache(id);
            }
        };
        listeners.put(id, listener);
        location.subscribeDataChange(id, listener);
    }

    @Override
    public void destroyCacheAfter(String id, T cache) {
        IZkDataListener listener = listeners.get(id);
        location.unsubscribeDataChange(id, listener);
    }
    /**
     * 销毁旧Cache，如果注册了Mbean还会销毁Mbean
     * <p>
     * 实际上是调用 {@link ICache#destroy()}方法来销毁旧实例的连接，当然了新实例可以依然使用旧实例对象
     * </p>
     * 
     * @param oldCache 旧Cache实例
     * @param expectClass 期待的Cache类型
     * @return 旧Cache实例
     */
    @SuppressWarnings("unchecked")
    protected <E extends ICache> E destroyOldCache(T oldCache, Class<E> expectClass) {
        if (oldCache != null) {
            logger.warn("destroy old cache: " + oldCache.getId() + ", " + oldCache.getClass());
            oldCache.destroy();
            if (oldCache instanceof BaseCache) {
                BaseCache baseCache = (BaseCache) oldCache;
                logger.warn("unregister mbean name if exist: " + baseCache.getMbeanName());
                CoreMbeanServer.getInstance().unregistMBean(baseCache.getMbeanName());
            }

            if (oldCache.getClass() == expectClass) {
                return (E) oldCache;
            }
        }
        return null;
    }
    
    protected List<InetSocketAddress> getAddresses(String s) {
        if (s == null) {
            throw new NullPointerException("Null host list");
        }
        if (s.trim().equals("")) {
            throw new IllegalArgumentException("No hosts in list:  ``" + s
                    + "''");
        }
        ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();

        for (String hoststuff : s.split(" ")) {
            int finalColon = hoststuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server ``"
                        + hoststuff + "'' in list:  " + s);

            }
            String hostPart = hoststuff.substring(0, finalColon);
            String portNum = hoststuff.substring(finalColon + 1);

            addrs
                    .add(new InetSocketAddress(hostPart, Integer
                            .parseInt(portNum)));
        }
        assert !addrs.isEmpty() : "No addrs found";
        return addrs;
    }
}