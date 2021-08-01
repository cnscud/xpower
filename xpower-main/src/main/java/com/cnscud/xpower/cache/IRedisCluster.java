package com.cnscud.xpower.cache;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RDeque;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;

import com.cnscud.xpower.cache.impl.GzipByteArrayCodec;

/**
 * 集群Redis接口
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-07-23
 */
public interface IRedisCluster extends ICache {

    /**
     * 从队列的左侧弹出 ，阻塞弹出。 现在只支持单一的key。不支持多个key同时操作。
     * 
     * @param timeout
     *            timeout 为0时一直阻塞，直到有值时返回。 timeout>0时,在timeout时间内如果有值时返回。 timeout>0时,在timeout时间后如果还是没有值时返回null。
     * @param key
     *            键值
     * @return 如果队列为空或者超时没有结果则返回null
     * @throws InterruptedException 
     * @see #lpop(String)
     * 
     */
    default String blpop(final String key, long timeout, TimeUnit unit) throws InterruptedException {
        return (String) getRawClient().getBlockingQueue(key).poll(timeout, unit);
    }
    /**
     * 从队列的右侧侧弹出 ，阻塞弹出。 现在只支持单一的key。不支持多个key同时操作。
     * 
     * @param timeout
     *            (单位秒) 为0时一直阻塞，直到有值时返回。 timeout>0时,在timeout时间内如果有值时返回。 timeout>0时,在timeout时间后如果还是没有值时返回null。
     * @param key
     *            键值
     * @return 如果队列为空或者超时没有结果则返回null
     * @throws InterruptedException 
     * @see #rpop(String)
     */
    default String brpop(final String key, long timeout, TimeUnit unit) throws InterruptedException {
        return (String) getRawClient().getBlockingDeque(key).pollLast(timeout, unit);
    }
    default  long decr(String key, long delta, long initValue, long timeout, int exp) {
        RAtomicLong atomicLong = getRawClient().getAtomicLong(key);
        long v = atomicLong.addAndGet(-delta);
        if (timeout > 0) {
            atomicLong.expire(timeout, TimeUnit.MILLISECONDS);
        }
        return v;
    }
    default int delete(Collection<String> keys) {
        return (int) getRawClient().getBuckets().delete(keys.toArray(new String[0]));
    }

    default  boolean delete(String key, long opTimeout) {
        return getRawClient().getBucket(key).delete();
    }

