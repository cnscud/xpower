package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认的整数列表解析器实现
 *
 * @author adyliu (imxylz@gmail.com)
 * @see DefaultOpList.IntegerList
 * @since 2012-2-1
 * @deprecated
 */
public class IntegerOpList extends OpList<Integer> {

    private int defaultNullValue = 0;

    public IntegerOpList(CharSequence sql, String bizName, long index) {
        super(sql, bizName, index);
    }

    public IntegerOpList(CharSequence sql, String bizName) {
        super(sql, bizName);
    }

    @Override
    protected Integer parse(ResultSet rs, int rowNum) throws SQLException {
        int v = rs.getInt(1);
        return rs.wasNull() ? defaultNullValue : v;
    }

    public IntegerOpList setDefaultNullValue(int defaultNullValue) {
        this.defaultNullValue = defaultNullValue;
        return this;
    }
}
