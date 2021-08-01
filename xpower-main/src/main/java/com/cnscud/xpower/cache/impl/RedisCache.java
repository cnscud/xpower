package com.cnscud.xpower.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol.Keyword;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

import com.cnscud.xpower.cache.IRedis;
import com.cnscud.xpower.cache.impl.transcoders.CachedData;
import com.cnscud.xpower.cache.impl.transcoders.SerializingTranscoder;

/**
 * Redis的操作实现
 * 
 * @author adyliu (imxylz@gmail.com)
 * @sine 2012-11-08
 */
@Deprecated
public class RedisCache extends BaseCache implements IRedis, RedisCacheMBean {

    private ShardedJedisPool pool;

    final SerializingTranscoder transcoder = new SerializingTranscoder();

    final AtomicInteger maxPoolCount = new AtomicInteger(0);

    final AtomicInteger currentPoolCount = new AtomicInteger(0);

    final AtomicLong totalCallTimes = new AtomicLong();

    /**
     * 为了支持线程安全，被迫使用线程栈
     */
    final ThreadLocal<ShardedJedis> jedisLocal = new ThreadLocal<ShardedJedis>();

    public RedisCache() {

    }

    public RedisCache(ShardedJedisPool pool) {
        this.pool = pool;
    }

    @Override
    public <T> T execute(String key, RedisCallback<T> callback) {
        boolean broken = false;
        try {
            return callback.doWithJedis(getJedis());
        } catch (JedisConnectionException e) {// Redis Connection is broken
            broken = true;// 发生了某个杯具，导致了Redis Connection坏掉了，我们要代表党消灭它
            throw e;
        } finally {
            cleanContext(broken);
        }
    }

    /**
     * 释放资源，将redis链接释放回连接池
     */
    protected void cleanContext(boolean broken) {
        ShardedJedis jedis = jedisLocal.get();
        if (jedis != null) {
            jedisLocal.remove();
            currentPoolCount.decrementAndGet();
            if (pool != null) {
                if (broken) {
                    pool.returnBrokenResource(jedis);
                } else {
                    pool.returnResource(jedis);
                }
            }
        }
    }

    /**
     * 由于jredis不是线程安全的，而且是直接操作Connection的，因此被迫使用连接池以及使用线程栈。
     * 
     * @return Redis客户端
     */
    protected ShardedJedis getJedis() {
        if (pool == null) {
            throw new IllegalStateException("redis client not initilized: " + getId());
        }
        totalCallTimes.incrementAndGet();
        ShardedJedis jedis = jedisLocal.get();// 多次调用上下文中包含了此实例直接获取
        if (jedis != null) {
            return jedis;
        }
        jedis = pool.getResource();
        jedisLocal.set(jedis);
        int c = currentPoolCount.incrementAndGet();
        int max = maxPoolCount.get();
        if (c > max) {// 记录下最大值
            maxPoolCount.compareAndSet(max, c);
        }
        return jedis;
    }

