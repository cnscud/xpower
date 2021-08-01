/**
 * 
 */
package com.cnscud.xpower.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;


/**
 * List 工具集合
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013年4月10日
 */
public final class Lists2 {

    final static Random random = new Random();

    public static int[] _int(Map<String, Object> map, String... names) {
        int[] values = new int[names.length];
        for (int i = 0; i < names.length; i++) {
            values[i] = _int(map.get(names[i]));
        }
        return values;
    }

    public static int _int(Object s) {
        return NumberUtils.toInt("" + s, 0);
    }

    /**
     * 随机获取列表中的一个元素
     * 
     * @param views
     *            列表
     * @return 随机元素或者null
     */
    public static <T> T random(List<T> views) {
        if (views == null || views.size() == 0) {
            return null;
        }
        return views.get(random.nextInt(views.size()));
    }

    /**
     * 随机获取子列表（伪随机算法）
     * <p>
     * 目标是从N个元素随机选出M个子元素(0 &lt;M &lt; N), 保证每个元素的机会均等。<br>
     * 算法是：随机选择一个数K(0 &lt;= K &lt; N)，每次选择K的左右各一个元素，直到都达到边界或者获取到M个元素中止
     * </p>
     * 
     * @param views
     *            集合
     * @param max
     *            结果集最多数量
     * @return 子集合
     * @since 2013年8月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static <T> List<T> randomList(List<T> views, int max) {
        if (views == null || views.size() == 0 || max <= 0) {
            return new ArrayList<T>(0);
        }
        final int size = views.size();
        int index = random.nextInt(size);
        //
        List<T> ret = new ArrayList<T>(max);
        int low = index - 1, high = index;
        while (max > 0 && (low >= 0 || high < size)) {
            if (low >= 0 && max-- > 0) {
                ret.add(views.get(low));
            }
            if (high < size && max-- > 0) {
                ret.add(views.get(high));
            }
            low--;
            high++;
        }
        return ret;
    }

    public static List<String> splitWithTrim(String s) {
        return splitWithTrim(s, ",");
    }

    public static List<String> splitWithTrim(String s, String sep) {
        if (StringUtils.isBlank(s)) {
            return new ArrayList<String>(0);
        }
        String[] arr = StringUtils.split(s, sep);
        List<String> ret = new ArrayList<String>(arr.length);
        for (String a : arr) {
            if (StringUtils.isNotBlank(a)) {
                ret.add(a.trim());
            }
        }
        return ret;
    }

    public static int[] toIntArray(String numbers) {
        return ArrayUtils.toPrimitive(toIntegerList(numbers).toArray(new Integer[0]));
    }

    public static List<Integer> toIntegerList(Collection<String> numbers) {
        return toIntegerList(numbers, false);
    }

    public static List<Integer> toIntegerList(Collection<String> numbers, boolean ignoreError) {
        if (numbers == null || numbers.isEmpty())
            return new ArrayList<Integer>(0);
        List<Integer> ret = new ArrayList<Integer>(numbers.size());
        for (String number : numbers) {
            try {
                number = number.trim();
                Integer v = Integer.valueOf(number);
                ret.add(v);
            } catch (NumberFormatException ex) {
                if (!ignoreError) {
                    throw ex;
                }
            }
        }
        return ret;
    }

    public static List<Integer> toIntegerList(String numbers) {
        return toIntegerList(numbers, false);
    }

    public static List<Integer> toIntegerList(String numbers, boolean ignoreError) {
        return toIntegerList(splitWithTrim(numbers), ignoreError);
    }

    public static List<Integer> toIntegerList(String[] numbers) {
        return toIntegerList(Arrays.asList(numbers));
    }

    public static List<Long> toLongList(List<String> numbers) {
        return toLongList(numbers, false);
    }

    public static List<Long> toLongList(List<String> numbers, boolean ignoreError) {
        if (numbers == null || numbers.isEmpty())
            return new ArrayList<Long>(0);
        List<Long> ret = new ArrayList<Long>(numbers.size());
        for (String number : numbers) {
            try {
                Long v = Long.valueOf(number);
                ret.add(v);
            } catch (NumberFormatException e) {
                if (!ignoreError) {
                    throw e;
                }
            }
        }
        return ret;
    }

    public static List<Long> toLongList(List<String> numbers, long defaultValue) {
        if (numbers == null || numbers.isEmpty())
            return new ArrayList<Long>(0);
        List<Long> ret = new ArrayList<Long>(numbers.size());
        for (String number : numbers) {
            Long v = NumberUtils.toLong(number, defaultValue);
            ret.add(v);
        }
        return ret;
    }

    public static List<Long> toLongList(String numbers) {
        return toLongList(numbers, true);
    }

    public static List<Long> toLongList(String numbers, boolean ignoreError) {
        return toLongList(splitWithTrim(numbers), ignoreError);
    }

    public static List<Long> toLongList(String numbers, long defaultValue) {
        return toLongList(splitWithTrim(numbers), defaultValue);
    }

    public static List<Long> toLongList(String[] numbers) {
        return toLongList(Arrays.asList(numbers));
    }

    public static List<String> wrap(List<?> items) {
        return wrap(items, '"');
    }

    public static List<String> wrap(List<?> items, char wrapChar) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<String>(0);
        }
        List<String> ret = new ArrayList<String>(items.size());
        for (Object item : items) {
            ret.add(wrapChar + String.valueOf(item) + wrapChar);
        }
        return ret;
    }
    
    public static String getSplit(String s, int index, String defaultValue) {
        return getSplit(s, ",", index, defaultValue);
    }
    public static String getSplit(String s, String sep,int index, String defaultValue) {
        List<String> list = splitWithTrim(s, sep);
        return get(list, index, defaultValue);
    }

    /**
     * 从list中获取指定索引位置的对象，如果索引越界，则使用默认值defaultValue填充
     * 
     * @param items
     *            list对象，最好是{@link RandomAccess}
     * @param index
     *            索引值，从0开始
     * @param defaultValue
     *            默认值
     * @return 列表list指定索引位置的值
     * @since 2014年5月5日
     */
    public static <T> T get(List<T> items, int index, T defaultValue) {
        if (index >= 0 && items != null && items.size() > index) {
            return items.get(index);
        }
        return defaultValue;
    }

