package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 单一结果操作<br />
 * 通常情况下需要对得到的结果进行判断是否为空，当为查询到结果时返回的对象就为空。 而对于{@link ExistOpUniq} ，得到的结果总是不为空，是一个{@link Boolean}对象。
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public abstract class OpUniq<T> extends Op {

    /**
     * 日期结果，如果无记录或者记录的值是null则返回null。 获取一个{@link java.util.Date}对象，特别注意，这不是 {@link java.sql.Date}对象
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class DateOpUniq extends OpUniq<Date> {

        public DateOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public DateOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected Date parse(ResultSet rs) throws SQLException {
            java.sql.Date date = rs.getDate(1);
            return rs.wasNull() ? null : new Date(date.getTime());
        }

    }

    public static class DateTimeUniq extends DateOpUniq {

        public DateTimeUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        public DateTimeUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        @Override
        protected Date parse(ResultSet rs) throws SQLException {
            java.sql.Timestamp date = rs.getTimestamp(1);
            return rs.wasNull() ? null : new Date(date.getTime());
        }
    }

    /**
     * 获取一个Double值，如果无记录或者记录的值是null则返回null
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class DoubleUniq extends OpUniq<Double> {

        public DoubleUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public DoubleUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected Double parse(ResultSet rs) throws SQLException {
            double d = rs.getDouble(1);
            return rs.wasNull() ? null : new Double(d);
        }

    }

    /**
     * 判断结果是否存在，如果存在为{@link Boolean#TRUE}, 否则为{@link Boolean#FALSE}
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class ExistOpUniq extends OpUniq<Boolean> {

        public ExistOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public ExistOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        /**
         * 得到的结果始终不为空，如果存在为{@link Boolean#TRUE}, 否则为{@link Boolean#FALSE}
         * 
         * @return 查询的结果是否存在
         */
        public Boolean getResult() {
            return result == null ? Boolean.FALSE : Boolean.TRUE;
        }

        @Override
        protected Boolean parse(ResultSet rs) throws SQLException {
            return Boolean.TRUE;
        }

    }

    /**
     * 获取一个整数值，如果无记录或者记录的值是null则返回null
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class IntegerOpUniq extends OpUniq<Integer> {

        public IntegerOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public IntegerOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected Integer parse(ResultSet rs) throws SQLException {
            Integer value = new Integer(rs.getInt(1));
            return rs.wasNull() ? null : value;
        }

    }

    /**
     * 获取一个长整数值，如果无记录或者记录的值是null则返回null
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class LongOpUniq extends OpUniq<Long> {

        public LongOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public LongOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected Long parse(ResultSet rs) throws SQLException {
            long v = rs.getLong(1);
            return rs.wasNull() ? null : new Long(v);
        }

    }

    /**
     * 获取一个短整数值，如果无记录或者记录的值是null则返回null
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class ShortOpUniq extends OpUniq<Short> {

        public ShortOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public ShortOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected Short parse(ResultSet rs) throws SQLException {
            short s = rs.getShort(1);
            return rs.wasNull() ? null : new Short(s);
        }

    }

    /**
     * 获取一个字符串，如果无记录或者记录的值是null则返回null
     * 
     * @author adyliu (imxylz@gmail.com)
     * @since 2011-6-28
     */
    public static class StringOpUniq extends OpUniq<String> {

        public StringOpUniq(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        public StringOpUniq(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        @Override
        protected String parse(ResultSet rs) throws SQLException {
            String s = rs.getString(1);
            return rs.wasNull() ? null : s;
        }

    }

    protected T result;

    /**
     * 构建一个唯一结果查询操作
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public OpUniq(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    /**
     * 构建一个唯一结果查询操作
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
    public OpUniq(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    /**
     * 获取查询的结果，如果查询的结果集为空则将得到一个null
     * 
     * @return 单一结果
     */
    public T getResult() {
        return result;
    }

    /**
     * 解析结果集<br/>
     * 注意ResultSet语句调用过了{@link ResultSet#next()}方法，如果返回的结果为null， 则不会调用此方法，如果返回的结果多余一个则会得到一个{@link IllegalStateException}
     * 
     * @param rs
     *            结果集
     * @return 单一对象
     * @throws SQLException
     *             任何数据库异常
     */
    protected abstract T parse(ResultSet rs) throws SQLException;

    void setResult(T result) {
        this.result = result;
    }

    @Override
    public OpUniq<T> addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpUniq<T> setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }

    public static IntegerOpUniq createInteger(CharSequence sql, String bizName) {
        return new OpUniq.IntegerOpUniq(sql, bizName);
    }

    public static LongOpUniq createLong(CharSequence sql, String bizName) {
        return new OpUniq.LongOpUniq(sql, bizName);
    }

    public static ExistOpUniq createBoolean(CharSequence sql, String bizName) {
        return new OpUniq.ExistOpUniq(sql, bizName);
    }
}