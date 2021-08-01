/**
 * 
 */
package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * List(Object)类型结果
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2014年12月15日
 */
public class ListOpList extends OpList<List<Object>> {
    public int fieldCount;

    /**
     * List(Object)类型结果
     * 
     * @param sql
     *            sql语句
     * @param bizName
     *            业务类型
     * @param fieldCount
     *            结果字段数量
     */
    public ListOpList(CharSequence sql, String bizName, int fieldCount) {
        super(sql, bizName);
        this.fieldCount = fieldCount;
    }

    @Override
    protected List<Object> parse(ResultSet rs, int rowNum) throws SQLException {
        List<Object> record = new ArrayList<Object>(fieldCount);
        for (int i = 1; i <= fieldCount; i++) {
            record.add(rs.getObject(i));
        }
        return record;
    }
}
