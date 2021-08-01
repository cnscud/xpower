package com.cnscud.xpower.knife.impl;

import static java.lang.String.format;

import java.io.Serializable;
import java.util.*;
import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.FieldValue;
import com.cnscud.xpower.dao.IDao;
import com.cnscud.xpower.dao.SqlUtils;
import com.cnscud.xpower.dao.SqlWhere;
import com.cnscud.xpower.knife.IPage;
import com.cnscud.xpower.knife.Iknife;
import com.cnscud.xpower.knife.lambda.LambdaService;
import org.apache.commons.lang.StringUtils;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class KnifeImpl implements Iknife {

    private final IDao dao = DaoFactory.getIDao();

    @Override
    public <T> int deleteById(String bizName, Class<T> clazz, Serializable id) {
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        final String sql = format("delete from %s where %s = ? limit 1", table.tableName, table.primaryKey.column);
        return dao.update(sql, bizName, id);
    }

    @Override
    public <T> int deleteById(String bizName, T model) {
        Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        final String sql = format("delete from %s where %s = ? limit 1", table.tableName, table.primaryKey.column);
        return dao.update(sql, bizName, table.primaryKey.getValue(model));
    }

    @Override
    public <T> int deleteByIds(String bizName, Class<T> clazz, Collection<Serializable> ids) {
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        final SqlWhere where = new SqlWhere(format("delete from %s where 1", table.tableName, table.primaryKey.column));
        where.andIn(table.primaryKey.column, ids);
        return dao.update(where.getSql(), bizName, where.getParams());
    }

    @Override
    public <T> int deleteByMap(String bizName, Class<T> clazz, Map<String, Object> conditions) {
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        final SqlWhere where = new SqlWhere(format("delete from %s where 1", table.tableName));
        table.prepareWhere(conditions, where);
        return dao.update(where.getSql(), bizName, where.getParams());
    }

    @Override
    public <T> Long insert(String bizName, T model) {
        Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        return DaoFactory.getIDao().insert(bizName, table.tableName, table.insertFields(model));
    }

    public <T> int insertBatch(String bizName, Collection<T> model) {
        if (model.size() <= 0) {
            return 0;
        }
        Set<String> keys = new LinkedHashSet<>();
        List<Collection<?>> params = new ArrayList<>();
        String tableName = null;
        for (T mod : model) {
            Class<?> clazz = mod.getClass();
            TableInfo table = TableCache.get(clazz);
            Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
            FieldValue fieldValue = table.insertFields(mod);
            if (keys.size() <= 0) {
                tableName = table.getTableName();
                keys.addAll(fieldValue.keys());
            }
            params.addAll(Collections.singleton(fieldValue.values()));
        }
        StringBuilder sb = new StringBuilder(format("insert into %s (", tableName));
        for (String fieldName : keys) {
            sb.append(fieldName).append(",");
        }
        String removeEnd = StringUtils.removeEnd(sb.toString(), ",");
        StringBuilder sb2 = new StringBuilder(removeEnd);
        sb2.append(") values (").append(SqlUtils.buildQuestionMark(keys.size())).append(")");
        int[] ints = dao.batchUpdate(sb2, bizName, params);
        return ints.length;
    }

    @Override
    public <T> IPage<T> list(final String bizName, T model, final String orderBy, final boolean asc, final long page, final long pageSize) {
        final Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        //Objects.requireNonNull(orderBy, orderBy + " must not be null");
        SqlWhere countSql = new SqlWhere(format("select count(1) from %s where 1", table.tableName));
        table.prepareWhere(model, countSql);
        final long count = dao.queryLong(countSql.getSql(), bizName, 0, countSql.getParams());
        Pagination<T> paging = new Pagination<>(page, pageSize, count);
        if (paging.hasNext()) {
            SqlWhere listSql = new SqlWhere(format("select * from %s where 1", table.tableName));
            table.prepareWhere(model, listSql);
            if (orderBy != null) {
                listSql.orderBy(orderBy, asc);
            }
            listSql.offset(paging.offset(), paging.limit());
            List<T> list = dao.queryList(listSql.getSql(), bizName, new TableInfoMapper<T>(table), listSql.getParams());
            paging.getList().addAll(list);
        }
        return paging;
    }

    @Override
    public <T> List<T> listLimit(final String bizName, T model, final String orderBy, final boolean asc, final long offset, final long limit) {
        if (limit > 100000) {
            throw new RuntimeException("limit to large");
        }
        TableInfo table = TableCache.get(model.getClass());
        SqlWhere listSql = new SqlWhere(format("select * from %s where 1", table.tableName));
        table.prepareWhere(model, listSql);
        if (orderBy != null) {
            listSql.orderBy(orderBy, asc);
        }
        listSql.offset(offset, limit);
        return dao.queryList(listSql.getSql(), bizName, new TableInfoMapper<>(table), listSql.getParams());
    }

    @Override
    public Long count(String bizName, String tableName) {
        SqlWhere countSql = new SqlWhere(format("select count(1) from %s where 1", tableName));
        return dao.queryLong(countSql.getSql(), bizName, 0);
    }

    @Override
    public <T> Long count(String bizName, T model) {
        final Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        SqlWhere countSql = new SqlWhere(format("select count(1) from %s where 1", table.tableName));
        table.prepareWhere(model, countSql);
        return dao.queryLong(countSql.getSql(), bizName, 0, countSql.getParams());
    }

    @Override
    public <T> IPage<T> listByMap(String bizName, Class<T> clazz, Map<String, Object> conditions, String orderBy, boolean asc, long page, long pageSize) {
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(orderBy, orderBy + " must not be null");
        SqlWhere countSql = new SqlWhere(format("select count(1) from %s where 1", table.getTableName()));
        table.prepareWhere(conditions, countSql);
        final long count = dao.queryLong(countSql.getSql(), bizName, 0, countSql.getParams());
        Pagination<T> paging = new Pagination<>(page, pageSize, count);
        if (paging.hasNext()) {
            SqlWhere listSql = new SqlWhere(format("select * from %s where 1", table.tableName));
            table.prepareWhere(conditions, countSql);
            listSql.orderBy(orderBy, asc).offset(paging.offset(), paging.limit());
            List<T> list = dao.queryList(listSql.getSql(), bizName, new TableInfoMapper<T>(table), listSql.getParams());
            paging.getList().addAll(list);
        }
        return paging;
    }

    public <T> T queryById(String bizName, Class<T> clazz, Serializable id) {
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        final String sql = format("select * from %s where %s = ?", table.tableName, table.primaryKey.column);
        return dao.queryUniq(sql, bizName, new TableInfoMapper<T>(table), id);
    }

    @Override
    public <T> List<T> queryByIds(String bizName, Class<T> clazz, Collection<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        SqlWhere w = new SqlWhere(format("select * from %s where 1", table.tableName));
        w.andIn(table.primaryKey.column, ids);
        return dao.queryList(w.getSql(), bizName, new TableInfoMapper<T>(table), w.getParams());
    }

    @Override
    public <T> T queryUniq(String bizName, T model) {
        Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        SqlWhere where = new SqlWhere(format("select * from %s where 1", table.tableName));
        table.prepareWhere(model, where);
        return dao.queryUniq(where, bizName, new TableInfoMapper<T>(table));
    }

    @Override
    public <T> List<T> queryUniqs(String bizName, T model) {
        Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        SqlWhere where = new SqlWhere(format("select * from %s where 1", table.tableName));
        table.prepareWhere(model, where);
        return dao.queryList(where, bizName, new TableInfoMapper<>(table));
    }

    @Override
    public <T> T queryUniqByMap(String bizName, Class<T> clazz, Map<String, Object> conditions) {
        TableInfo table = TableCache.get(clazz);
        SqlWhere where = new SqlWhere(format("select * from %s where 1", table.tableName));
        table.prepareWhere(conditions, where);
        return dao.queryUniq(where, bizName, new TableInfoMapper<T>(table));
    }

    @Override
    public <T> int updateById(String bizName, T model) {
        final Class<?> clazz = model.getClass();
        TableInfo table = TableCache.get(clazz);
        Objects.requireNonNull(table.primaryKey, clazz + " must contains `TableField(id=true)`");
        SqlWhere where = new SqlWhere(format("update %s set", table.tableName));
        table.prepareUpdateById(model, where);
        return dao.update(where.getSql(), bizName, where.getParams());
    }

    @Override
    public <T> LambdaService lambda(Class<T> clazz, String bizName) {
        return new LambdaService(clazz, bizName);

    }


}
