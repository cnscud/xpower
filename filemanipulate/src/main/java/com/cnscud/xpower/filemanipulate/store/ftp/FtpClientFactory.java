/**
 * 
 */
package com.cnscud.xpower.filemanipulate.store.ftp;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.filemanipulate.store.DeployServerItem;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem.ServerType;

/**
 * ftp服务器配置
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013年4月3日
 */
public class FtpClientFactory implements PoolableObjectFactory<IFtpClient> {
    final Logger logger = LoggerFactory.getLogger(getClass());

    
    final List<DeployServerItem> serverItems;
    final String poolId;

    public FtpClientFactory(FtpStoreSetting ftpStoreSetting,ServerType serverType, String poolId) {
        this.serverItems = ftpStoreSetting.getServers().stream().filter(x->x.isNormal(serverType)).collect(Collectors.toList());
        this.poolId = poolId;
    }

    @Override
    public IFtpClient makeObject() throws Exception {
        if (serverItems.isEmpty()) {
            throw new IllegalArgumentException("no available ftp servers");
        }
        DeployServerItem server = serverItems.get(RandomUtils.nextInt(serverItems.size()));
        FtpFileDeployClient client = new FtpFileDeployClient(server, poolId);
        client.connect();
        return client;
    }

    @Override
    public void destroyObject(IFtpClient obj) throws Exception {
        if(obj!=null) {
            obj.disconnect();
        }
    }

    @Override
    public boolean validateObject(IFtpClient obj) {
        try {
            ((FtpFileDeployClient) obj).ftp.noop();
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return false;
    }

    @Override
    public void activateObject(IFtpClient obj) throws Exception {
    }

    @Override
    public void passivateObject(IFtpClient obj) throws Exception {
    }

}
