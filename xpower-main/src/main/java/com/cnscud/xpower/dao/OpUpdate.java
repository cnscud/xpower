package com.cnscud.xpower.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 更新操作 （插入、修改、删除）
 * 
 * @since 2011-6-14
 * @author adyliu (imxylz@gmail.com)
 */
public class OpUpdate extends Op {

    /**
     * 构建一个数据库更新操作，通常情况下将使用主库，如果有从库的话
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public OpUpdate(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    /**
     * 构建一个数据库更新操作，通常情况下将使用主库，如果有从库的话
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @param index
     *            散表的数据库索引，-1代表不散表
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public OpUpdate(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    /**
     * 更新语句参数，如果是批量更新（插入、修改、删除）操作需要{@link PreparedStatement#addBatch()} 如果sql语句没有预编译参数而调用此方法将导致sql异常
     */
    protected void setParams(PreparedStatement ps) throws SQLException {
        super.setParams(ps);
    }

    @Override
    public OpUpdate addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpUpdate setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }
}