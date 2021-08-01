package com.cnscud.xpower.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.ShardedJedis;

/**
 * Redis 操作接口
 * @deprecated
 * @see IRedisCluster
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-08
 */
public interface IRedis extends ICache {

    /**
     * 获取二进制字节
     * 
     * @param key
     *            键值
     * @return byte数组或者null
     */
    byte[] getBytes(String key);

    /**
     * 批量获取二进制字节
     * 
     * @param keys
     *            键值
     * @return 二进制字节数组
     */
    Map<String, byte[]> getBytes(Collection<String> keys);

    /**
     * 持久化二进制字节
     * 
     * @param key
     *            键值
     * @param data
     *            数据
     * @return 是否持久化成功
     */
    boolean setBytes(String key, byte[] data);

    /**
     * 批量持久化二进制字节 不支持设置过期时间 不管keys 存在不存在都会把原来的覆盖
     * 
     * @param keyDatas
     * @return 成功set的keys
     */
    List<String> setBytes(Map<String, byte[]> keyDatas);

    List<String> mset(Map<String, String> keyDatas);

    <T> List<String> msetObject(Map<String, T> keyDatas);

    /**
     * 持久化二进制字节
     * <p>
     * 这与{@link #set(String, int, Object, long)}不同，set会在cache的数据中写入标志位， 表示一个字节数组；而setBytes不会写入标志位，写入的是原生的字节数组。这在跨语言、跨平台、跨客户端上 非常有用
     * </p>
     * 
     * @param key
     *            键值
     * @param data
     *            数据
     * @param exp
     *            过期时间，单位秒
     * @return 是否持久化成功
     */
    boolean setBytes(String key, byte[] data, int exp);

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
    String getString(String key);

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
     * @return 是否操作成功
     */
    boolean setString(String key, String value);

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
    boolean setString(String key, String value, int exp);

    /**
     * 根据特定的key列表获取集合列表
     * 
     * @param keys
     *            key列表
     * @return 集合列表
     */
    Map<String, String> getString(Collection<String> keys);

    /**
     * 返回一个key的过期时间
     * 
     * @param key
     *            键值
     * @return 过期时间（秒）或者-1，如果key不存在
     */
    long ttl(String key);

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
    Long lpush(String key, String value);

    /**
     * 向列表头添加一个值,如果不存在则不添加
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return 添加后的列表大小
     */
    Long lpushx(final String key, final String value);

    Long lpush(String key, String... values);

    Long lpush(String key, byte[]... bytes);

    /**
     * 队列末尾添加一个值
     * 
     * @param key
     *            键值
     * @param value
     *            添加的值
     * @return 队列添加后长度
     */
    long rpush(String key, String value);

    /**
     * 批量队列末尾添加一个值
     * 
     * @param key
     *            键值
     * @param values
     *            添加的值
     * @return 队列添加后长度
     */
    long rpush(String key, String[] values);

    Long rpush(String key, byte[]... bytes);

    /**
     * 从队列头部取值
     * 
     * @param key
     *            键值
     * @return 从队列中取出的值
     */
    public String lpop(String key);

    public byte[] lpop(byte[] key);

    /**
     * 使用Redis客户端执行操作，不建议使用<br/>
     * redis的java客户端性能不佳，有可能被替换，因此不建议使用原生的客户端
     * 
     * @param key
     *            要操作的key，如果有散列，散列方法需要一个key
     * @param callback
     *            回调函数
     */
    <T> T execute(String key, RedisCallback<T> callback);

    /**
     * Redis回调操作
     * 
     * @param <T>
     * @author adyliu (adyliu@sohu-inc.com)
     * @since 2011-8-19
     */
    public interface RedisCallback<T> {

        T doWithJedis(ShardedJedis jedis);
    }

    /**
     * 设置有效期
     * 
     * @param key
     *            键值
     * @param seconds
     *            单位为：秒
     * @return
     */
    Long expire(String key, int seconds);

    /**
     * 设置有效期
     * 
     * @param key
     *            键值
     * @param seconds
     *            it takes an absolute one in the form of a UNIX timestamp (Number of seconds elapsed since 1 Gen 1970).
     * @return
     */
    Long expireAt(String key, long unixTime);

