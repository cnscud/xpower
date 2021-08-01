package com.cnscud.xpower.dao;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * 带事务的SQL操作
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年3月29日
 */
class TransactionDao implements ITransactionDao {
    final Connection conn;
    final IDao dao;
    final String bizName;

    public TransactionDao(final String bizName, Connection conn, IDao dao) {
        this.bizName = bizName;
        this.conn = conn;
        this.dao = dao;
    }

    //
    public <T> T insert(CharSequence sql, Class<T> clazz, Object... params) throws DaoException {
        OpInsert<T> op = new OpInsert<>(sql, bizName, clazz).addParams(params);
        op.setConnection(conn);
        return dao.insert(op);
    }

    @Override
    public int update(CharSequence sql, Object... params) throws DaoException {
        OpUpdate op = new OpUpdate(sql, bizName).addParams(params);
        op.setConnection(conn);
        return dao.update(op);
    }

    @Override
    public <T> T queryUniq(CharSequence sql, IRowMapper<T> mapper, Object... params) throws DaoException, MultiResultException {
        OpUniq<T> op = new DefaultOpUniq<>(sql, bizName, mapper).addParams(params);
        op.setConnection(conn);
        return dao.queryUniq(op);
    }

    @Override
    public <T> List<T> queryList(CharSequence sql, IRowMapper<T> mapper, Object... params) {
        OpList<T> op = new DefaultOpList<>(sql, bizName, mapper).addParams(params);
        op.setConnection(conn);
        return dao.queryList(op);
    }

    @Override
    public <T> T queryResult(CharSequence sql, IResultHandler<T> resultHandler, Object... params) throws DaoException {
        OpResult<T> op = OpResult.create(sql, bizName, resultHandler).addParams(params);
        op.setConnection(conn);
        return dao.queryResult(op);
    }

    @Override
    public int[] batchUpdate(CharSequence sql, Collection<Collection<?>> params) {
        OpBatchUpdate op = new OpBatchUpdate(sql, bizName);
        op.addParamLists(params).setConnection(conn);
        return dao.batchUpdate(op);
    }

    @Override
    public String getBizName() {
        return bizName;
    }
    @Override
    public IDao getDao() {
        return dao;
    }
}
