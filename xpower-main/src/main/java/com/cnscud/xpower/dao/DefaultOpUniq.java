package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认的当行记录解析方式
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-1-30
 */
public class DefaultOpUniq<T> extends OpUniq<T> {

    private IRowMapper<T> mapper = null;

    public DefaultOpUniq(CharSequence sql, String bizName, long index, IRowMapper<T> mapper) {
        super(sql, bizName, index);
        this.mapper = mapper;
    }

    /**
     * @since 2013-3-26
     */
    public DefaultOpUniq(CharSequence sql, String bizName, IRowMapper<T> mapper) {
        super(sql, bizName);
        this.mapper = mapper;
    }

    public DefaultOpUniq(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    public DefaultOpUniq(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    @Override
    protected T parse(ResultSet rs) throws SQLException {
        return mapper != null ? mapper.mapRow(rs, 0) : null;
    }

    public DefaultOpUniq<T> setMapper(IRowMapper<T> mapper) {
        this.mapper = mapper;
        return this;
    }
}
