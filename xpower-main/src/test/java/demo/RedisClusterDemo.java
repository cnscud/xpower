package demo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.LocalCachedMapOptions.EvictionPolicy;
import org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.LocalCachedMapOptions.SyncStrategy;
import org.redisson.api.RLocalCachedMap;

import com.cnscud.xpower.cache.IRedisCluster;
import com.cnscud.xpower.cache.impl.RedisClusterAutoConfigCacheFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-07-23
 */
public class RedisClusterDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        IRedisCluster redis = RedisClusterAutoConfigCacheFactory.getInstance().getCache("cluster.main");
        try {
            //run(redis);
            test1(redis);
        } finally {
            redis.destroy();
        }
    }
    static void test1(IRedisCluster redis) {
        LocalCachedMapOptions options = LocalCachedMapOptions.defaults()
                // 用于淘汰清除本地缓存内的元素
                // 共有以下几种选择:
                // LFU - 统计元素的使用频率，淘汰用得最少（最不常用）的。
                // LRU - 按元素使用时间排序比较，淘汰最早（最久远）的。
                // SOFT - 元素用Java的WeakReference来保存，缓存元素通过GC过程清除。
                // WEAK - 元素用Java的SoftReference来保存, 缓存元素通过GC过程清除。
                // NONE - 永不淘汰清除缓存元素。
               .evictionPolicy(EvictionPolicy.NONE)
               // 如果缓存容量值为0表示不限制本地缓存容量大小
               .cacheSize(1000)
                // 以下选项适用于断线原因造成了未收到本地缓存更新消息的情况。
                // 断线重连的策略有以下几种：
                // CLEAR - 如果断线一段时间以后则在重新建立连接以后清空本地缓存
                // LOAD - 在服务端保存一份10分钟的作废日志
                //        如果10分钟内重新建立连接，则按照作废日志内的记录清空本地缓存的元素
                //        如果断线时间超过了这个时间，则将清空本地缓存中所有的内容
                // NONE - 默认值。断线重连时不做处理。
               .reconnectionStrategy(ReconnectionStrategy.NONE)
                // 以下选项适用于不同本地缓存之间相互保持同步的情况
                // 缓存同步策略有以下几种：
                // INVALIDATE - 默认值。当本地缓存映射的某条元素发生变动时，同时驱逐所有相同本地缓存映射内的该元素
                // UPDATE - 当本地缓存映射的某条元素发生变动时，同时更新所有相同本地缓存映射内的该元素
                // NONE - 不做任何同步处理
               .syncStrategy(SyncStrategy.INVALIDATE)
                // 每个Map本地缓存里元素的有效时间，默认毫秒为单位
               .timeToLive(10000)
                // 或者
               .timeToLive(10, TimeUnit.SECONDS)
                // 每个Map本地缓存里元素的最长闲置时间，默认毫秒为单位
               .maxIdle(10000)
                // 或者
               .maxIdle(10, TimeUnit.SECONDS);
        //
        RLocalCachedMap<String, String> map  = redis.getRawClient().getLocalCachedMap("localcache", options);
        map.putIfAbsent("poa1", "这是第一个POA");
        map.putIfAbsent("poa2", "这是第2个POA");
        map.putIfAbsent("poa3", "这是第III个POA");
        //
        for(int i=0;i<20;i++) {
            System.out.println("poa1="+ map.get("poa1"));
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        }
    }

    static void run(IRedisCluster redis) {
        
        
        
        String key1 = "{key}1";
        redis.delete(key1);
        String key2 = "{key}2";
        redis.delete(key2);
        String key3 = "{key}3";
        redis.delete(key3);
        //
        redis.setString(key1, "value1");
        redis.setString(key2, "value2");
        redis.setString(key3, "value3");
        //
        System.out.println(redis.getString(key1));
        System.out.println(redis.getString(key2));
        System.out.println(redis.getString(key3));
        //
        System.out.println(redis.get(Arrays.asList(key1, key2, key3)));
        //
        redis.hset("amap2", "name", "fanfan");
        redis.hset("amap2", "project", "redis");
        System.out.println(redis.hgetAll("amap2"));
        //
        String uncompressData = "{\n" + "    \"user\" : \" 我是中国人\",\n" + "    \"kill\" : \" all baby\"\n" + "}";
        System.out.println(uncompressData.length());
        redis.setBytesGzip("compress", uncompressData.getBytes(StandardCharsets.UTF_8), 0, TimeUnit.DAYS);

        System.out.println(new String(redis.getBytesGzip("compress"), StandardCharsets.UTF_8));
    }

}
