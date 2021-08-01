package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果集（Resultset）操作<br />
 * 相对于{@link OpList}，无须返回结果
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-08-08
 */
public abstract class OpWithResultset extends Op {

    final boolean isReadConnection;

    /**
     * 构建一个列表查询操作
     *
     * @param sql              sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName          数据源名称，通常就是业务名称
     * @param isReadConnection 是否是只读连接，也就是是否是从库操作，如果有的话
     * @throws IllegalArgumentException 如果bizName是空的话
     */
    public OpWithResultset(CharSequence sql, String bizName, boolean isReadConnection) {
        super(sql, bizName);
        this.isReadConnection = isReadConnection;
    }

    /**
     * 构建一个列表查询操作
     *
     * @param sql              sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName          数据源名称，通常就是业务名称
     * @param index            散表的数据库索引，-1代表不散表
     * @param isReadConnection 是否是只读连接，也就是是否是从库操作，如果有的话
     * @throws IllegalArgumentException 如果bizName是空的话
     */
    public OpWithResultset(CharSequence sql, String bizName, long index, boolean isReadConnection) {
        super(sql, bizName, index);
        this.isReadConnection = isReadConnection;
    }

    /**
     * 处理结果集
     *
     * @param rs 结果集(需要自行遍历{@link java.sql.ResultSet#next()})
     * @throws SQLException 任何数据库异常
     * @return 此结果集(ResultSet)的大小
     */
    protected abstract int execute(ResultSet rs) throws SQLException;


    @Override
    public OpWithResultset addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpWithResultset setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }
}
