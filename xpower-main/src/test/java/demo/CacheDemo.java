/**
 * 
 */
package demo;

import com.cnscud.xpower.cache.IRedis;
import com.cnscud.xpower.cache.impl.RedisAutoConfigCacheFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年8月31日
 */
public class CacheDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        IRedis redis = RedisAutoConfigCacheFactory.getInstance().getCache("redis.main");
        redis.delete("ok");
        System.out.println("equals 1? "+ redis.incr("ok", 1));
        redis.expire("ok", 2);
        System.out.println("equals 1? "+ redis.incr("ok", 0));
        Thread.sleep(3000);
        System.out.println("equals 0? "+ redis.incr("ok", 0, 3));
        System.out.println("equals 1? "+ redis.incr("ok", 1));
    }

}
