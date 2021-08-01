/**
 * 
 */
package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * 默认的查询结果集操作
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年6月17日
 */
public class DefaultOpWithResultset extends OpWithResultset {

    final Consumer<ResultSet> consumer;

    /**
     * 默认的查询结果集操作
     * 
     * @param sql
     *            SQL语句
     * @param bizName
     *            业务名称
     * @param isReadConnection
     *            是否只读连接
     * @param consumer
     *            每次消费一行数据（以检测过 ResultSet.next()）
     */
    public DefaultOpWithResultset(CharSequence sql, String bizName, boolean isReadConnection, final Consumer<ResultSet> consumer) {
        super(sql, bizName, isReadConnection);
        this.consumer = consumer;
    }

    /**
     * 默认的查询结果集操作
     * 
     * @param sql
     *            SQL语句
     * @param bizName
     *            业务名称
     * @param index
     *            数据源分库
     * @param isReadConnection
     *            是否只读连接
     * @param consumer
     *            每次消费一行数据（以检测过 ResultSet.next()）
     */
    public DefaultOpWithResultset(CharSequence sql, String bizName, long index, boolean isReadConnection, final Consumer<ResultSet> consumer) {
        super(sql, bizName, index, isReadConnection);
        this.consumer = consumer;
    }

    @Override
    protected int execute(ResultSet rs) throws SQLException {
        int rownum = 0;
        while (rs.next()) {
            consumer.accept(rs);
            rownum++;
        }
        return rownum;
    }
}