    @Override
    public long decr(final String key, final long delta, long initValue, long timeout, int exp) {
        // FIXME: 需要处理initValue以及过期时间
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.decrBy(key, delta);
            }
        });
    }

    @Override
    public boolean delete(final String key, long opTimeout) {
        return execute(key, new RedisCallback<Boolean>() {

            public Boolean doWithJedis(ShardedJedis jedis) {
                return 1 == jedis.del(key);
            }
        });
    }

    @Override
    public int delete(final Collection<String> keys) {
        return execute(null, new RedisCallback<Integer>() {

            public Integer doWithJedis(ShardedJedis jedis) {
                Map<Jedis, List<String>> serverKeyMap = getJedisKeyMap(jedis, keys);
                long cnt = 0;
                for (Map.Entry<Jedis, List<String>> e : serverKeyMap.entrySet()) {
                    if (e.getValue().size() > 0) {
                        cnt += e.getKey().del(e.getValue().toArray(new String[e.getValue().size()]));
                    }
                }
                return Integer.valueOf((int) cnt);
            }
        });
    }

    private Map<Jedis, List<String>> getJedisKeyMap(ShardedJedis sharedJedis, Collection<String> keys) {
        // 同一批KEY共享同一个SHARDING(jedis客户端），将所有key按照SHARDING分组
        Map<Jedis, List<String>> serverKeys = new HashMap<Jedis, List<String>>();
        for (String key : keys) {
            Jedis info = sharedJedis.getShard(key);
            List<String> list = serverKeys.get(info);
            if (list == null) {
                list = new ArrayList<String>();
                serverKeys.put(info, list);
            }
            list.add(key);
        }
        return serverKeys;
    }

    @Override
    public Map<String, String> getString(final Collection<String> keys) {
        return execute(null, new RedisCallback<Map<String, String>>() {

            @Override
            public Map<String, String> doWithJedis(ShardedJedis jedis) {
                // 同一批KEY共享同一个SHARDING(jedis客户端），将所有key按照SHARDING分组
                Map<JedisShardInfo, List<String>> shardInfos = new HashMap<JedisShardInfo, List<String>>();
                for (String key : keys) {
                    JedisShardInfo info = jedis.getShardInfo(key);
                    List<String> list = shardInfos.get(info);
                    if (list == null) {
                        list = new ArrayList<String>();
                        shardInfos.put(info, list);
                    }
                    list.add(key);
                }
                //
                final Map<String, String> map = new LinkedHashMap<String, String>();
                for (Map.Entry<JedisShardInfo, List<String>> e : shardInfos.entrySet()) {
                    List<String> ks = e.getValue();
                    Jedis rjedis = jedis.getShard(ks.get(0));
                    List<String> rs = rjedis.mget(ks.toArray(new String[ks.size()]));
                    for (int i = 0; i < ks.size(); i++) {
                        String data = rs.get(i);
                        if (data != null) {
                            map.put(ks.get(i), data);
                        }
                    }
                }
                return map;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> get(final Collection<String> keyCollections) {
        Map<String, byte[]> bytesMap = getBytes(keyCollections);
        Map<String, T> map = new LinkedHashMap<String, T>(bytesMap.size());
        for (Map.Entry<String, byte[]> e : bytesMap.entrySet()) {
            map.put(e.getKey(), (T) transcoder.decode(new CachedData(e.getValue())));
        }
        return map;
    }

    @Override
    public boolean setBytes(String key, byte[] data) {
        return setBytes(key, data, 0);
    }

    @Override
    public boolean setBytes(final String key, final byte[] data, final int exp) {
        return execute(key, new RedisCallback<Boolean>() {

            @Override
            public Boolean doWithJedis(ShardedJedis jedis) {
                final byte[] encodeKey = transcoder.encodeString(key);
                String status = null;
                if (exp > 0) {
                    status = jedis.setex(encodeKey, exp, data);
                } else {
                    status = jedis.set(encodeKey, data);
                }
                boolean r = Keyword.OK.name().equalsIgnoreCase(status);
                return r;
            }
        });
    }

    @Override
    public byte[] getBytes(final String key) {
        return execute(key, new RedisCallback<byte[]>() {

            @Override
            public byte[] doWithJedis(ShardedJedis jedis) {
                return jedis.get(transcoder.encodeString(key));
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final String key) {
        return execute(key, new RedisCallback<T>() {

            @Override
            public T doWithJedis(ShardedJedis jedis) {
                byte[] dat = jedis.get(transcoder.encodeString(key));
                if (dat == null || dat.length < 1) {
                    return (T) null;
                }
                return (T) transcoder.decode(new CachedData(dat));
            }
        });
    }

    @Override
    public long incr(final String key, final long delta, long initValue, long timeout, int exp) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.incrBy(key, delta);// FIXME: 处理initValue
            }
        });
    }

    @Override
    public boolean setString(final String key, final String value, final int exp) {
        return execute(key, new RedisCallback<Boolean>() {

            public Boolean doWithJedis(ShardedJedis jedis) {
                String status = jedis.set(key, value);
                boolean r = Keyword.OK.name().equalsIgnoreCase(status);
                if (r && exp > 0) {
                    r &= 1 == jedis.expire(key, exp);
                }
                return r;
            }
        });
    }

    @Override
    public boolean set(final String key, final Object value, long timeout, TimeUnit unit) {
        return execute(key, new RedisCallback<Boolean>() {

            public Boolean doWithJedis(ShardedJedis jedis) {
                String status = "";
                if (timeout > 0) {
                    status = jedis.setex(transcoder.encodeString(key), (int)unit.toSeconds(timeout), transcoder.encode(value).getFullData());
                } else {
                    status = jedis.set(transcoder.encodeString(key), transcoder.encode(value).getFullData());
                }
                // String status = jedis.set(transcoder.encodeString(key), transcoder.encode(value).getFullData());
                boolean r = Keyword.OK.name().equalsIgnoreCase(status);
                // if (r && exp > 0) {
                // r &= 1 == jedis.expire(key, exp);
                // }
                return r;
            }
        });
    }

    @Override
    public long rpush(final String key, final String value) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.rpush(key, value);
            }
        });
    }

    @Override
    public Long lpush(final String key, final String value) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.lpush(key, value);
            }
        });
    }

    public Long lpushx(final String key, final String value) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                Jedis j = jedis.getShard(key);
                return j.lpushx(key, value);
            }
        });
    }

    @Override
    public String lpop(final String key) {
        return execute(key, new RedisCallback<String>() {

            @Override
            public String doWithJedis(ShardedJedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    @Override
    public long ttl(final String key) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    @Override
    public Long zadd(final String key, final double score, final String member) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zadd(key, score, member);
            }
        });
    }

    @Override
    public Long zadd(final String key, final Map<Double, String> scoreMembers) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zadd(key, scoreMembers);
            }
        });
    }

    @Override
    public Double zscore(final String key, final String member) {
        return execute(key, new RedisCallback<Double>() {

            @Override
            public Double doWithJedis(ShardedJedis jedis) {
                return jedis.zscore(key, member);
            }
        });
    }

    @Override
    public Long zrank(final String key, final String member) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zrank(key, member);
            }
        });
    }

    @Override
    public Set<String> zrange(final String key, final int startIndex, final int endIndex) {
        return execute(key, new RedisCallback<Set<String>>() {

            @Override
            public Set<String> doWithJedis(ShardedJedis jedis) {
                return jedis.zrange(key, startIndex, endIndex);
            }
        });
    }

    @Override
    public Map<String, Double> zrangeWithScore(final String key, final int startIndex, final int endIndex) {
        return execute(key, new RedisCallback<Map<String, Double>>() {

            @Override
            public Map<String, Double> doWithJedis(ShardedJedis jedis) {
                Set<Tuple> resultSet = jedis.zrangeWithScores(key, startIndex, endIndex);
                Iterator<Tuple> iterator = resultSet.iterator();
                Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
                while (iterator.hasNext()) {
                    Tuple resultTuple = (Tuple) iterator.next();
                    resultMap.put(resultTuple.getElement(), resultTuple.getScore());
                }
                return resultMap;
            }
        });
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return execute(key, new RedisCallback<Set<String>>() {

            @Override
            public Set<String> doWithJedis(ShardedJedis jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Map<String, Double> zrangeByScoreWithScores(final String key, final double min, final double max) {
        return execute(key, new RedisCallback<Map<String, Double>>() {

            @Override
            public Map<String, Double> doWithJedis(ShardedJedis jedis) {
                Set<Tuple> resultSet = jedis.zrangeByScoreWithScores(key, min, max);
                Iterator<Tuple> iterator = resultSet.iterator();
                Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
                while (iterator.hasNext()) {
                    Tuple resultTuple = (Tuple) iterator.next();
                    resultMap.put(resultTuple.getElement(), resultTuple.getScore());
                }
                return resultMap;
            }
        });
    }

    @Override
    public Map<String, Double> zrevrangeWithScore(final String key, final int startIndex, final int endIndex) {
        return execute(key, new RedisCallback<Map<String, Double>>() {

            @Override
            public Map<String, Double> doWithJedis(ShardedJedis jedis) {
                Set<Tuple> resultSet = jedis.zrevrangeWithScores(key, startIndex, endIndex);
                Iterator<Tuple> iterator = resultSet.iterator();
                Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
                while (iterator.hasNext()) {
                    Tuple resultTuple = (Tuple) iterator.next();
                    resultMap.put(resultTuple.getElement(), resultTuple.getScore());
                }
                return resultMap;
            }
        });
    }

    @Override
    public Long zcard(final String key) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    @Override
    public Long expireAt(final String key, final long unixTime) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.expireAt(key, unixTime);
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return execute(key, new RedisCallback<Boolean>() {

            @Override
            public Boolean doWithJedis(ShardedJedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long zremrangeByRank(final String key, final int start, final int end) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zremrangeByRank(key, start, end);
            }
        });
    }

    @Override
    public void destroy() {
        if (pool != null) {
            cleanContext(true);
            pool.destroy();
            pool = null;
        }
    }

    @Override
    public Long zcount(final String key, final int min, final int max) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zcount(key, min, max);
            }
        });
    }

    @Override
    public Long sadd(final String key, final String member) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.sadd(key, member);
            }
        });
    }

    @Override
    public String hget(final String key, final String member) {
        return execute(key, new RedisCallback<String>() {

            @Override
            public String doWithJedis(ShardedJedis jedis) {
                return jedis.hget(key, member);
            }
        });
    }

    @Override
    public Long hdel(final String key, final String member) {
        return execute(key, new RedisCallback<Long>() {

            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.hdel(key, member);
            }
        });
    }

    @Override
    public Long hincrby(final String key, final String member, final long value) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.hincrBy(key, member, value);
            }
        });
    }

    @Override
    public Long hset(final String key, final String member, final String value) {
        return execute(key, new RedisCallback<Long>() {

            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.hset(key, member, value);
            }
        });
    }

    @Override
    public String hmset(final String key, final Map<String, String> fieldValue) {
        return execute(key, new RedisCallback<String>() {

            public String doWithJedis(ShardedJedis jedis) {
                return jedis.hmset(key, fieldValue);
            }
        });
    }

    @Override
    public String getString(final String key) {
        return execute(key, new RedisCallback<String>() {

            public String doWithJedis(ShardedJedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public boolean setString(final String key, final String value) {
        return execute(key, new RedisCallback<Boolean>() {

            public Boolean doWithJedis(ShardedJedis jedis) {
                String ret = jedis.set(key, value);
                return ret != null && Keyword.OK.name().equalsIgnoreCase(ret);
            }
        });
    }

    /**
     * @param pool
     *            the pool to set
     */
    public void setPool(ShardedJedisPool pool) {
        this.pool = pool;
    }

    // @Override
    // public Long zinterstore(final String key, final String[] keys) {
    // return execute(key, new RedisCallback<Long>() {
    // public Long doWithJedis(ShardedJedis jedis) {
    //
    // Map<JedisShardInfo, List<String>> map = new HashMap<JedisShardInfo, List<String>>();
    // for(String k:keys) {
    // jedis.zrangeWithScores(k, 0, -1);
    // }
    // return jedis.getShard(key).zinterstore(key, keys);
    // }
    // });
    // }

    // @Override
    // public Long zinterstore(final String key, final ZParams opparms, final String[] keys) {
    // return execute(key, new RedisCallback<Long>() {
    // public Long doWithJedis(ShardedJedis jedis) {
    // return jedis.getShard(key).zinterstore(key, opparms, keys);
    // }
    // });
    // }
    //
    // @Override
    // public Long zunionstore(final String key , final String[] keys) {
    // return execute(key, new RedisCallback<Long>() {
    // public Long doWithJedis(ShardedJedis jedis) {
    // return jedis.getShard(key).zunionstore(key, keys);
    // }
    // });
    // }

    // @Override
    // public Long zunionstore(final String key, final ZParams Op_params, final String[] keys) {
    // return execute(key, new RedisCallback<Long>() {
    // public Long doWithJedis(ShardedJedis jedis) {
    // return jedis.getShard(key).zunionstore(key, Op_params, keys);
    // }
    // });
    // }

    @Override
    public Long zrem(final String key, final String member) {
        return execute(key, new RedisCallback<Long>() {

            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zrem(key, member);
            }
        });
    }

    /**
     * @return the currentPoolCount
     */
    public Integer getCurrentPoolCount() {
        return currentPoolCount.get();
    }

    /**
     * @return the maxPoolCount
     */
    public Integer getMaxPoolCount() {
        return maxPoolCount.get();
    }

    @Override
    public Long zremrangeByScore(final String key, final int minScore, final int maxScore) {
        return execute(key, new RedisCallback<Long>() {

            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.zremrangeByScore(key, minScore, maxScore);
            }
        });
    }

    /**
     * @return the totalCallTimes
     */
    public long getTotalCallTimes() {
        return totalCallTimes.get();
    }

    @Override
    public Map<String, byte[]> getBytes(final Collection<String> keys) {
        final Map<String, byte[]> map = new HashMap<String, byte[]>(0);
        if (keys == null || keys.size() == 0) {
            return map;// 允许返回的map可以修改
        }
        return execute(null, new RedisCallback<Map<String, byte[]>>() {

            @Override
            public Map<String, byte[]> doWithJedis(ShardedJedis jedis) {
                // 同一批KEY共享同一个SHARDING(jedis客户端），将所有key按照SHARDING分组
                Map<JedisShardInfo, List<byte[]>> shardInfos = new HashMap<JedisShardInfo, List<byte[]>>();
                for (String key : keys) {
                    byte[] bkey = transcoder.encodeString(key);
                    JedisShardInfo info = jedis.getShardInfo(bkey);
                    List<byte[]> list = shardInfos.get(info);
                    if (list == null) {
                        list = new ArrayList<byte[]>();
                        shardInfos.put(info, list);
                    }
                    list.add(bkey);
                }
                //
                for (Map.Entry<JedisShardInfo, List<byte[]>> e : shardInfos.entrySet()) {
                    List<byte[]> ks = e.getValue();
                    Jedis rjedis = jedis.getShard(ks.get(0));
                    List<byte[]> rs = rjedis.mget(ks.toArray(new byte[ks.size()][]));
                    for (int i = 0; i < ks.size(); i++) {
                        byte[] data = rs.get(i);
                        if (data != null) {
                            map.put(transcoder.decodeString(ks.get(i)), data);
                        }
                    }
                }
                return map;
            }
        });
    }

    @Override
    public List<String> setBytes(final Map<String, byte[]> keyDatas) {
        if (keyDatas == null || keyDatas.keySet().size() == 0) {
            return new ArrayList<String>();
        }
        return execute(null, new RedisCallback<List<String>>() {

            @Override
            public List<String> doWithJedis(ShardedJedis jedis) {
                ArrayList<String> successKeys = new ArrayList<String>();
                // 同一批KEY共享同一个SHARDING(jedis客户端），将所有key按照SHARDING分组
                Map<JedisShardInfo, List<byte[]>> shardInfos = new HashMap<JedisShardInfo, List<byte[]>>();
                for (String key : keyDatas.keySet()) {
                    byte[] bkey = transcoder.encodeString(key);
                    JedisShardInfo info = jedis.getShardInfo(bkey);
                    List<byte[]> list = shardInfos.get(info);
                    if (list == null) {
                        list = new ArrayList<byte[]>();
                        shardInfos.put(info, list);
                    }
                    list.add(bkey);
                }
                //
                for (Map.Entry<JedisShardInfo, List<byte[]>> e : shardInfos.entrySet()) {
                    List<byte[]> ks = e.getValue();
                    Jedis rjedis = jedis.getShard(ks.get(0));
                    String replyCode = rjedis.mset(getKeysValuesByMap(keyDatas, ks));
                    if (replyCode.equalsIgnoreCase("ok")) {
                        successKeys.addAll(getStringByBytes(ks));
                    }
                }
                return successKeys;
            }
        });
    }

    private byte[][] getKeysValuesByMap(Map<String, byte[]> keyDatas, List<byte[]> subKeys) {
        List<byte[]> values = new ArrayList<byte[]>();
        for (byte[] key : subKeys) {
            values.add(key);
            values.add(keyDatas.get(transcoder.decodeString(key)));
        }
        return values.toArray(new byte[][] {});
    }

    private List<String> getStringByBytes(List<byte[]> bytes) {
        List<String> keys = new ArrayList<String>();
        for (byte[] one : bytes) {
            keys.add(transcoder.decodeString(one));
        }
        return keys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#lrange(java.lang.String, int, int)
     */
    @Override
    public List<String> lrange(final String key, final long startIndex, final long endIndex) {
        return execute(key, new RedisCallback<List<String>>() {

            @Override
            public List<String> doWithJedis(ShardedJedis jedis) {
                return jedis.lrange(key, startIndex, endIndex);
            }
        });
    }

    @Override
    public Long lpush(final String key, final String... values) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                byte[][] byteValues = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    byteValues[i] = SafeEncoder.encode(values[i]);
                }
                return jedis.lpush(SafeEncoder.encode(key), byteValues);
            }
        });
    }

    @Override
    public long rpush(final String key, final String[] values) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                byte[][] byteValues = new byte[values.length][];
                for (int i = 0; i < values.length; i++) {
                    byteValues[i] = SafeEncoder.encode(values[i]);
                }
                return jedis.rpush(SafeEncoder.encode(key), byteValues);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#llen(java.lang.String)
     */
    @Override
    public long llen(final String key) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#lrem(java.lang.String, int, java.lang.String)
     */
    @Override
    public long lrem(final String key, final long count, final String value) {
        return execute(key, new RedisCallback<Long>() {

            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.lrem(key, count, value);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#rpop(java.lang.String)
     */
    @Override
    public String rpop(final String key) {
        return this.execute(key, new RedisCallback<String>() {

            @Override
            public String doWithJedis(ShardedJedis jedis) {
                return jedis.rpop(key);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#ltrim(java.lang.String, long, long)
     */
    @Override
    public String ltrim(final String key, final long start, final long end) {
        return this.execute(key, new RedisCallback<String>() {

            @Override
            public String doWithJedis(ShardedJedis jedis) {
                return jedis.ltrim(key, start, end);
            }
        });
    }

    @Override
    public Map<String, String> hmget(final String key, final Collection<String> fields) {
        return execute(key, new RedisCallback<Map<String, String>>() {

            @Override
            public Map<String, String> doWithJedis(ShardedJedis jedis) {
                String[] sfields = fields.toArray(new String[fields.size()]);
                List<String> list = jedis.hmget(key, sfields);
                Map<String, String> map = new HashMap<String, String>(sfields.length);
                for (int i = 0; i < sfields.length; i++) {
                    String value = list.get(i);
                    if (value != null) {
                        map.put(sfields[i], value);
                    }
                }
                return map;
            }
        });
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        return execute(key, new RedisCallback<Map<String, String>>() {

            @Override
            public Map<String, String> doWithJedis(ShardedJedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sohu.suc.platform.core.cache.IRedis#mset(java.util.Map)
     */
    @Override
    public List<String> mset(Map<String, String> keyDatas) {
        Map<String, byte[]> keybyteMap = new HashMap<String, byte[]>();
        for (Iterator<Entry<String, String>> i = keyDatas.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            keybyteMap.put(entry.getKey(), transcoder.encodeString(entry.getValue()));
        }
        return this.setBytes(keybyteMap);
    }

    public <T> List<String> msetObject(Map<String, T> keyDatas) {
        Map<String, byte[]> keybyteMap = new HashMap<String, byte[]>();
        for (Iterator<Entry<String, T>> i = keyDatas.entrySet().iterator(); i.hasNext();) {
            Entry<String, T> entry = i.next();
            keybyteMap.put(entry.getKey(), transcoder.encode(entry.getValue()).getFullData());
        }
        return this.setBytes(keybyteMap);
    }

    @Override
    public Double zincrby(final String key, final double score, final String member) {
        return execute(key, new RedisCallback<Double>() {

            @Override
            public Double doWithJedis(ShardedJedis jedis) {
                return jedis.zincrby(key, score, member);
            }
        });
    }

    @Override
    public Long hlen(final String key) {
        return execute(key, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    @Override
    public boolean hexists(final String key, final String field) {
        return execute(key, new RedisCallback<Boolean>() {
            @Override
            public Boolean doWithJedis(ShardedJedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    @Override
    public Set<String> hkeys(final String key) {
        return execute(key, new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doWithJedis(ShardedJedis jedis) {
                return jedis.hkeys(key);
            }
        });

    }

    @Override
    public List<String> hvalues(final String key) {
        return execute(key, new RedisCallback<List<String>>() {
            @Override
            public List<String> doWithJedis(ShardedJedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    @Override
    public Long lpush(final String key, final byte[]... bytes) {
        return execute(null, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.lpush(transcoder.encodeString(key), bytes);
            }
        });
    }

    @Override
    public byte[] lpop(final byte[] key) {
        return execute(null, new RedisCallback<byte[]>() {
            @Override
            public byte[] doWithJedis(ShardedJedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    @Override
    public List<byte[]> lrange(final byte[] key, final int startIndex, final int endIndex) {
        return execute(null, new RedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> doWithJedis(ShardedJedis jedis) {
                return jedis.lrange(key, startIndex, endIndex);
            }
        });
    }

    @Override
    public byte[] hget(final byte[] key, final String member) {
        return execute(null, new RedisCallback<byte[]>() {
            @Override
            public byte[] doWithJedis(ShardedJedis jedis) {
                return jedis.hget(key, transcoder.encodeString(member));
            }
        });
    }

    @Override
    public Long hset(final String key, final String member, final byte[] value) {
        return execute(null, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.hset(transcoder.encodeString(key), transcoder.encodeString(member), value);
            }
        });
    }

    @Override
    public Long rpush(final String key, final byte[]... bytes) {
        return execute(null, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.rpush(transcoder.encodeString(key), bytes);
            }
        });
    }

    @Override
    public Long linsert(final String key, final String where, final String pivot, final String value) {

        return execute(null, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.linsert(key, Client.LIST_POSITION.valueOf(where), pivot, value);
            }
        });
    }

    @Override
    public String blpop(final int timeout, final String key) {
        return execute(null, new RedisCallback<String>() {
            @Override
            public String doWithJedis(ShardedJedis jedis) {
                Jedis j = jedis.getShard(key);
                List<String> list = j.blpop(timeout, key);
                return list == null ? null : list.get(1);
            }
        });
    }

    @Override
    public String brpop(final int timeout, final String key) {
        return execute(null, new RedisCallback<String>() {
            @Override
            public String doWithJedis(ShardedJedis jedis) {
                Jedis j = jedis.getShard(key);
                List<String> list = j.brpop(timeout, key);
                return list == null ? null : list.get(1);
            }
        });
    }

    @Override
    public Set<String> zrevrange(final String key, final int startIndex, final int endIndex) {
        return execute(key, new RedisCallback<Set<String>>() {

            @Override
            public Set<String> doWithJedis(ShardedJedis jedis) {
                return jedis.zrevrange(key, startIndex, endIndex);
            }
        });
    }

    @Override
    public List<String> zrevrangeList(final String key, final int startIndex, final int endIndex) {
        Set<String> result = zrevrange(key, startIndex, endIndex);
        return new ArrayList<String>(result);
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        return this.execute(key, new RedisCallback<Boolean>() {
            @Override
            public Boolean doWithJedis(ShardedJedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    @Override
    public Set<String> smembers(final String key) {
        return this.execute(key, new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doWithJedis(ShardedJedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    @Override
    public String srandmember(final String key) {
        return this.execute(key, new RedisCallback<String>() {
            @Override
            public String doWithJedis(ShardedJedis jedis) {
                return jedis.srandmember(key);
            }
        });
    }

    @Override
    public Long scard(final String key) {
        return this.execute(key, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    @Override
    public Long sadd(final String key, final long exp, final String... members) {
        return this.execute(key, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                jedis.sadd(key, members);
                return jedis.expire(key, (int) exp);
            }
        });
    }

    @Override
    public Long sadd(final String key, final String... members) {
        return this.execute(key, new RedisCallback<Long>() {
            @Override
            public Long doWithJedis(ShardedJedis jedis) {
                return jedis.sadd(key, members);
            }
        });
    }

}