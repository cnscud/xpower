package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 自定义结果集操作
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public abstract class OpResult<T> extends Op {

    T result;

    /**
     * 构建一个自定义结果集操作
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public OpResult(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    /**
     * 构建一个自定义结果集操作
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
    public OpResult(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    /**
     * 解析结果集
     * 
     * @param rs
     *            结果集，此结果集没有调用过{@link ResultSet#next()}，原始的结果集
     * @return 解析的后结果对象，也即是最终得到的结果
     * @throws SQLException
     *             任何数据库异常
     */
    protected abstract T parse(ResultSet rs) throws SQLException;

    @Override
    public OpResult<T> addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpResult<T> setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }

    static class DefaultOpResult<T> extends OpResult<T> {
        final IResultHandler<T> resultHandler;

        public DefaultOpResult(CharSequence sql, String bizName, IResultHandler<T> resultHandler) {
            super(sql, bizName);
            this.resultHandler = resultHandler;
        }

        public DefaultOpResult(CharSequence sql, String bizName, long index, IResultHandler<T> resultHandler) {
            super(sql, bizName, index);
            this.resultHandler = resultHandler;
        }

        @Override
        protected T parse(ResultSet rs) throws SQLException {
            Objects.requireNonNull(resultHandler, "resultHanlder must not be null");
            return resultHandler.parse(rs);
        }
    }

    public static <T> OpResult<T> create(CharSequence sql, String bizName, long index, IResultHandler<T> resultHandler) {
        return new DefaultOpResult<>(sql, bizName, index, resultHandler);
    }

    public static <T> OpResult<T> create(CharSequence sql, String bizName, IResultHandler<T> resultHandler) {
        return new DefaultOpResult<>(sql, bizName, resultHandler);
    }
}