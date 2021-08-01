package com.cnscud.xpower.knife;

import java.util.List;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public interface IPage<T> {

    List<T> getList();

    /** 记录总数 */
    long getCount();

    /** 总页数 */
    default long getPageCount() {
        return getCount() > 0 && getPageSize() > 0 ? (getCount() + getPageSize() - 1) / getPageSize() : 0;
    }

    /** 每页大小 */
    long getPageSize();

    /** 当前第几页 */
    long getPage();
}
