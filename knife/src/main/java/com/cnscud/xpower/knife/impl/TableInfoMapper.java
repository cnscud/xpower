package com.cnscud.xpower.knife.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.cnscud.xpower.dao.IRowMapper;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class TableInfoMapper<T> implements IRowMapper<T> {
    final TableInfo tableInfo;
    public TableInfoMapper(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T ret = tableInfo.newObject();
        ResultSetMetaData meta = rs.getMetaData();
        for(int i=1;i<=meta.getColumnCount();i++) {
            String columnName = meta.getColumnName(i);
            TableFieldInfo f = tableInfo.getField(columnName);
            if(f != null) {
                f.setter(ret, rs, i);
            }
        }
        return ret;
    }
    /**
     * 自动生成对象映射
     * @param clazz 类对象
     * @return 数据库类转化对象
     */
    public static <T> TableInfoMapper<T> create(Class<?> clazz){
        return new TableInfoMapper<>(TableCache.get(clazz));
    }
}
