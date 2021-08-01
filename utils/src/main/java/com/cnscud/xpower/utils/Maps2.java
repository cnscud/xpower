package com.cnscud.xpower.utils;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.math.NumberUtils.toDouble;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map 工具类
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013年4月10日
 */
@SuppressWarnings("unchecked")
public final class Maps2 {
    /**
     * 将一个Map按照指定的Key顺序重新排序<br/>
     * map中的所有key应该都在orders中
     * 
     * @param map
     *            原始Map
     * @param orders
     *            需要排序的Key的顺序
     * @return 新的有序Map，实际上是一个{@link LinkedHashMap}
     */
    public static <K, V> Map<K, V> sort(Map<K, V> map, Collection<K> orders) {
        if (orders.isEmpty())
            return Collections.emptyMap();
        Map<K, V> ret = new LinkedHashMap<K, V>(orders.size());
        for (K k : orders) {
            V v = map.get(k);
            if (v != null) {
                ret.put(k, v);
            }
        }
        return ret;
    }

    /**
     * 将一个Map的值按照指定的Key顺序重新排序<br/>
     * map中的所有key应该都在orders中
     * 
     * @param map
     *            原始Map
     * @param orders
     *            需要排序的Key的顺序
     * @return 新的有序集合，实际上是一个{@link List}
     */
    public static <K, V> List<V> sortValue(Map<K, V> map, Collection<K> orders) {
        if (orders == null || orders.isEmpty())
            return Collections.emptyList();
        List<V> list = new ArrayList<V>();
        for (K k : orders) {
            V v = map.get(k);
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }

    /**
     * 将一个Map的所有key转换成字符串形式的Key（FreeMarker的变态设计)
     * 
     * @param map
     * @return
     */
    public static <K, V> Map<String, V> convertMapForFreeMarker(Map<K, V> map) {
       return convertMapForFreeMarker(map, false);
    }

    /**
     * 将一个Map的所有key转换成字符串形式的Key（FreeMarker的变态设计)
     *
     * @param map
     * @return
     */
    public static <K, V> Map<String, V> convertMapForFreeMarker(Map<K, V> map, boolean orderMap) {
        if (map.isEmpty())
            return Collections.emptyMap();
        Map<String, V> ret;
        if (orderMap) {
            ret = new LinkedHashMap<>();
        } else {
            ret = new HashMap<>(map.size());

        }
        for (Map.Entry<K, V> e : map.entrySet()) {
            ret.put(e.getKey().toString(), e.getValue());
        }
        return ret;
    }

    /**
     * 从Map的多个key中读取键值，只要存在某个key就解析对应的数字
     * 
     * @param map
     *            map对象
     * @param defaultValue
     *            默认值，如果key不存在或者非数字值
     * @param keys
     *            键的名称
     * @return 数字值
     */
    public static int get(Map<String, ?> map, int defaultValue, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return toInt(valueOf(value), defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * 从Map的多个key中读取键值，只要存在某个key就解析对应的数字
     * 
     * @param map
     *            map对象
     * @param defaultValue
     *            默认值，如果key不存在或者非数字值
     * @param keys
     *            键的名称
     * @return 数字值
     */
    public static double get(Map<String, ?> map, double defaultValue, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return toDouble(valueOf(value), defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * 从Map的多个key中读取键值，只要存在某个key就解析对应的数字
     * 
     * @param map
     *            map对象
     * @param defaultValue
     *            默认值，如果key不存在或者非数字值
     * @param keys
     *            键的名称
     * @return 数字值
     */
    public static long get(Map<String, ?> map, long defaultValue, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return toLong(valueOf(value), defaultValue);
            }
        }
        return defaultValue;
    }
    public static boolean get(Map<String, ?> map, boolean defaultValue, String... keys) {
        String dv = get(map, String.valueOf(defaultValue), keys);
        return Boolean.getBoolean(dv);
    }
    /**
     * 从Map的多个key中读取键值，只要存在某个key就解析对应的字符串
     * 
     * @param map
     *            map对象
     * @param defaultValue
     *            默认值，如果key不存在
     * @param keys
     *            键的名称
     * @return 字符串值
     */
    public static String get(Map<String, ?> map, String defaultValue, String... keys) {
        if (map == null || map.isEmpty()) {
            return defaultValue;
        }
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return defaultValue;
    }

    public static <T> List<T> getList(Map<String, ?> map, String key) {
        return getList(map, true, key);
    }

    public static <T> List<T> getList(Map<String, ?> map, boolean emptyList, String key) {
        return getList(map, (List<T>) (emptyList ? Collections.EMPTY_LIST : null), key);
    }

    public static <T> List<T> getList(Map<String, ?> map, List<T> defaultValue, String key) {
        return getObject(map, defaultValue, key);
    }

    public static <T> T getObject(Map<String, ?> map, T defaultValue, String key) {
        if (!isEmpty(map)) {
            Object v = map.get(key);
            if (v != null) {
                return (T) v;
            }
        }
        return defaultValue;
    }

    public static Map<String, ?> getMap(Map<String, ?> map, String key) {
        return getMap(map, true, key);
    }

    public static Map<String, ?> getMap(Map<String, ?> map, boolean emptyMap, String key) {
        return getMap(map, (Map<String, ?>) (emptyMap ? Collections.EMPTY_MAP : null), key);
    }

    public static Map<String, ?> getMap(Map<String, ?> map, Map<String, ?> defaultValue, String key) {
        return getObject(map, defaultValue, key);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 构造一个纯字符串的有序map
     * 
     * @param kv
     *            偶数个数的字符数列表
     * @return 有序字符串map
     * @since 2014年8月12日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LinkedHashMap<String, String> create(String... kv) {
        if (kv == null || kv.length % 2 != 0) {
            throw new IllegalArgumentException("ERROR KEY OR VALUE");
        }
        final int len = kv.length;
        LinkedHashMap<String, String> m = new LinkedHashMap<>(len / 2);
        for (int i = 0; i < len - 1; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }
    
    /**
     * 根据一个集合创建一个Map
     * @param vs 集合对象
     * @param mapper 集合值映射成key
     * @return LinkedHashMap
     */
    public static <K,V> LinkedHashMap<K, V> create(Collection<V> vs, Function<? super V, ? extends K> mapper){
        LinkedHashMap<K, V> m = new LinkedHashMap<>(vs.size());
        for(V v: vs) {
            m.put(mapper.apply(v), v);
        }
        return m;
    }

    /**
     * 创建key为字符串的有序Map
     * 
     * @param kv
     *            key 和 value
     * @return 有序map
     * @since 2020-01-06
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LinkedHashMap<String, Object> of(Object... kv) {
        if (kv == null || kv.length % 2 != 0) {
            throw new IllegalArgumentException("KV不成对");
        }
        final int len = kv.length;
        LinkedHashMap<String, Object> m = new LinkedHashMap<>(len / 2);
        for (int i = 0; i < len - 1; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }
}
