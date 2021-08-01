package com.cnscud.xpower.cache.impl;

import org.redisson.api.RedissonClient;

import com.cnscud.xpower.cache.IRedisCluster;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-07-23
 */
public class RedisClusterCache extends BaseCache implements IRedisCluster {
    final RedissonClient cluster;
    
    public RedisClusterCache(RedissonClient cluster) {
        this.cluster = cluster;
    }

    
    @Override
    public RedissonClient getRawClient() {
        return cluster;
    }

    @Override
    public void destroy() {
        cluster.shutdown();
    }

}
