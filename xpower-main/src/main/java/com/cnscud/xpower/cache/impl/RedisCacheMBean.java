package com.cnscud.xpower.cache.impl;

/**
 * Cache的Mbean操作，主要用于查看当前Cache状态
 * 
 * @author adyliu (imxylz@gmail.com)
 * @sine 2012-11-08
 */
public interface RedisCacheMBean {

    Integer getMaxPoolCount();

    Integer getCurrentPoolCount();

    long getTotalCallTimes();

    String getId();
}