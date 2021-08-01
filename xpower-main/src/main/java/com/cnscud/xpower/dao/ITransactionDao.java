package com.cnscud.xpower.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 在同一个业务名称(bizName)下执行同一个事务操作
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年3月29日
 */
public interface ITransactionDao {
    /**
     * 插入记录返回主键
     * @param sql sql语句
     * @param clazz 主键类型
     * @param params 参数列表
     * @return 主键
     * @throws DaoException 任何数据库异常
     */
    <T> T insert(CharSequence sql, Class<T> clazz, Object... params) throws DaoException;
    /**
     * 更新操作(insert/update/delete)
     * @param sql sql语句
     * @param params 参数列表
     * @return 受影响行数（永远不会返回主键）
     * @throws DaoException 任何数据库异常
     */
    int update(CharSequence sql, Object... params) throws DaoException;
    /**
     * 查询唯一一条结果
     * @param <T> 自定义类型
     * @param sql sql语句
     * @param mapper 解析结果ResultSet
     * @param params 参数列表
     * @return 自定义结果类型
     * @throws DaoException 任何数据库异常
     * @throws MultiResultException 返回多余一条记录
     */
    <T> T queryUniq(CharSequence sql, IRowMapper<T> mapper, Object... params) throws DaoException, MultiResultException;
    /**
     * 查询列表结果
     * @param <T> 自定义类型
     * @param sql sql语句
     * @param mapper 解析结果ResultSet
     * @param params 参数列表
     * @return 集合对象
     * @throws DaoException 任何数据库异常
     */
    <T> List<T> queryList(CharSequence sql, IRowMapper<T> mapper, Object... params) throws DaoException;
    /**
     * 查询自定义结果
     * @param <T> 自定义类型
     * @param sql sql语句
     * @param resultHandler 自定义结果解析
     * @param params 参数列表
     * @return 自定义结果
     * @throws DaoException 任何数据库异常
     */
    <T> T queryResult(CharSequence sql, IResultHandler<T> resultHandler, Object... params) throws DaoException;
    /**
     * 批量执行更新操作（自动执行addBatch）
     * @param sql sql语句
     * @param params 参数列表
     * @return 每条语句执行的结果
     * @throws DaoException 任何数据库异常
     */
    int[] batchUpdate(CharSequence sql, Collection<Collection<?>> params) throws DaoException;
    
    IDao getDao();
    String getBizName();
    
    /**
     * 根据条件删除
     * @param bizName
     * @param tableName
     * @param fields
     * @return
     * @since 2019-07-30
     */
    default int delete(String tableName, FieldValue fields) {
        if(fields.isEmpty()) {
            throw new IllegalArgumentException("DO NOT DELETE ALL DATA");
        }
        SqlWhere sql = new SqlWhere(String.format("DELETE FROM %s WHERE 1", tableName));
        fields.forEach((field, value)->sql.andEquals(true, field, value));
        return update(sql.getSql(), sql.getParams());
    }
    default int update(String tableName, FieldValue fields,String primaryKeyName, Serializable primaryKeyValue) {
        PrimaryKeyOpUpdate op = new PrimaryKeyOpUpdate(tableName, primaryKeyName, getBizName(), fields.keys());
        op.addParams(fields.values()).addParams(primaryKeyValue);
        return update(op.sql, op.getParamList());
    }
    
    default Long insert(String tableName, FieldValue fields) throws DaoException{
        PrimaryKeyOpInsert pkoi = new PrimaryKeyOpInsert(tableName, getBizName(), fields.keys());
        pkoi.addParams(fields.values());
        return insert(pkoi.sql, Long.class, pkoi.getParamList());
    }
}
