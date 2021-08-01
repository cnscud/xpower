package com.cnscud.xpower.knife.impl;

/**
 * @author Deacon Peng 2020-09-07 16:20
 * @version 1.0.0
 */
public class Page {
    public static long getPageCount(long count,long pageSize){
        return count > 0 && pageSize > 0 ? (count + pageSize - 1) / pageSize : 0;
    }

    public static long getPageOffset(long page,long pageSize){
        return page <= 0 ? 0 : (page - 1) * pageSize;
    }
}
