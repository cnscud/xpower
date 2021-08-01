package com.cnscud.xpower.knife.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class TableCache {

    static ConcurrentMap<Class, TableInfo> cache = new ConcurrentHashMap<>();
    public static TableInfo get(Class<?> clazz) {
        TableInfo tf = cache.computeIfAbsent(clazz, TableInfo::new);
        return tf;
    }
}
