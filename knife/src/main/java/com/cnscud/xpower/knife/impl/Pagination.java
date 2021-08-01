package com.cnscud.xpower.knife.impl;

import static java.lang.String.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cnscud.xpower.knife.IPage;

/**
 * 分页对象
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class Pagination<T> implements IPage<T>, Serializable {

    private final long count;
    private final long pageSize;
    private final long page;
    private List<T> list = new ArrayList<>();

    /**
     * 分页
     * 
     * @param pageSize
     *            每页大小
     * @param page
     *            页码，从1开始
     * @param count
     *            记录总数
     */
    public Pagination(long page, long pageSize, long count) {
        this.count = count;
        this.pageSize = pageSize;
        this.page = page;
    }

    public Pagination(long page, long pageSize, long count,List<T> list) {
        this.count = count;
        this.pageSize = pageSize;
        this.page = page;
        this.list = list;
    }


    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public long getPageSize() {
        return pageSize;
    }

    @Override
    public long getPage() {
        return page;
    }

    // ---- protected ----
    boolean hasNext() {
        return count > (page - 1) * pageSize;
    }
    public long offset() {
        return (page-1)*pageSize;
    }
    public long limit() {
        return getPageSize();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    @Override
    public String toString() {
        return format("count=%s, page=%s, pageSize=%s, pageCount=%s, listSize=%s", count, page, pageSize, getPageCount(), list.size());
    }
}
