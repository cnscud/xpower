package com.cnscud.xpower.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作接口
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public interface IDao {
    /** 插入策略 */
    public static enum InsertStrategy{
        INSERT,
        INSERT_IGNORE,
        DUPLICATE_UPDATE,
        REPLACE;
    }
    /**
     * 批量更新对象
     *
     * @param op
     *            操作描述（更新操作）
     * @return 受影响的行数 <br>
     *         an array of update counts containing one element for each command in the batch. The elements of the array are ordered according to the order in
     *         which commands were added to the batch. <br>
     *         比如执行了批量操作一次执行了3条update command 正常返回数组length为3，int[0]的值返回的是第一条update command影响到的行数。 the order in which commands were added to the batch
     * @throws DaoException
     *             任何数据库异常
     * @see OpUpdate
     * @see #batchUpdate(OpBatchUpdate)
     */
    int[] batchUpdate(final OpUpdate op) throws DaoException;
    /**
     * 批量更新对象
     * @param op 操作描述（更新操作）
     * @return 受影响的行数
     * @throws DaoException 任何数据库异常
     * @since 2017年3月29日
     */
    int[] batchUpdate(final OpBatchUpdate op) throws DaoException;
    /**
     * 批量更新对象
     * @param sql sql语句
     * @param bizName 业务名称
     * @param params 参数列表，每一行参数是一条sql语句，自动添加addBatch
     * @return 受影响的行数
     * @throws DaoException 任何数据库异常
     * @since 2017年3月29日
     */
    default int[] batchUpdate(CharSequence sql, String bizName, Collection<Collection<?>> params) throws DaoException{
        return batchUpdate(new OpBatchUpdate(sql,bizName).addParamLists(params));
    }

    /**
     * 自己操作Connection，此连接不需要手动关闭{@link Connection#close()}
     *
     * @param op
     *            操作描述
     * @throws DaoException
     *             任何数据库异常
     * @see OpWithConnection
     */
    <T> T doWithConnection(OpWithConnection<T> op) throws DaoException;

    /**
     * 自己操作Connection，此连接不需要手动关闭{@link Connection#close()}
     * 
     * @param bizName
     *            业务名称
     * @param isReadConnection
     *            是否只读数据连接
     * @param sqlId
     *            由于无法记录SQL操作语句，所以需要用一个唯一的标志(固定值)来标示这次查询，这样才能记录查询耗时
     * @param handler
     *            数据库连接处理器
     * @return 返回结果
     * @since 2015年7月1日
     * @author Ady Liu (imxylz@gmail.com)
     */
    <T> T doWithConnection(String bizName, boolean isReadConnection, String sqlId, IConnectionHandler<T> handler);

    /**
     * 自己操作Connection（自动打开事务），此连接不需要手动关闭{@link Connection#close()}
     * <p>
     * 等价于：
     * 
     * <pre>
     * conn = getConnection(op.bizName, op.getRoutePattern(), op.isReadConnection);
     * conn.setAutoCommit(false);
     * try {
     *     T t = op.doInConnection(conn);
     *     conn.commit();
     *     return t;
     * } catch (Exception ex) {
     *     conn.rollback();
     *     throw ex;
     * }
     * </pre>
     * </p>
     * 
     * @param op
     *            操作描述
     * @return 返回结果
     * @throws DaoException
     *             任何数据库异常(包括回滚事务）
     * @since 2015年1月21日
     */
    <T> T doWithTransaction(OpWithConnection<T> op) throws DaoException;

    /**
     * 自己操作Connection（自动打开事务），此连接不需要手动关闭{@link Connection#close()} 有事务通常是更新操作，所以需要可写数据源（非丛库）
     * 
     * @param bizName
     *            业务名称
     * @param sqlId
     *            由于无法记录SQL操作语句，所以需要用一个唯一的标志(固定值)来标示这次查询，这样才能记录查询耗时
     * @param handler
     *            数据库连接处理器
     * @return 返回结果
     * @since 2015年6月30日
     * @author Ady Liu (imxylz@gmail.com)
     */
    <T> T doWithTransaction(String bizName, String sqlId, IConnectionHandler<T> handler);
    /**
     * 在同一个业务名称(bizName)下执行同一个事务操作
     * <p>
     * <pre>
        dao.doWithTransaction(bizName, transactionDao -> {
            // 新插入一条数据
            transactionDao.update(SQL_INSERT, 103, "vincent");
            int nowCount = transactionDao.queryUniq(SQL_COUNT, IRowMapper.INTEGER);
            System.out.println("now must be 3? -> " + nowCount);
            // 插入一条重复数据
            transactionDao.update(SQL_INSERT, 101, "tony");
        });
     * </pre>
     * </p>
     * @param bizName 业务名称
     * @param handler 操作对象
     */
    void doWithTransaction(String bizName, ITransactionHandler handler);

    /**
     * 根据业务名称和匹配规则获取数据库连接
     *
     * @param bizName
     *            业务名称
     * @param pattern
     *            匹配规则，如果为null，通常意味着不需要分库分表（路由功能）
     * @param isReadConnection
     *            是否是只读连接（通常情况下意味着是丛库）
     * @return 数据库连接
     * @throws DaoException
     *             任何数据库异常
     * @throws IllegalArgumentException
     *             如果不能获取任何数据库连接（通常是由于提供的参数有误）
     */
    default Connection getConnection1(final String bizName, final String pattern, final boolean isReadConnection) throws DaoException{
        return getConnection(bizName, pattern, isReadConnection, null);
    }
    Connection getConnection(final String bizName, final String pattern, final boolean isReadConnection, final Connection conn) throws DaoException;

    DataSource getDataSource(final String bizName, final String pattern, final boolean isReadDataSource);

    /**
     * get next sequence <br />
     *
     * @param sequenceName
     *            the sequence name
     * @return the next id
     * @throws DaoException
     *             any SQL exception
     */
    long getNextSequence(String sequenceName) throws DaoException;

    /**
     * 插入一条记录<br>
     * 和{@link #update(OpUpdate)}不同的是，此方法将返回插入的主键（如果有的话），前者只是返回插入记录行数 <br>
     * 【特别提醒】如果是 'INSERT INTO ... ON DUPLICATE KEY UPDATE ...'操作，根据MySQL的官方文档， 受影响的行数可能有三种返回值：
     * <ul>
     * <li>1： 插入操作，此时能返回主键ID</li>
     * <li>2: 数据有变化更新，此时能返回主键ID</li>
     * <li>0: 数据无变化更新，此时【不能】返回主键ID</li>
     * </ul>
     * 对于第三种情况，数据无变化更新，无法获取主键，此时主键为null，因此需要根据业务逻辑查询主键。
     * 
     * @param op
     *            操作描述，返回的类型与{@code op} 定义的类型相同
     * @return 主键Id（通常是自增id），如果没有主键，则返回null
     * @throws DaoException
     *             任何数据库异常
     * @see {@link OpInsert}
     */
    <T> T insert(final OpInsert<T> op) throws DaoException;

    /**
     * 插入一条记录<br>
     * 和{@link #update(OpUpdate)}不同的是，此方法将返回插入的主键（如果有的话），前者只是返回插入记录行数 <br>
     * 【特别提醒】如果是 'INSERT INTO ... ON DUPLICATE KEY UPDATE ...'操作，根据MySQL的官方文档， 受影响的行数可能有三种返回值：
     * <ul>
     * <li>1： 插入操作，此时能返回主键ID</li>
     * <li>2: 数据有变化更新，此时能返回主键ID</li>
     * <li>0: 数据无变化更新，此时【不能】返回主键ID</li>
     * </ul>
     * 对于第三种情况，数据无变化更新，无法获取主键，此时主键为null，因此需要根据业务逻辑查询主键。
     * 
     * @param sql
     *            SQL语句
     * @param bizName
     *            业务类型
     * @param clazz
     *            主键类型（Integer/Long/String）
     * @param params
     *            所需要的参数
     * @return 主键Id（通常是自增id），如果没有主键，则返回null
     * @throws DaoException
     *             任何数据库异常
     * @see {@link OpInsert}
     * @since 2016年7月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    default <T> T insert(CharSequence sql, String bizName, Class<T> clazz, Object... params) throws DaoException {
        return insert(new OpInsert<>(sql, bizName, clazz).addParams(params));
    }
    /**
     * 插入数据
     * @param bizName
     * @param tableName
     * @param fields
     * @return
     * @throws DaoException
     * @since 2019-07-30
     * @see #insert(CharSequence, String, Class, Object...)
     */
    default Long insert(String bizName, String tableName, FieldValue fields) throws DaoException{
        return insert(bizName, tableName, fields, InsertStrategy.INSERT);
    }
    /**
     * 插入数据
     * @param bizName
     * @param tableName
     * @param fields
     * @param strategy 插入策略
     * @return
     * @throws DaoException
     * @since 2019-11-28
     * @see #insert(CharSequence, String, Class, Object...)
     */
    default Long insert(String bizName, String tableName, FieldValue fields, InsertStrategy strategy) throws DaoException{
        PrimaryKeyOpInsert pkoi = new PrimaryKeyOpInsert(tableName, bizName, fields.keys(), strategy);
        pkoi.addParams(fields.values());
        return insert(pkoi);
    }
    

    /**
     * 处理查询结果集
     *
     * @param op
     *            查询操作
     * @throws DaoException
     *             任何数据库异常
     * @since 2013/8/8
     */
    public void queryWithResultset(OpWithResultset op) throws DaoException;

    /**
     * 查询列表对象
     *
     * @param <T>
     *            泛型对象
     * @param op
     *            操作描述，通常情况下在解析{@link ResultSet}是都已经调用过 {@link ResultSet#next()}
     * @return 列表对象，不为null，最多为EMPTY
     * @throws DaoException
     *             任何数据库异常
     * @see OpList
     */
    <T> List<T> queryList(OpList<T> op) throws DaoException;

    /**
     * 查询列表对象
     *
     * @param <T>
     *            泛型对象
     * @param sql
     *            SQL语句
     * @param bizName
     *            数据库实例
     * @param mapper
     *            解析方式
     * @param params
     *            参数列表，每个参数可以使单一类型 或者 集合类型
     * @return 列表对象，不为null，最多为EMPTY
     * @throws DaoException
     *             任何数据库异常
     * @see OpList
     * @see #queryList(OpList)
     * @since 2016年7月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    default <T> List<T> queryList(CharSequence sql, String bizName, IRowMapper<T> mapper, Object... params) {
        return queryList(new DefaultOpList<>(sql, bizName, mapper).addParams(params));
    }
    default <T> List<T> queryList(SqlWhere where, String bizName, IRowMapper<T> mapper) {
        return queryList(where.getSql(), bizName, mapper, where.getParams());
    }
    /**
     * 查询一个任意结果集对象
     *
     * @param <T>
     *            自定义对象
     * @param op
     *            操作描述，在此操作中需要自己解析{@link ResultSet}
     * @return 自定义对象
     * @throws DaoException
     *             任何数据库异常
     * @see OpResult
     */
    <T> T queryResult(OpResult<T> op) throws DaoException;

    /**
     * 查询一个任意结果集对象
     *
     * @param <T>
     *            自定义对象
     * @param sql
     *            SQL语句
     * @param bizName
     *            数据库实例
     * @param resultHandler
     *            数据解析方式
     * @param params
     *            参数列表，每个参数可以使单一类型 或者 集合类型
     * @return 自定义对象
     * @throws DaoException
     *             任何数据库异常
     * @see OpResult
     * @since 2016年7月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    default <T> T queryResult(CharSequence sql, String bizName, IResultHandler<T> resultHandler, Object... params) throws DaoException {
        return queryResult(OpResult.create(sql, bizName, resultHandler).addParams(params));
    }

    /**
     * 查询唯一的结果对象
     *
     * @param <T>
     *            泛型结果
     * @param op
     *            操作描述
     * @return 唯一结果，如果没有结果得到null,多于一行记录将得到一个异常
     * @throws DaoException
     *             任何数据库异常
     * @throws MultiResultException
     *             查询结果多于一行记录
     * @see OpUniq
     */
    <T> T queryUniq(OpUniq<T> op) throws DaoException, MultiResultException;

    /**
     * 查询唯一的结果对象
     *
     * @param <T>
     *            泛型结果
     * @param sql
     *            SQL语句
     * @param bizName
     *            数据库实例
     * @param mapper
     *            解析方式
     * @param params
     *            参数列表，每个参数可以使单一类型 或者 集合类型
     * @return 唯一结果，如果没有结果得到null,多于一行记录将得到一个异常
     * @throws DaoException
     *             任何数据库异常
     * @throws MultiResultException
     *             查询结果多于一行记录
     * @see OpUniq
     * @since 2016年7月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    default <T> T queryUniq(CharSequence sql, String bizName, IRowMapper<T> mapper, Object... params) throws DaoException, MultiResultException {
        return queryUniq(new DefaultOpUniq<>(sql, bizName, mapper).addParams(params));
    }
    default <T> T queryUniq(SqlWhere where, String bizName, IRowMapper<T> mapper) throws DaoException, MultiResultException {
        return queryUniq(where.getSql(), bizName, mapper, where.getParams());
    }
    /**
     * 查询一个整数值
     * @param sql SQL语句
     * @param bizName 数据库实例
     * @param defaultValue 默认值，如果数据库无记录，则返回默认值
     * @param params SQL参数
     * @return 整数值
     * @throws DaoException 任何数据库异常
     * @throws MultiResultException 查询结果多于一行记录
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年10月17日
     */
    default int queryInt(CharSequence sql, String bizName, int defaultValue, Object... params) throws DaoException, MultiResultException {
        Integer v = queryUniq(new DefaultOpUniq<>(sql, bizName, IRowMapper.INTEGER).addParams(params));
        return v != null ? v.intValue() : defaultValue;
    }
    /**
     * 查询一个长整数值
     * @param sql SQL语句
     * @param bizName 数据库实例
     * @param defaultValue 默认值，如果数据库无记录，则返回默认值
     * @param params SQL参数
     * @return 整数值
     * @throws DaoException 任何数据库异常
     * @throws MultiResultException 查询结果多于一行记录
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年10月17日
     */
    default long queryLong(CharSequence sql, String bizName, long defaultValue, Object... params) throws DaoException, MultiResultException {
        Long v = queryUniq(new DefaultOpUniq<>(sql, bizName, IRowMapper.LONG).addParams(params));
        return v != null ? v.longValue() : defaultValue;
    }
    /**
     * 查询一个字符串值
     * @param sql SQL语句
     * @param bizName 数据库实例
     * @param defaultValue 默认值，如果数据库无记录，则返回默认值
     * @param params SQL参数
     * @return 字符串值
     * @throws DaoException 任何数据库异常
     * @throws MultiResultException 查询结果多于一行记录
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年10月17日
     */
    default String queryString(CharSequence sql, String bizName, String defaultValue, Object... params) throws DaoException, MultiResultException {
        String v = queryUniq(new DefaultOpUniq<>(sql, bizName, IRowMapper.STRING).addParams(params));
        return v != null ? v : defaultValue;
    }

    /**
     * 查询一个Map值对象
     * 
     * @param sql
     *            SQL语句
     * @param bizName
     *            数据库实例
     * @param handler
     *            如何解析这个Map
     * @param params
     *            SQL参数
     * @return 非null的Map值对象
     * @throws DaoException
     *             任何数据库异常
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年10月17日
     */
    default <K, V> Map<K, V> queryMap(CharSequence sql, String bizName, IMapHandler<K, V> handler, Object... params)
            throws DaoException {
        return queryResult(sql, bizName, handler, params);
    }
    /**
     * 单独更新一次操作（适用于插入、修改、删除）
     *
     * @param op
     *            操作描述
     * @return 受影响的行数
     * @throws DaoException
     *             any SQL exception
     * @see OpUpdate
     */
    int update(final OpUpdate op) throws DaoException;

    /**
     * 单独更新一次操作（适用于插入、修改、删除）
     *
     * @param sql
     *            SQL语句
     * @param bizName
     *            数据库实例
     * @param params
     *            参数列表，每个参数可以使单一类型 或者 集合类型
     * @return 受影响的行数
     * @throws DaoException
     *             any SQL exception
     * @see OpUpdate
     * @since 2016年7月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    default int update(CharSequence sql, String bizName, Object... params) throws DaoException {
        return update(new OpUpdate(sql, bizName).addParams(params));
    }
    /**
     * 主键更新单表
     * @param bizName
     * @param tableName
     * @param fields
     * @param primaryKeyName
     * @param primaryKeyValue
     * @return 受影响行数
     * @since 2019-07-30
     */
    default int update(String bizName, String tableName, FieldValue fields,String primaryKeyName, Serializable primaryKeyValue) {
        PrimaryKeyOpUpdate op = new PrimaryKeyOpUpdate(tableName, primaryKeyName, bizName, fields.keys());
        op.addParams(fields.values()).addParams(primaryKeyValue);
        return update(op);
    }
    /**
     * 主键删除
     * @param bizName
     * @param tableName
     * @param primaryKeyName
     * @param primaryKeyValue
     * @return
     * @since 2019-07-30
     */
    default int delete(String bizName, String tableName, String primaryKeyName, Serializable primaryKeyValue) {
        return update(new PrimaryKeyOpDelete(tableName, primaryKeyName, bizName, primaryKeyValue));
    }
    /**
     * 根据条件删除
     * @param bizName
     * @param tableName
     * @param fields
     * @return
     * @since 2019-07-30
     */
    default int delete(String bizName, String tableName, FieldValue fields) {
        if(fields.isEmpty()) {
            throw new IllegalArgumentException("DO NOT DELETE ALL DATA");
        }
        SqlWhere sql = new SqlWhere(String.format("DELETE FROM %s WHERE 1=1", tableName));
        fields.forEach((field, value)->sql.andEquals(true, field, value));
        return update(sql.getSql(), bizName, sql.getParams());
    }

    Long updateAndReturnLastInsertId(final OpUpdate op) throws DaoException;
}