package com.cnscud.xpower.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cache操作接口
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-08
 */
public interface ICache {

    /**
     * 原子减操作
     * 
     * @param key 键值
     * @param delta 累减值，非负数
     * @return 累减后的值，如果此key不存在，则返回0，如果减完后成为负数，则返回0
     * @see #decr(String, long, long, long, int)
     */
    long decr(String key, long delta);

    /**
     * 原子减操作
     * 
     * @param key 键值
     * @param delta 累减值，非负数
     * @param initValue 初始值，非负数
     * @return 累减后的值，如果此key不存在，则返回初始值initValue，如果减完后成为负数，则返回0
     * @see #decr(String, long, long, long, int)
     */
    long decr(String key, long delta, long initValue);

    /**
     * 原子减操作
     * 
     * @param key 键值
     * @param delta 累减值，非负数
     * @param initValue 初始值，非负数
     * @param timeout 超时时间，单位毫秒
     * @return 累减后的值，如果此key不存在，则返回初始值initValue，如果减完后成为负数，则返回0
     * @see #decr(String, long, long, long, int)
     */
    long decr(String key, long delta, long initValue, long timeout);

    /**
     * 原子减操作 <br/>
     * 如果想获取当前值，不能使用get()操作，必须使用incr(0)或者decr(0)操作
     * 
     * @param key 键值
     * @param delta 累减值，非负数
     * @param initValue 初始值，非负数
     * @param timeout 超时时间，单位毫秒
     * @param exp 过期时间，单位秒
     * @return 累减后的值，如果此key不存在，则返回初始值initValue，如果减完后成为负数，则返回0<br/>
     *         假设key对应的值为x:
     *         <ul>
     *         <li>key不存在: 返回initValue(必须是正数，否则返回0)</li>
     *         <li>x>initValue: 返回x - initValue</li>
     *         <li>x<=initValue: 返回0</li>
     *         </ul>
     */
    long decr(String key, long delta, long initValue, long timeout, int exp);

    /**
     * 删除一个缓存
     * 
     * @param key 键值
     * @return 是否删除成功，如果key不存在则返回false
     */
    boolean delete(String key);

    /**
     * 从缓存中删除一个对象
     * 
     * @param key 键值
     * @param opTimeout 操作超时时间，单位毫秒
     * @return 是否删除成功，如果key不存在则返回false
     */
    boolean delete(String key, long opTimeout);

    /**
     * 批量删除多个key
     * 
     * @param keys 多个key
     * @return 删除的数量
     */
    int delete(Collection<String> keys);

    /**
     * 批量获取一批对象
     * 
     * @param <T> 对象类型
     * @param keyCollections 对象的key集合
     * @return 对象对应的键值集合
     */
    <T> Map<String, T> get(Collection<String> keyCollections);

    /**
     * 从Cache中获取一个对象
     * <p>
     * 不同的Cache的获取实现不同，但都是走Java的序列化机制。因此数据是不能跨缓存客户端的。
     * 尤其对于Redis而言，由于Redis不能够区分特定的二进制类型，因此写入Cache中的数据除了序列化字节外还包括一些 标志位，描述是
     * 用序列化实现的。显然这对于Strings数据结构的Redis来说是不能跨客户端的。比如通过命令行就不能够正确的解析此内容。
     * 谨慎操作，需要确定是否使用序列化机制，通常情况下{@link IRedis#getString(String)}都能够满足要求。
     * </p>
     * 
     * @param <T> 对象类型
     * @param key 键值（注意是不带前缀的，需要自己决定是否重复）
     * @return 对应的对象或者null
     * @see {@link IRedis#getString(String)}
     */
    <T> T get(String key);


    /**
     * 原子加操作
     * 
     * @param key 键值
     * @param delta 累加值，非负数
     * @return 累加后的结果
     * @see #incr(String, long, long, long, int)
     */
    long incr(String key, long delta);

    /**
     * 原子加操作
     * 
     * @param key 键值
     * @param delta 累加值，非负数
     * @param initValue 初始值，非负数
     * @return 累加后的值
     * @see #incr(String, long, long, long, int)
     */
    long incr(String key, long delta, long initValue);

    /**
     * 原子加操作
     * 
     * @param key 键值
     * @param delta 累加值，非负数
     * @param initValue 初始值，非负数
     * @param timeout 超时时间，单位毫秒
     * @return 累加后的值
     * @see #incr(String, long, long, long, int)
     */
    long incr(String key, long delta, long initValue, long timeout);

    /**
     * 原子加操作 如果想获取当前值，不能使用get()操作，必须使用incr(0)或者decr(0)操作
     * 
     * @param key 键值
     * @param delta 累加值，非负数
     * @param initValue 初始值，非负数
     * @param timeout 操作超时时间，单位毫秒
     * @param exp 过期时间，单位秒
     * @return 累加后的值，如果此key不存在，则返回初始值initValue，如果加后成为负数，则返回0<br/>
     *         假设key对应的值为x:
     *         <ul>
     *         <li>key不存在: 返回initValue(必须是正数，否则返回0)</li>
     *         <li>x+initValue > 0: 返回x + initValue</li>
     *         <li>x+initValue <= 0: 返回0</li>
     *         </ul>
     */
    long incr(String key, long delta, long initValue, long timeout, int exp);

    /**
     * 设置一个对象
     * 
     * @param key 键值
     * @param exp 过期时间，单位秒
     * @param value 对象值（必须是可序列化）
     * @return 是否操作成功
     * @see {@link #set(String, int, Object, long)}
     * @deprecated
     * @see #set(String, Object, long, TimeUnit)
     */
    default boolean set(String key, int exp, Object value) {
        return set(key, value, exp, TimeUnit.SECONDS);
    }

    /**
     * 设置一个值到Cache中。<br/>
     * 
     * <b>特别说明：对于{@code object }，是使用java的序列化机制，因此是不能通过其它客户端反序列化的。</b>
     * 对于Redis而言，可以使用：
     * <ul>
     * <li>{@link IRedis#setString(String, String)}</li>
     * <li>{@link IRedis#setString(String, String, int)}</li>
     * <li>{@link IRedis#setBytes(String, byte[])}</li>
     * <li>{@link IRedis#setBytes(String, byte[], int)}</li>
     * </ul>
     * 
     * @param key Cache对应的key
     * @param exp 过期时间，单位秒,非负整数
     *        <p>
     *        不同的Cache实现对于过期时间处理不一致:
     *        <ul>
     *        <li>Memcache:
     *        0代表30天(Memcache支持的最大存储时间)；&gt;0代表过期时间（秒），每次更新过期时间</li>
     *        <li>Redis:
     *        &lt;=0如果第一次存储代表永久存储，以后代表不更新时间，自然过期；&gt;0代表过期时间（秒），每次更新过期时间。
     *        另外对于Redis而言，由于对Java序列化支持不太好，因此如果不是明确的序列化Java对象，那么可以调用
     *        {@link IRedis#setString()}等方法</li>
     *        </ul>
     *        </p>
     * @param value 要存储的值
     * @param timeout 操作的超时时间，单位毫秒，如果指定时间未返回将得到一个超时异常
     * @return 操作的结果，是否成功
     * @see {@link IRedis#setString(String, String)}
     */
    boolean set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * Cache 的全局Id
     * 
     * @return cache id
     */
    String getId();

    /**
     * 销毁Cache对象
     */
    void destroy();
}