    /**
     * 判断指定的key是否存在
     * 
     * @param key
     *            键值
     * @return true or false
     */
    public Boolean exists(String key);

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
    Long zadd(String key, double score, String member);

    Long zadd(String key, Map<Double, String> scoreMembers);

    /**
     * 查询排序队列中某个成员的排名（名次）
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 成员存在则返回该成员的排名，成员不存在，或者键值不存在则null
     */
    Long zrank(String key, String member);

    /**
     * 查询排序队列中某个成员的权值
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 成员存在则返回该成员的权值，成员不存在，或者键值不存在则null
     */
    Double zscore(String key, String member);

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
    Double zincrby(final String key, final double score, final String member);

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
    Set<String> zrange(String key, int startIndex, int endIndex);

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
    Map<String, Double> zrangeWithScore(String key, int startIndex, int endIndex);

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
    Set<String> zrangeByScore(String key, double min, double max);

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
    Map<String, Double> zrangeByScoreWithScores(String key, double min, double max);

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
    Map<String, Double> zrevrangeWithScore(String key, int startIndex, int endIndex);

    /**
     * 查询排序队列中元素的个数
     * 
     * @param key
     *            键值
     * @return 队列中元素的个数
     */
    Long zcard(String key);

    /**
     * 从排序队列中删除元素 排序为从小到大，最小的位置最靠前
     * 
     * @param key
     *            键值
     * @param start
     *            开始位置
     * @param end
     *            结束位置
     * @return 删除的元素个数
     */
    public Long zremrangeByRank(String key, int start, int end);

    /**
     * 返回指定有序序列权值范围内元素的个数
     * 
     * @param key
     *            键值
     * @param min
     *            最大权值
     * @param max
     *            最小权值
     * @return Long 元素个数
     */
    Long zcount(String key, int min, int max);

    /**
     * 对给出的序列求交集，并将交集的结果存放在destkey中
     * 
     * @param destKey
     *            目标key，存放结果用,元素在结果集中的score是其在各个源集合score的和
     * @param keys
     *            源key集合，可为2个或多个，用于做求交集运算
     * @return 返回运算结果集中的元素个数
     */
    // Long zinterstore(String destKey, String[] keys);

    /**
     * 对给出的序列求并集，并将交集的结果存放在destkey中
     * 
     * @param destKey
     *            目标key，存放结果用,元素在结果集中的score是其在各个源集合score的和
     * @param keys
     *            源key集合，可为2个或多个，用于做求并集运算
     * @return 返回运算结果集中的元素个数
     */
    // Long zunionstore(String destKey, String[] keys);

    /**
     * 对给出的序列求交集，并将交集的结果存放在destkey中
     * 
     * @param destKey
     *            目标key，存放结果用
     * @param ZParams
     *            Op_params 操作参数 Zparams 设置运算类型：MAX，MIN，SUM，默认的AGGREGATE是SUM 设置集合权重：Weight，默认为1 ------------------------------------------------- Zparam的作用：
     *            1、权重，对于每个集合赋予一个权重，在进行交集运算后，对该集合中落入结果集的元素的Score进行乘运算， 并以乘运算的结果作为进行最终运算的基数 2、类型，对落入结果集的元素进行计算，有求和，最小值，最大值三种，计算结果作为结果集中元素的Score 比较绕口，看例子 集合状态：
     * 
     *            <pre>
     * redis>  ZADD zset1 1 "one"                           zset1：{one:1}
     * redis>  ZADD zset1 2 "two"                           zset1：{one:1,two:2}
     * redis>  ZADD zset2 1 "one"                           zset2: {one:1}                  zset1：{one:1,two:2}
     * redis>  ZADD zset2 2 "two"                           zset2: {one:1,two:2}            zset1：{one:1,two:2}
     * redis>  ZADD zset2 3 "three"                         zset2: {one:1,two:2,three:3}    zset1：{one:1,two:2}
     * redis>  ZINTERSTORE out 2 zset1 zset2 WEIGHTS 2 3          求交集，结果集为out，参与运算的有2个集合，分别为zset1，zset2,权重分别为，zset1*2 zset2*3
     * redis>  ZRANGE out 0 -1 WITHSCORES                         元素计算：结果集元素{one,two}  权重计算:zset1：{one:1*2,two:2*2} zset2：{one:1*3,two:2*3}
     *     1) "one"                                               计算结果:out:{one:1*2+1*3=5,two:2*2+2*3=19}
     *  2) "5"
     *  3) "two"
     *  4) "10"
     * </pre>
     * @param keys
     *            源key集合，可为2个或多个，用于做求交集运算
     * @return 返回运算结果集中的元素个数
     */
    // Long zinterstore(String destKey, ZParams zparams, String[] keys);