    /**
     * 从list中获取指定索引位置的对象，如果索引越界，则使用null填充
     * 
     * @param items
     *            list对象，最好是{@link RandomAccess}
     * @param index
     *            索引值，从0开始
     * @return 列表list指定索引位置的值
     * @since 2014年5月5日
     */
    public static <T> T get(List<T> items, int index) {
        return get(items, index, null);
    }

    /**
     * 获取集合的第一个元素
     * 
     * @param items
     *            集合
     * @param defaultValue
     *            集合为null或者空时的默认值
     * @return 集合元素或者null
     * @since 2014年8月1日
     */
    public static <T> T first(Iterable<T> items, T defaultValue) {
        Iterator<T> it;
        if (items != null && (it = items.iterator()).hasNext()) {
            return it.next();
        }
        return defaultValue;
    }
    
    public static <T> Optional<T> findFirst(Iterable<T> items, Predicate<T> predicate) {
        if (items != null) {
            for (T t : items) {
                if (predicate.test(t)) {
                    return Optional.of(t);
                }
            }
        }
        return Optional.empty();
    }

    public static int firstInt(String s, int defaultValue) {
        if (s != null) {
            int index = s.indexOf(',');
            if (index > 0) {
                s = s.substring(0, index);
            }
            return NumberUtils.toInt(s.trim(), defaultValue);
        }
        return defaultValue;
    }

    public static long firstLong(String s, long defaultValue) {
        if (s != null) {
            int index = s.indexOf(',');
            if (index > 0) {
                s = s.substring(0, index);
            }
            return NumberUtils.toLong(s.trim(), defaultValue);
        }
        return defaultValue;
    }

    /**
     * 因为有些地方有且只有两个属性 需要分别取出
     * @param s
     * @param defaultValue
     * @return
     */
    public static long lastLong(String s, long defaultValue) {
        if (s != null) {
            int index = s.lastIndexOf(',');
            if (index > 0) {
                s = s.substring(index+1);
            }
            return NumberUtils.toLong(s.trim(), defaultValue);
        }
        return defaultValue;
    }

