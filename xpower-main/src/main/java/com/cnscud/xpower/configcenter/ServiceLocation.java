package com.cnscud.xpower.configcenter;

import com.github.zkclient.IZkDataListener;

/**
 * @author: adyliu(imxylz@gmail.com)
 * @since: 2012-10-25
 */
public class ServiceLocation {
    protected final String parentPath;
    public ServiceLocation(String parentPath){
        this.parentPath = parentPath;
    }

    public String getConfigValue(final String serviceName){
        return ConfigCenter.getInstance().getDataAsString(getPath(serviceName));
    }

    public String getPath(String serviceName){
        return parentPath + "/"+serviceName;
    }

    public void subscribeDataChange(final String serviceName,IZkDataListener listener){
        ConfigCenter.getInstance().subscribeDataChanges(getPath(serviceName),listener);
    }

    public void unsubscribeDataChange(final String serviceName,IZkDataListener listener){
        ConfigCenter.getInstance().unsubscribeDataChanges(getPath(serviceName),listener);
    }
}
