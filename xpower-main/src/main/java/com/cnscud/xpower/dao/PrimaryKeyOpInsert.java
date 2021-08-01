/**
 * 
 */
package com.cnscud.xpower.dao;

import static java.lang.String.format;
import static java.lang.String.join;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.cnscud.xpower.dao.IDao.InsertStrategy;

/**
 * 主键插入单表数据
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年1月30日
 */
public class PrimaryKeyOpInsert extends OpInsert<Long> {
    /**
     * 通过主键插入单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     */
    public PrimaryKeyOpInsert(String tableName, String bizName, String... updateFieldNames) {
        this(tableName, bizName, Arrays.asList(updateFieldNames));
    }

    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     */
    public PrimaryKeyOpInsert(String tableName, String bizName, Collection<String> updateFieldNames) {
        this(tableName, bizName, updateFieldNames, Op.DEFAULT_INDEX);
    }
    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     * @since 2019-11-28
     */
    public PrimaryKeyOpInsert(String tableName, String bizName, Collection<String> updateFieldNames, InsertStrategy strategy) {
        this(tableName, bizName, updateFieldNames, Op.DEFAULT_INDEX, strategy);
    }

    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param primaryKeyName
     *            主键字段名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     * @param index
     *            业务路由字段
     * @see {@link Op#getRoutePattern()}
     */
    public PrimaryKeyOpInsert(String tableName, String bizName, Collection<String> updateFieldNames, long index) {
        this(tableName, bizName, updateFieldNames, index, InsertStrategy.INSERT);
    }
    
    /**
     * 通过主键更新单表字段信息
     * 
     * @param tableName
     *            表名称
     * @param primaryKeyName
     *            主键字段名称
     * @param bizName
     *            业务名称
     * @param updateFieldNames
     *            要更新的字段
     * @param index
     *            业务路由字段
     * @param strategy
     *            插入策略
     * @see {@link Op#getRoutePattern()}
     * @since 2019-11-28
     */
    public PrimaryKeyOpInsert(String tableName, String bizName, Collection<String> updateFieldNames, long index, InsertStrategy strategy) {
        super(buildSql(strategy, tableName, updateFieldNames), bizName, index, Long.class);
    }

    /**
     * 预编译参数的个数必须是 updateFieldNames 个数+1（位置相同），最后一个参数是主键的值
     */
    @Override
    public PrimaryKeyOpInsert addParams(Object... params) {
        return (PrimaryKeyOpInsert) super.addParams(params);
    }

    private static String buildSql(InsertStrategy strategy, String tableName, String... updateFieldNames) {
        return buildSql(strategy, tableName, Arrays.asList(updateFieldNames));
    }

    private static String buildSql(InsertStrategy strategy, String tableName,//
            Collection<String> fieldNames) {
        StringBuilder sql = new StringBuilder();
        if(strategy == InsertStrategy.REPLACE) {
            sql.append("replace into ");
        }else if(strategy == InsertStrategy.INSERT_IGNORE) {
            sql.append("insert ignore into ");
        }else {
            sql.append("insert into ");
        }
        sql.append(tableName).append("(").append(join(",", fieldNames)).append(")");
        sql.append(" values(").append(SqlUtils.buildQuestionMark(fieldNames.size())).append(")");
        if(strategy == InsertStrategy.DUPLICATE_UPDATE) {
            sql.append(" on duplicate key update ");
            String more = fieldNames.stream().map(f->format("%s=values(%s)",f,f)).collect(Collectors.joining(","));
            sql.append(more);
        }
        return sql.toString();
    }
}