    /**
     * 对给出的序列求并集，并将交集的结果存放在destkey中
     * 
     * @param destKey
     *            目标key，存放结果用
     * @param ZParams
     *            Op_params 操作参数 Zparams 设置运算类型：MAX，MIN，SUM，默认的AGGREGATE是SUM 设置集合权重：Weight，默认为1 ------------------------------------------------- Zparam的作用：
     *            1、权重，对于每个集合赋予一个权重，在进行交集运算后，对该集合中落入结果集的元素的Score进行乘运算， 并以乘运算的结果作为进行最终运算的基数 2、类型，对落入结果集的元素进行计算，有求和，最小值，最大值三种，计算结果作为结果集中元素的Score 比较绕口，看例子 集合状态：
     *            redis> ZADD zset1 1 "one" zset1：{one:1} redis> ZADD zset1 2 "two" zset1：{one:1,two:2} redis> ZADD zset2 1 "one" zset2: {one:1}
     *            zset1：{one:1,two:2} redis> ZADD zset2 2 "two" zset2: {one:1,two:2} zset1：{one:1,two:2} redis> ZADD zset2 3 "three" zset2:
     *            {one:1,two:2,three:3} zset1：{one:1,two:2} redis> ZINTERSTORE out 2 zset1 zset2 WEIGHTS 2 3 求交集，结果集为out，参与运算的有2个集合，分别为zset1，zset2,权重分别为，zset1*2
     *            zset2*3 redis> ZRANGE out 0 -1 WITHSCORES 元素计算：结果集元素{one,two,three} 权重计算:zset1：{one:1*2,two:2*2} zset2：{one:1*3,two:2*3,three:3*3} 1) "one"
     *            计算结果:out:{one:1*2+1*3=5,two:2*2+2*3=19,three:2*0+3*3} 2) "5" 3) "two" 4) "10" 5) "three" 6) "9"
     * @param keys
     *            源key集合，可为2个或多个，用于做求并集运算
     * @return 返回运算结果集中的元素个数
     */
    // Long zunionstore(String destKey, ZParams zparams, String[] keys);

    /**
     * 从指定的序列中删除某一个元素
     * 
     * @param key
     *            序列名
     * @param member
     *            被删除的元素名
     * @return 返回被删除的元素数, 删除失败返回0
     */
    Long zrem(String key, String member);

    /**
     * 删除名称为key的zset中score >= min且score <= max的所有元素
     * 
     * @param key
     *            序列名
     * @param minScore
     * @param maxScore
     * @return 被删除的元素个数
     */
    Long zremrangeByScore(String key, int minScore, int maxScore);

    /**
     * 返回Hash中当前对应member的Value
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return 字符串值，不存在返回null
     */
    String hget(String key, String member);

    byte[] hget(byte[] key, String member);

    /**
     * 删除hash中指定的成员
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return
     */
    Long hdel(String key, String member);

    /**
     * 返回整个hash
     * 
     * @param key
     *            键值
     * @return
     */
    Map<String, String> hgetAll(String key);

    /**
     * 获取一个key对应HASH字段的所有值
     * 
     * @param key
     *            键
     * @param fields
     *            对应字段
     * @return 对应字段的所有制，如果没有则不在此列表中
     * @author adyliu (imxylz@gmail.com)
     * @since 2012-2-13
     */
    Map<String, String> hmget(String key, Collection<String> fields);

    /**
     * 增加Hash中相应member的value值
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @param value
     *            增量
     * @return
     */
    Long hincrby(String key, String member, long value);

    /**
     * 向指定的Hash中插入成员
     * 
     * @param key
     *            键值
     * @param member
     *            成员
     * @return
     * @parma value 初始值
     */
    Long hset(String key, String member, String value);

    Long hset(String key, String member, byte[] value);

