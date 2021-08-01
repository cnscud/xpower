package com.cnscud.xpower.dao;

/**
 * Sequence操作接口
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-18
 */
public interface ISequence {

    /**
     * 获取下一个序列Id
     * 
     * @param sequenceName 序列名称
     * @return 下一个序列Id
     */
    long getNextSequence(final String sequenceName);
}