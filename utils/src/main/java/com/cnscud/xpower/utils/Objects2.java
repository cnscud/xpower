package com.cnscud.xpower.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 对象本身的常用操作
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-12-14
 */
public class Objects2 {
    
    public static <T> List<T> or(List<T> list){
        return list != null ? list : new ArrayList<>();
    }
    public static <K,V> Map<K,V> or(Map<K,V> map){
        return map != null ? map : new HashMap<>(0);
    }
    public static <K,V> SortedMap<K,V> or(SortedMap<K,V> map){
        return map != null ? map : new TreeMap<>();
    }
    public static <T> Set<T> or(Set<T> set){
        return set != null ? set : new HashSet<>();
    }
    public static String or(String s) {
        return s != null ? s : "";
    }
    public static <T> T or(T t, T defaultValueIfNull) {
        return t != null ? t : defaultValueIfNull;
    }
}
