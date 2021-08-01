package com.cnscud.xpower.cache.impl;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import com.cnscud.xpower.cache.IRedisCluster;
import org.redisson.config.SingleServerConfig;

/**
 * Redis集群的配置工厂
 * 
 * @author Ady Liu (imxylz@gmail.com)
 * @since 2019-07-25
 */
public class RedisClusterAutoConfigCacheFactory extends AbstractAutoConfigCacheFactory<IRedisCluster> {

    private static RedisClusterAutoConfigCacheFactory instance = new RedisClusterAutoConfigCacheFactory();

    private RedisClusterAutoConfigCacheFactory() {
    }

    public static RedisClusterAutoConfigCacheFactory getInstance() {
        return instance;
    }

    @Override
    protected IRedisCluster buildCache(String id, IRedisCluster oldCache) {
        Config config = new Config();
        String serverList = getServiceUrl(id).trim();
        config.useClusterServers().addNodeAddress(serverList.split("\\s+"));
        config.setCodec(new StringCodec());
        RedissonClient cluster = Redisson.create(config);
        RedisClusterCache rcc = new RedisClusterCache(cluster);
        return rcc;
    }

    @Override
    protected IRedisCluster buildSingleCache(String id, IRedisCluster oldCache) {
        //现在只有单机支持密码 一般不推荐使用密码
        Config config = new Config();
        String serverList = getServiceUrl(id).trim();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig
                .setConnectTimeout(30000)
                .setTimeout(10000)
                .setRetryAttempts(5)
                .setRetryInterval(3000)
                .setAddress(serverList);
        if (getServiceUrlPwd(id) != null) {
            singleServerConfig.setPassword(getServiceUrlPwd(id));
        }
        RedissonClient client = Redisson.create(config);
        return new RedisClusterCache(client);
    }

}