    /**
     * 判断指定的key是否存在
     * 
     * @param key
     *            键值
     * @return true or false
     */
    default boolean exists(String key) {
        return getRawClient().getBucket(key).isExists();
    }
    default boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        return getRawClient().getBucket(key).expire(timeToLive, timeUnit);
    }

    default boolean expireAt(String key, long at) {
        return getRawClient().getBucket(key).expireAt(at);
    }

    default <T> Map<String, T> get(Collection<String> keyCollections) {
        return (Map) getRawClient().getBuckets().get(keyCollections.toArray(new String[0]));
    }

    default String get(String key) {
        return (String) getRawClient().getBucket(key).get();
    }


    default <T> T get(String key, Codec codec) {
        return (T) getRawClient().getBucket(key, codec).get();
    }
    /**
     * 获取二进制字节
     * 
     * @param key
     *            键值
     * @return byte数组或者null
     */
    default byte[] getBytes(String key) {
        return (byte[]) getRawClient().getBucket(key, ByteArrayCodec.INSTANCE).get();
    }

    default byte[] getBytesGzip(String key) {
        return (byte[]) getRawClient().getBucket(key, GzipByteArrayCodec.INSTANCE).get();
    }


    RedissonClient getRawClient();
    //
    //
    //
    /**
     * 以字符串形式获取一个值
     * <p>
     * 这不同于{@link IRedis#get(String)} ，后者存储的是一个二进制，无法通过命令行解析，不保证其它redis客户端能够兼容。 此方法操作的文本字符串，因此是能够和所有客户端兼容的。
     * </p>
     * 
     * @param key
     *            键值
     * @return key对应的值
     */
    default String getString(String key) {
        return (String) getRawClient().getBucket(key, StringCodec.INSTANCE).get();
    }

    /**
     * 删除hash中指定的成员
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return
     */
    default long hdel(String key, String... members) {
        return getRawClient().getMap(key).fastRemove((Object[])members);
    }



    default boolean hexists(String key, String field) {
       return getRawClient().getMap(key).containsKey(field); 
    }

    /**
     * 返回Hash中当前对应member的Value
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 字符串值，不存在返回null
     */
    default String hget(String key, String member) {
        return (String) getRawClient().getMap(key).get(member);
    }

    /**
     * 返回整个hash
     * 
     * @param key
     *            键值
     * @return
     */
    default Map hgetAll(String key){
        return getRawClient().getMap(key).readAllMap();
    }



    /**
     * 增加Hash中相应member的value值
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @param delta
     *            增量
     * @return
     */
    default Long hincrby(String key, String member, long delta) {
        Number n = (Number) getRawClient().getMap(key).addAndGet(member, delta);
        return n == null ? null : n.longValue();
    }

    default Set<String> hkeys(String key){
        return (Set) getRawClient().getMap(key).readAllKeySet();
    }

    default long hlen(String key) {
        return getRawClient().getMap(key).size();
    }

    /**
     * 获取一个key对应HASH字段的所有值
     * 
     * @param key
     *            键
     * @param fields
     *            对应字段
     * @return 对应字段的所有值，如果没有则不在此列表中
     * @author adyliu (imxylz@gmail.com)
     * @since 2012-2-13
     */
    default Map<String, Object> hmget(String key, Collection<?> fields){
        return (Map) getRawClient().getMap(key).getAll(new HashSet<>(fields));
    }

    /**
     * hash 的批量set方法
     * 
     * @param key
     * @param fieldValue
     * @return
     */
    default void hmset(String key, Map fieldValue) {
        getRawClient().getMap(key).putAll(fieldValue);
    }

    /**
     * 向指定的Hash中插入成员
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 前一个旧值
     * @parma value 初始值
     */
    default String hset(String key, String member, Object value) {
        return (String) getRawClient().getMap(key).put(member, value);
    }

    default Collection<String> hvalues(String key){
        return (Collection) getRawClient().getMap(key).readAllValues();
    }

    default long incr(String key, long delta, long initValue, long timeout, int exp) {
        RAtomicLong atomicLong = getRawClient().getAtomicLong(key);
        long v = atomicLong.addAndGet(delta);
        if (timeout > 0) {
            atomicLong.expire(timeout, TimeUnit.MILLISECONDS);
        }
        return v;
    }

    default int linsertAfter(String key, String where, String value) {
        return getRawClient().getList(key).addAfter(where, value);
    }

    /**
     * list中在一个pivot 中的 where位置插入一个value
     * 
     * @param key
     * @param where
     *            BEFORE, AFTER;
     * @param pivot
     * @param value
     * @return
     */
    default int linsertBefore(String key, String where, String value) {
        return getRawClient().getList(key).addBefore(where, value);
    }

   

    /**
     * 获取list的长度
     * 
     * @param key
     *            键值
     * @return 对应list的长度, 没有返回0
     */
    default long llen(String key) {
        return getRawClient().getList(key).size();
    }

    /**
     * 从队列头部取值
     * 
     * @param key
     *            键值
     * @return 从队列中取出的值
     */
    default String lpop(String key) {
        return (String) getRawClient().getQueue(key).poll();
    }

    /**
     * 向列表头添加一个值
     * <p>
     * 如果key不存在则创建一个空列表并加入当前的value
     * </p>
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return 添加后的列表大小
     */
    default long lpush(String key, String value) {
        RDeque<Object> deque = getRawClient().getDeque(key);
        deque.addFirst(value);
        return deque.size();
    }


    /**
     * 返回列表key中指定区间内的元素
     * 
     * @param key
     *            键值
     * @param startIndex
     *            开始下标,以0为开始
     * @param endIndex
     *            结束下标
     * @return 指定区间内的元素
     */
    default List<String> lrange(String key, int startIndex, int endIndex){
        return (List) getRawClient().getList(key).range(startIndex, endIndex);
    }

    /**
     * 移除指定个数个指定值
     * 
     * @param key
     *            键值
     * @param count
     *            指定个数,0为所有
     * @param value
     *            指定值
     * @return 被移除元素数量
     */
    default boolean lrem(String key, int count, String value) {
        return getRawClient().getList(key).remove(value, count);
    }

    /**
     * 截取list,会包括结束的索引
     * 
     * @param key
     *            键值
     * @param start
     *            开始值 (包含）
     * @param end
     *            结束值 （包含）
     */
    default void ltrim(String key, int start, int end) {
        getRawClient().getList(key).trim(start, end);
    }

    /**
     * 剩余过期时间（毫秒）
     * @param key 键值
     * @return -2 表示key不存在，-1表示永不过期，>=0 表示剩余毫秒
     */
    default long remainTimeToLive(String key) {
        return getRawClient().getBucket(key).remainTimeToLive();
    }

    /**
     * 右边移除元素
     * 
     * @param key
     *            键值
     * @return 被移除的元素
     */
    default String rpop(String key) {
        return (String) getRawClient().getDeque(key).pollLast();
    }

    /**
     * 队列末尾添加一个值
     * 
     * @param key
     *            键值
     * @param value
     *            添加的值
     * @return 队列添加后长度
     */
    default long rpush(String key, String value) {
        RList<Object> list = getRawClient().getList(key);
        list.add(value);
        return list.size();
    }

    /**
     * 批量添加成员到一个集合中
     * 
     * @param key
     *            缓存中的键值
     * @param members
     *            要加入的成员
     */
    default boolean sadd(String key, Collection<String> members) {
       return getRawClient().getSet(key).addAll(members); 
    }

    /**
     * 向指定的Set中插入元素
     * 
     * @param key
     *            键值
     * @param member
     *            元素
     */
    default boolean sadd(String key, String member) {
        return getRawClient().getSet(key).add(member);
    }

    /**
     * 获取指定集合的元素数量
     * 
     * @param key
     *            缓存中的键值
     * @return 指定set中的元素数量
     */
    default long scard(String key) {
        return getRawClient().getSet(key).size();
    }

    default void set(Map<String, String> buckets) {
        getRawClient().getBuckets().set(buckets);
    }
    default void set(String key, Object value, Codec codec, long timeout, TimeUnit unit) {
        if(timeout > 0) {
            getRawClient().getBucket(key, codec).set(value, timeout, unit);
        }else {
            getRawClient().getBucket(key, codec).set(value);
        }
    }
    
    /**
     * value 必须是String
     */
    default boolean set(String key, Object value, long timeout, TimeUnit unit) {
        RBucket<Object> bucket = getRawClient().getBucket(key);
        if(timeout > 0) {
            bucket.set(String.valueOf(value), timeout, unit);
        }else {
            bucket.set(String.valueOf(value));
        }
        return true;
    }
    default void setBytes(String key, byte[] data) {
        getRawClient().getBucket(key, ByteArrayCodec.INSTANCE).set(data);
    }

    default void setBytes(String key, byte[] data, long timeToLive, TimeUnit timeUnit) {
        getRawClient().getBucket(key, ByteArrayCodec.INSTANCE).set(data, timeToLive, timeUnit);
    }

    /**
     * GZIP 压缩文本
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    default void setBytesGzip(String key, byte[] value, long timeout, TimeUnit unit) {
        set(key, value, GzipByteArrayCodec.INSTANCE, timeout, unit);
    }

    /**
     * 以字符串形式存取一个值
     * <p>
     * 这不同于{@link IRedis#set(String, int, Object)} ，后者存储的是一个二进制，无法通过命令行解析，不保证其它redis客户端能够兼容。 此方法操作的文本字符串，因此是能够和所有客户端兼容的。
     * </p>
     * 
     * @param key
     *            键值
     * @param value
     *            对应的值
     * @param exp
     *            过期时间，单位秒,非负整数. &lt;=0如果第一次存储代表永久存储，以后代表不更新时间，自然过期；&gt;0代表过期时间（秒），每次更新过期时间。
     * @return 是否操作成功
     */
    default void setString(String key, String value) {
        getRawClient().getBucket(key, StringCodec.INSTANCE).set(value);
    }

    /**
     * 以字符串形式存取一个值
     * <p>
     * 这不同于{@link IRedis#set(String, int, Object)} ，后者存储的是一个二进制，无法通过命令行解析，不保证其它redis客户端能够兼容。 此方法操作的文本字符串，因此是能够和所有客户端兼容的。
     * </p>
     * 
     * @param key
     *            键值
     * @param value
     *            对应的值
     */
    default void setString(String key, String value, long timeToLive, TimeUnit timeUnit) {
        getRawClient().getBucket(key, StringCodec.INSTANCE).set(value, timeToLive, timeUnit);
    }

    /**
     * 判断key对应的set集合中,是否有指定的member成员
     * 
     * @param key
     *            set集合再缓存中的键值
     * @param member
     *            需要判断是否存在的成员
     * @return true:存在,false:不存在
     */
    default boolean sismember(String key, String member) {
        return getRawClient().getSet(key).contains(member);
    }

    /**
     * 获取指定key对应的set集合中的所有数据
     * 
     * @param key
     *            要指定的键值
     * @return 集合中的所有数据
     */
    default Set<String> smembers(String key){
        return (Set) getRawClient().getSet(key).readAll();
    }
    /**
     * 返回一个key的过期时间
     * 
     * @param key
     *            键值
     * @return -2 表示key不存在，-1表示永不过期，>=0 表示剩余毫秒
     */
    default long ttl(String key) {
        return remainTimeToLive(key);
    }

    /**
     * 添加一个记录到排序的队列中
     * 
     * @param key
     *            键值
     * @param score
     *            权值，用于排序
     * @param member
     *            成员
     * @return 添加成功返回1，已经存在则更新权值返回0，未知错误返回null
     */
    default boolean zadd(String key, double score, String member) {
        return getRawClient().getScoredSortedSet(key).add(score, member);
    }

    default int zadd(String key, Map<Object, Double> scoreMembers) {
        return getRawClient().getScoredSortedSet(key).addAll(scoreMembers);
    }

   

    /**
     * 查询排序队列中元素的个数
     * 
     * @param key
     *            键值
     * @return 队列中元素的个数
     */
    default long zcard(String key) {
        return getRawClient().getScoredSortedSet(key).size();
    }

    /**
     * Incr member score in the sorted sets
     * 
     * @param key
     *            the key in redis
     * @param score
     *            the score of member
     * @param member
     *            the member in sorted set
     * @return the new score
     */
    default Double zincrby(final String key, final double score, final String member) {
        return getRawClient().getScoredSortedSet(key).addScore(member, score);
    }

    /**
     * 查询排序队列中指定范围startIndex，endIndex的元素
     * <p>
     * 在三个元素的排序列表
     * <ul>
     * <li>位置： 0 1 2 3（空）</li>
     * <li>位置： -3 -2 -1</li>
     * <li>元素： 1 2 3</li>
     * </ul>
     * 按照redis官方例子举例
     * <ul>
     * <li>0 -1 结果为1 2 3</li>
     * <li>2 3 结果为 3</li>
     * <li>-2 -1 结果为2 3</li>
     * </ul>
     * </p>
     * 
     * @param key
     *            键值
     * @param startIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 包含符合查询条件的元素的set
     */
    default Collection<String> zrange(String key, int startIndex, int endIndex){
        return (Collection) getRawClient().getScoredSortedSet(key).valueRange(startIndex, endIndex);
    }


    /**
     * 获取指定socre范围内min到max的元素的集合
     * 
     * @param key
     *            键值
     * @param min
     *            最小score
     * @param max
     *            最大score
     * @return 返回包含符合查询条件的元素的set
     */
    default Collection<String> zrangeByScore(String key, double min, double max){
        return (Collection) getRawClient().getScoredSortedSet(key).valueRange(min, true, max, true);
    }
    

    /**
     * 获取指定socre范围内min到max的元素和对应score的map
     * 
     * @param key
     *            键值
     * @param min
     *            最小score
     * @param max
     *            最大score
     * @return 包含符合查询条件的元素的Map<元素，权值> 按照权值从小到大
     */
    default Map<String, Double> zrangeByScoreWithScores(String key, double min, double max){
        return getRawClient().getScoredSortedSet(key).entryRange(min, true, max, true)//
                .stream().collect(Collectors.toMap(e -> (String) e.getValue(), e -> e.getScore()));
    }

    /**
     * 查询排序队列中指定范围startIndex，endIndex的元素，
     * 
     * @param key
     *            键值
     * @param startIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 包含符合查询条件的元素的Map<元素，权值> 按照权值从小到大返回
     */
    default Map<String, Double> zrangeWithScore(String key, int startIndex, int endIndex) {
        return getRawClient().getScoredSortedSet(key).entryRange(startIndex, endIndex)//
                .stream().collect(Collectors.toMap(e -> (String) e.getValue(), e -> e.getScore()));
    }

    /**
     * 查询排序队列中某个成员的排名（名次）
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 成员存在则返回该成员的排名，成员不存在，或者键值不存在则null
     */
    default Integer zrank(String key, String member) {
        return getRawClient().getScoredSortedSet(key).rank(member);
    }

    /**
     * 从指定的序列中删除某一个元素
     * 
     * @param key
     *            序列名
     * @param member
     *            被删除的元素名
     */
    default boolean zrem(String key, String member) {
        return getRawClient().getScoredSortedSet(key).remove(member);
    }

    /**
     * 从排序队列中删除元素 排序为从小到大，最小的位置最靠前
     * 
     * @param key
     *            键值
     * @param startIndex
     *            开始位置
     * @param endIndex
     *            结束位置
     * @return 删除的元素个数
     */
    default int zremrangeByRank(String key, int startIndex, int endIndex) {
        return getRawClient().getScoredSortedSet(key).removeRangeByRank(startIndex, endIndex);
    }

    /**
     * 删除名称为key的zset中score >= min且score <= max的所有元素
     * 
     * @param key
     *            序列名
     * @param minScore
     * @param maxScore
     * @return 被删除的元素个数
     */
    default int zremrangeByScore(String key, double minScore, double maxScore) {
        return getRawClient().getScoredSortedSet(key).removeRangeByScore(minScore, true, maxScore, true);
    }
    
    /**
     * 查询排序队列中指定范围startIndex，endIndex的元素，
     * 
     * @param key
     *            键值
     * @param startIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 包含符合查询条件的元素的Map<元素，权值> 按照权值从大到小返回
     */
    default Map<String, Double> zrevrangeWithScore(String key, int startIndex, int endIndex){
        return getRawClient().getScoredSortedSet(key).entryRangeReversed(startIndex, endIndex)//
                .stream().collect(Collectors.toMap(e -> (String) e.getValue(), e -> e.getScore()));
    }
    
    /**
     * 查询排序队列中某个成员的权值
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 成员存在则返回该成员的权值，成员不存在，或者键值不存在则null
     */
    default Double zscore(String key, String member) {
        return getRawClient().getScoredSortedSet(key).getScore(member);
    }
    
    default long strlen(String key) {
        return getRawClient().getBucket(key).size();
    }
}
