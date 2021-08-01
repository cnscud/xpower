/**
 * 
 */
package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义解析结果集
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年3月2日
 */
@FunctionalInterface
public interface IResultHandler<T> {
    /**
     * 解析结果集
     * 
     * @param rs
     *            结果集，此结果集没有调用过{@link ResultSet#next()}，原始的结果集
     * @return 解析的后结果对象，也即是最终得到的结果
     * @throws SQLException
     *             任何数据库异常
     */
    T parse(ResultSet rs) throws SQLException;
}
