package com.cnscud.xpower.filemanipulate.store.ftp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cnscud.xpower.filemanipulate.store.DeployServerItem;

/**
 * Ftp 整体配置信息.
 *
 * @author Felix Zhang  Date 2012-10-24 14:34
 * @version 1.0.0
 */
public class FtpStoreSetting {
    
    private List<DeployServerItem> servers; //server列表
    private int retryTimes = 3; //重试次数
    private Map<String, String> auth = new HashMap<>();

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public List<DeployServerItem> getServers() {
        return servers;
    }

  
    

    public void setServers(List<DeployServerItem> servers) {
        this.servers = servers;
    }
    public Map<String, String> getAuth() {
        return auth;
    }
    
     @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FtpStoreSetting [");
        if (servers != null) {
            builder.append("servers=");
            builder.append(servers);
            builder.append(", ");
        }
        builder.append("retryTimes=");
        builder.append(retryTimes);
        builder.append("]");
        return builder.toString();
    }
}
