package com.cnscud.xpower.cache.impl;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import com.cnscud.xpower.cache.IRedis;
import com.cnscud.xpower.management.CoreMbeanServer;

/**
 * Redis的配置工厂
 * @deprecated
 * @author adyliu (adyliu@sohu-inc.com)
 * @since 2011-7-1
 */
public class RedisAutoConfigCacheFactory extends AbstractAutoConfigCacheFactory<IRedis> {

    private static RedisAutoConfigCacheFactory instance = new RedisAutoConfigCacheFactory();

    private RedisAutoConfigCacheFactory() {
    }

    public static RedisAutoConfigCacheFactory getInstance() {
        return instance;
    }

    @Override
    protected IRedis buildCache(String id, IRedis oldCache) {
        List<InetSocketAddress> addrs = getAddresses(getServiceUrl(id));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(1024);//最大支持1024个并发连接
        config.setMaxIdle(100);
        config.setMinIdle(0);
        config.setMaxWait(-1);
        config.setTestOnBorrow(false);//这是不要设置为true，否则极大影响性能
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(10 * 1000L);
        config.setNumTestsPerEvictionRun(10);
        //降低闲置的测试时间，默认情况下redis-server的限制连接时间为300秒
        config.setTimeBetweenEvictionRunsMillis(60 * 1000L);
        List<JedisShardInfo> shards = new ArrayList<>();
        for (InetSocketAddress addr : addrs) {
            JedisShardInfo shard = new JedisShardInfo(addr.getAddress().getHostAddress(), addr.getPort());
            shards.add(shard);
        }
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        //JedisPool pool = new JedisPool(config, addr.getAddress().getHostAddress(), addr.getPort());
        RedisCache rc = destroyOldCache(oldCache, RedisCache.class);
        if (rc == null) {
            rc = new RedisCache();
        }
        rc.setPool(pool);
        rc.setId(id);
        CoreMbeanServer.getInstance().registMBean(rc, rc.getMbeanName());
        return rc;
    }

    @Override
    protected IRedis buildSingleCache(String id, IRedis oldCache) {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisCache rc = destroyOldCache(oldCache, RedisCache.class);
        if (rc == null) {
            rc = new RedisCache();
        }
        config.setMaxIdle(100);
        config.setMaxActive(1024);//最大支持1024个并发连接
        config.setMaxWait(-1);
        config.setMinIdle(0);
        config.setTestOnReturn(false);
        config.setTestOnBorrow(false);//这是不要设置为true，否则极大影响性能
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(10 * 1000L);
        config.setNumTestsPerEvictionRun(10);
        //降低闲置的测试时间，默认情况下redis-server的限制连接时间为300秒
        config.setTimeBetweenEvictionRunsMillis(60 * 1000L);
        List<JedisShardInfo> shards = new ArrayList<>();
        List<InetSocketAddress> addressList = getAddresses(getServiceUrl(id));
        for (InetSocketAddress addr : addressList) {
            JedisShardInfo shard = new JedisShardInfo(addr.getAddress().getHostAddress(), addr.getPort());
            String pwd = getServiceUrlPwd(id);
            if (StringUtils.isNotBlank(pwd)) {
                shard.setPassword(pwd);
            }
            shards.add(shard);
        }
        ShardedJedisPool pool = new ShardedJedisPool(config, shards);
        rc.setPool(pool);
        rc.setId(id);
        CoreMbeanServer.getInstance().registMBean(rc, rc.getMbeanName());
        return rc;
    }

}