    @FunctionalInterface
    public interface BinaryComparator<T, K> {
        /**比较方法，0是相等*/
        int compare(T o1, K k);
    }
    /**
     * 二分查找快速排序
     * @param <T> 列表对象(必须已经按照从小到大排好序了）
     * @param <K> 列表对象要比较的key
     * @param list 要排序的列表
     * @param key 要比较的key，对象不一定和列表对象相同
     * @param c 比较方法
     * @return 找到的对象，有可能为空
     */
    public static <T, K> Optional<T> indexedBinarySearch(List<? extends T> list, K key, BinaryComparator<? super T, K> c) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = list.get(mid);
            int cmp = c.compare(midVal, key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return Optional.of(midVal); // key found
        }
        return Optional.empty(); // key not found
    }
    /**
     * 列表均分 n 等分(部分子列表比后面的子列表可能多1个
     * <p>
     * <pre>[]    --chunk3-->   []
[1]    --chunk3-->   [[1]]
[1, 2]    --chunk3-->   [[1], [2]]
[1, 2, 3]    --chunk3-->   [[1], [2], [3]]
[1, 2, 3, 4]    --chunk3-->   [[1, 2], [3], [4]]
[1, 2, 3, 4, 5]    --chunk3-->   [[1, 2], [3, 4], [5]]
[1, 2, 3, 4, 5, 6]    --chunk3-->   [[1, 2], [3, 4], [5, 6]]
[1, 2, 3, 4, 5, 6, 7]    --chunk3-->   [[1, 2, 3], [4, 5], [6, 7]]
[1, 2, 3, 4, 5, 6, 7, 8]    --chunk3-->   [[1, 2, 3], [4, 5, 6], [7, 8]]
    </pre>
    </p>
     * @param <T> 对象类型
     * @param list 列表
     * @param n 最多均分份数
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2017年1月20日
     */
    public static <T> List<List<T>> chunkWithNumber(final List<T> list, final int n){
        if(list == null || list.size() == 0 || n <= 0) {
            return new ArrayList<>(0);
        }
        final int mod = list.size() % n;//前多少个的个数正常值+1
        final int num = list.size() / n;
        final int retSize = Math.min(list.size(), n);
        final List<List<T>> ret = new ArrayList<>(retSize);
        int fromIndex = 0;
        for(int i=0;i<retSize;i++) {
            int len = i < mod ? num + 1 : num;
            ret.add(list.subList(fromIndex, fromIndex+len));
            fromIndex += len;
        }
        return ret;
    }

    public static <F, T> List<T> transform(final Collection<F> list, Function<? super F, ? extends T> mapper) {
        return transform(list, x -> true, mapper);
    }

    public static <F> List<F> filter(final Collection<F> list, Predicate<? super F> predicate) {
        return transform(list, predicate, x -> x);
    }
    /**
     * 将一个list转换成另一个list
     * @param list 原始list
     * @param predicate 过滤条件
     * @param mapper 转换条件
     * @return 转换结果
     * @since 2017年3月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static <F, T> List<T> transform(final Collection<F> list, Predicate<? super F> predicate, Function<? super F, ? extends T> mapper) {
        return list.stream().filter(predicate).map(mapper).collect(Collectors.toList());
    }
    public static <F> String joining(final Collection<F> list) {
        return joining(list, String::valueOf);
    }
    public static <F> String joining(final Collection<F> list, Function<? super F, String> mapper) {
        return joining(list, mapper, ",", "", "");
    }
    public static <F, T> List<T> map(final Collection<F> list , Function<? super F, ? extends T> mapper){
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    public static <F, T> Set<T> mapSet(final Collection<F> list , Function<? super F, ? extends T> mapper){
        return list.stream().map(mapper).collect(Collectors.toSet());
    }
    /**
     * 将一个集合转换成字符串
     * @param list 原始集合
     * @param mapper 转换对象
     * @param delimiter 分隔符
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 字符串
     * @since 2017年3月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static <F> String joining(final Collection<F> list, Function<? super F, String> mapper, String delimiter, String prefix, String suffix) {
        return list == null ? "" : list.stream().map(mapper).collect(Collectors.joining(delimiter, prefix, suffix));
    }
}
