package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表查询操作<br />
 * 相对于{@link OpResult}，这里只是每次需要解析一行记录，并且返回的结果是一个列表
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public abstract class OpList<T> extends Op {

    final List<T> results = new ArrayList<T>();

    /**
     * 构建一个列表查询操作
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public OpList(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    /**
     * 构建一个列表查询操作
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
    public OpList(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    /**
     * 解析一行记录
     * 
     * @param rs
     *            SQL结果集，特别注意，此处语句调用过了{@link ResultSet#next()}方法， 因此不用担心没有结果的问题，而且也不应该调用{@link ResultSet#next()}方法，否 则将导致无法得到正确的结果。
     * @param rowNum
     *            当前记录行数，从0开始
     * @return 解析的单行结果
     * @throws SQLException
     *             任何数据库异常
     */
    protected abstract T parse(ResultSet rs, int rowNum) throws SQLException;

    @Override
    public OpList<T> addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpList<T> setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }

}