    /**
     * hash 的批量set方法
     * 
     * @param key
     * @param fieldValue
     * @return
     */
    String hmset(String key, Map<String, String> fieldValue);

    Long hlen(String key);

    boolean hexists(String key, String field);

    Set<String> hkeys(String key);

    List<String> hvalues(String key);

    /**
     * 向指定的Set中插入元素
     * 
     * @param key
     *            键值
     * @param member
     *            元素
     * @return 1成功插入 0元素已存在
     */
    Long sadd(String key, String member);

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
    List<String> lrange(String key, long startIndex, long endIndex);

    List<byte[]> lrange(byte[] key, int startIndex, int endIndex);

    /**
     * 获取list的长度
     * 
     * @param key
     *            键值
     * @return 对应list的长度, 没有返回0
     */
    long llen(String key);

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
    long lrem(String key, long count, String value);

    /**
     * 右边移除元素
     * 
     * @param key
     *            键值
     * @return 被移除的元素
     */
    String rpop(String key);

    /**
     * 截取list,会包括结束的索引
     * 
     * @param key
     *            键值
     * @param start
     *            开始值
     * @param end
     *            结束值
     * @return 如果成功返回OK
     */
    String ltrim(String key, long start, long end);

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
    Long linsert(String key, String where, String pivot, String value);

    /**
     * 从队列的左侧弹出 ，阻塞弹出。 现在只支持单一的key。不支持多个key同时操作。
     * 
     * @param timeout
     *            timeout(单位秒)为0时一直阻塞，直到有值时返回。 timeout>0时,在timeout时间内如果有值时返回。 timeout>0时,在timeout时间后如果还是没有值时返回null。
     * @param key
     *            键值
     * @return 如果队列为空或者超时没有结果则返回null
     * @see #lpop(String)
     * 
     */
    String blpop(final int timeout, final String key);

    /**
     * 从队列的右侧侧弹出 ，阻塞弹出。 现在只支持单一的key。不支持多个key同时操作。
     * 
     * @param timeout
     *            (单位秒) 为0时一直阻塞，直到有值时返回。 timeout>0时,在timeout时间内如果有值时返回。 timeout>0时,在timeout时间后如果还是没有值时返回null。
     * @param key
     *            键值
     * @return 如果队列为空或者超时没有结果则返回null
     * @see #rpop(String)
     */
    String brpop(final int timeout, final String key);

    /**
     * 查询排序队列中指定范围startIndex，endIndex的元素,排序值从大到小
     * 
     * @param key
     *            键值
     * @param startIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 包含符合查询条件的元素的List<元素> 按照权值从大到小返回
     */
    Set<String> zrevrange(String key, int startIndex, int endIndex);

    /**
     * 查询排序队列中指定范围startIndex，endIndex的元素,排序值从大到小
     * 
     * @param key
     *            键值
     * @param startIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 包含符合查询条件的元素的List<元素> 按照权值从大到小返回
     */
    List<String> zrevrangeList(String key, int startIndex, int endIndex);

    /**
     * 批量添加成员到一个集合中
     * 
     * @param key
     *            缓存中的键值
     * @param members
     *            要加入的成员
     * @return 成功添加的个数
     */
    Long sadd(String key, String... members);

    /**
     * 判断key对应的set集合中,是否有指定的member成员
     * 
     * @param key
     *            set集合再缓存中的键值
     * @param member
     *            需要判断是否存在的成员
     * @return true:存在,false:不存在
     */
    Boolean sismember(String key, String member);

    /**
     * 获取指定key对应的set集合中的所有数据
     * 
     * @param key
     *            要指定的键值
     * @return 集合中的所有数据
     */
    Set<String> smembers(String key);

    /**
     * 随机获取指定set结合中的一个键
     * 
     * @param key
     *            要指定的键值
     * @return 获取到的键值
     */
    String srandmember(String key);

    /**
     * 批量添加成员到一个集合中
     * 
     * @param key
     *            缓存中的键值
     * @param members
     *            要加入的成员
     * @return 成功添加的个数 * @return 成功添加的个数
     */
    Long sadd(String key, long exp, String... members);

    /**
     * 获取指定集合的元素数量
     * 
     * @param key
     *            缓存中的键值 * @return 指定set中的元素数量
     */
    Long scard(String key);
}