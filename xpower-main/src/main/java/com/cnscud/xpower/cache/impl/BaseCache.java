package com.cnscud.xpower.cache.impl;

import com.cnscud.xpower.cache.ICache;

/**
 * Cache的基础实现
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-08
 */
public abstract class BaseCache implements ICache {

    private String id;

    private long timeout = 3000L;//超时时间

    @Override
    public long decr(String key, long delta) {
        return decr(key, delta, 0);
    }

    @Override
    public long decr(String key, long delta, long initValue) {
        return decr(key, delta, initValue, getTimeout());
    }

    @Override
    public long decr(String key, long delta, long initValue, long timeout) {
        return decr(key, delta, initValue, timeout, 0);
    }

    @Override
    public boolean delete(String key) {
        return delete(key, getTimeout());
    }

    /**
     * 全局的Cache id
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * get mbean name for Cache
     * 
     * @return mbean name
     */
    String getMbeanName() {
        return "com.sohu.suc.platform.core.cache:type=" + getClass().getSimpleName() + "-" + getId();
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public long incr(String key, long delta) {
        return incr(key, delta, 0);
    }

    @Override
    public long incr(String key, long delta, long initValue) {
        return incr(key, delta, initValue, getTimeout());
    }

    @Override
    public long incr(String key, long delta, long initValue, long timeout) {
        return incr(key, delta, initValue, timeout, 0);
    }


    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @deprecated
     * @param name the name to set
     * @see #setId(String)
     */
    public void setName(String name) {
        setId(name);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}