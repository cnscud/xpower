package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认的当行记录解析方式
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-2-1
 */
public class DefaultOpList<T> extends OpList<T> {

    public static class IntegerList extends DefaultOpList<Integer> {
        private int defaultNullValue = 0;

        public IntegerList(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        public IntegerList(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        @Override
        protected Integer parse(ResultSet rs, int rowNum) throws SQLException {
            int v = rs.getInt(1);
            return rs.wasNull() ? defaultNullValue : v;
        }

        public IntegerList setDefaultNullValue(int defaultNullValue) {
            this.defaultNullValue = defaultNullValue;
            return this;
        }
    }

    public static class LongList extends DefaultOpList<Long> {
        private long defaultNullValue = 0L;

        public LongList(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        public LongList(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        @Override
        protected Long parse(ResultSet rs, int rowNum) throws SQLException {
            long v = rs.getLong(1);
            return rs.wasNull() ? defaultNullValue : v;
        }

        public LongList setDefaultNullValue(long defaultNullValue) {
            this.defaultNullValue = defaultNullValue;
            return this;
        }
    }

    public static class StringList extends DefaultOpList<String> {
        private String defaultNullValue = null;

        public StringList(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        public StringList(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        @Override
        protected String parse(ResultSet rs, int rowNum) throws SQLException {
            String v = rs.getString(1);
            return rs.wasNull() ? defaultNullValue : v;
        }

        public StringList setDefaultNullValue(String defaultNullValue) {
            this.defaultNullValue = defaultNullValue;
            return this;
        }
    }

    @Deprecated
    public static class DefaultOpLongList extends DefaultOpList<Long> {

        public DefaultOpLongList(CharSequence sql, String bizName, long index) {
            super(sql, bizName, index);
        }

        public DefaultOpLongList(CharSequence sql, String bizName) {
            super(sql, bizName);
        }

        @Override
        protected Long parse(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
        }

    }

    private IRowMapper<T> mapper = null;

    public DefaultOpList(CharSequence sql, String bizName, long index, IRowMapper<T> mapper) {
        super(sql, bizName, index);
        this.mapper = mapper;
    }

    /**
     * @since 2013-3-26
     */
    public DefaultOpList(CharSequence sql, String bizName, IRowMapper<T> mapper) {
        super(sql, bizName);
        this.mapper = mapper;
    }

    public DefaultOpList(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    public DefaultOpList(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    @Override
    protected T parse(ResultSet rs, int rowNum) throws SQLException {
        if (mapper == null) {
            throw new IllegalArgumentException("mapper must not be null");
        }
        return mapper.mapRow(rs, rowNum);
    }

    public DefaultOpList<T> setMapper(IRowMapper<T> mapper) {
        this.mapper = mapper;
        return this;
    }
}