package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认的长整数列表解析器实现
 *
 * @author adyliu (imxylz@gmail.com)
 * @see DefaultOpList.LongList
 * @since 2012-2-1
 * @deprecated
 */
public class LongOpList extends OpList<Long> {

    private long defaultNullValue = 0;

    public LongOpList(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    public LongOpList(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    @Override
    protected Long parse(ResultSet rs, int rowNum) throws SQLException {
        long v = rs.getLong(1);
        return rs.wasNull() ? defaultNullValue : v;
    }

    public LongOpList setDefaultNullValue(long defaultNullValue) {
        this.defaultNullValue = defaultNullValue;
        return this;
    }
}
