package com.cnscud.xpower.dao;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.configcenter.SystemConfig;
import com.cnscud.xpower.ddd.DataSourceFactory;

/**
 * 数据库操作工具类<br />
 * 此操作支持泛型和自定义操作<br />
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
class Dao implements IDao {

    final Logger log = LoggerFactory.getLogger(IDao.class);
    
    /**保留连接不关闭*/
    private final boolean KEEP_CONNECTION = true;
    /**只读连接（从库）*/
    private final boolean READ_CONNECTION = true;

    protected volatile ISequence _sequence;

    private final boolean DEBUG = Boolean.getBoolean("xpower.dao.debug");

    protected Dao() {
    }

    protected ISequence getSequence() {
        if (_sequence == null) {
            synchronized (this) {
                if (_sequence == null) {
                    String sequenceClass = SystemConfig.getInstance().getString(ISequence.class.getName(), null);
                    if (sequenceClass != null) {
                        try {
                            _sequence = (ISequence) Class.forName(sequenceClass).newInstance();
                        } catch (InstantiationException e) {
                            throw new IllegalArgumentException(e);
                        } catch (IllegalAccessException e) {
                            throw new IllegalArgumentException(e);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            }
        }
        return _sequence;
    }

    protected void checkEmpty(String s, String msg) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    private long beginLog() {
        return log.isDebugEnabled() ? System.currentTimeMillis() : 0;
    }

    /**
     * 记录SQL语句执行状况
     * 
     * @param begin
     *            此次查询的开始时间（unix毫秒)
     * @param op
     *            操作符
     * @param rownum
     *            结果行数或者受影响记录行数
     */
    private void endLog(long begin, Op op, int rownum) {
        if (begin > 0) {
            log.debug(format("SQL_EXECUTE#%s#%s#%s#%d#%d", op.bizName, op.getRoutePattern(), op.sql, (System.currentTimeMillis() - begin), rownum));
        }
    }

    private void endLog(long begin, String bizName, String routePattern, String sql) {
        if (begin > 0) {
            log.debug(format("SQL_EXECUTE#%s#%s#%s#%d", bizName, routePattern, sql, (System.currentTimeMillis() - begin)));
        }
    }

    @Override
    public <T> T doWithConnection(OpWithConnection<T> op) {
        Connection conn = null;
        final long begin = beginLog();
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), op.isReadConnection, null);
            return op.doInConnection(conn);
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, -1);
            closeRSC(null, null, conn, !KEEP_CONNECTION);
        }
    }

    @Override
    public <T> T doWithConnection(String bizName, boolean isReadConnection, String sqlId, IConnectionHandler<T> handler) {
        Objects.requireNonNull(handler, "connection handler must not be null");
        Connection conn = null;
        final long begin = beginLog();
        try {
            conn = getConnection(bizName, bizName, isReadConnection, null);
            T t = handler.doInConnection(conn);
            return t;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            endLog(begin, bizName, bizName, sqlId);
            closeRSC(null, null, conn, !KEEP_CONNECTION);
        }
    }

    @Override
    public <T> T doWithTransaction(OpWithConnection<T> op) throws DaoException {
        Connection conn = null;
        final long begin = beginLog();
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), op.isReadConnection, op.getConnection());
            conn.setAutoCommit(false);
            try {
                T t = op.doInConnection(conn);
                conn.commit();
                return t;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, -1);
            closeRSC(null, null, conn, op.getConnection() != null);
        }
    }
    
    @Override
    public void doWithTransaction(String bizName, ITransactionHandler handler) {
        Objects.requireNonNull(handler, "transaction handler must not be null");
        Connection conn = null;
        final long begin = beginLog();
        try {
            conn = getConnection(bizName, bizName, !READ_CONNECTION, null);
            conn.setAutoCommit(false);
            try {
                handler.execute(new TransactionDao(bizName, conn, this));
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            endLog(begin, bizName, bizName, bizName + System.currentTimeMillis());
            closeRSC(null, null, conn, !KEEP_CONNECTION);
        }
    }

    @Override
    public <T> T doWithTransaction(String bizName, String sqlId, IConnectionHandler<T> handler) {
        Objects.requireNonNull(handler, "connection handler must not be null");
        Connection conn = null;
        final long begin = beginLog();
        try {
            conn = getConnection(bizName, bizName, !READ_CONNECTION, null);
            conn.setAutoCommit(false);
            try {
                T t = handler.doInConnection(conn);
                conn.commit();
                return t;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            endLog(begin, bizName, bizName, sqlId);
            closeRSC(null, null, conn, false);
        }
    }

    public <T> T queryResult(OpResult<T> op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            logSQL(ps);
            rs = ps.executeQuery();
            op.result = op.parse(rs);
            rownum = getSizeOfObject(op.result);
            return op.result;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    private int getSizeOfObject(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Collection<?>) {
            return ((Collection) o).size();
        }
        if (o instanceof Map<?, ?>) {
            return ((Map<?, ?>) o).size();
        }
        // IGNORE Iterable
        return -1;
    }

    public void queryWithResultset(OpWithResultset op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), op.isReadConnection, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            logSQL(ps);
            rs = ps.executeQuery();
            rownum = op.execute(rs);
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    public <T> List<T> queryList(OpList<T> op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            logSQL(ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                op.results.add(op.parse(rs, rownum));
                rownum++;
            }
            return op.results;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    @Override
    public int[] batchUpdate(OpBatchUpdate op) throws DaoException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), !READ_CONNECTION, op.connection);
            //todo mie 此处修改
            conn.setAutoCommit(false);//自动提交关闭 开启手动提交
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            int[] ret = ps.executeBatch();
            conn.commit();//手动提交
            conn.setAutoCommit(true);//再打开
            for (int i = 0; i < ret.length; i++) {
                rownum += ret[i];
            }
            return ret;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }
    @Override
    public int[] batchUpdate(final OpUpdate op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), !READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            int[] ret = ps.executeBatch();
            for (int i = 0; i < ret.length; i++) {
                rownum += ret[i];
            }
            return ret;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    private void logSQL(PreparedStatement ps) {
        if (log.isDebugEnabled()) {
            log.debug("SQL_REAL#" + ps.toString());
        }
    }

    public int update(final OpUpdate op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), !READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            logSQL(ps);
            rownum = ps.executeUpdate();
            return rownum;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {

            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T insert(final OpInsert<T> op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), !READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql, Statement.RETURN_GENERATED_KEYS);
            op.setParams(ps);
            logSQL(ps);
            rownum = ps.executeUpdate();
            if (rownum > 0) {
                rs = ps.getGeneratedKeys();
                return op.parsePrimaryKey(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {

            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    @Override
    public Long updateAndReturnLastInsertId(OpUpdate op) throws DaoException {
        return null;
    }

    public <T> T queryUniq(OpUniq<T> op) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        final long begin = beginLog();
        int rownum = 0;
        try {
            conn = getConnection(op.bizName, op.getRoutePattern(), READ_CONNECTION, op.connection);
            ps = conn.prepareStatement(op.sql);
            op.setParams(ps);
            logSQL(ps);
            rs = ps.executeQuery();
            if (rs.next()) {
                op.setResult(op.parse(rs));
                rownum = 1;
                if (rs.next()) {
                    rownum = 2;
                    throw new MultiResultException("more than one result for Uniq: " + op.toString(), op);
                }
            }
            return op.getResult();
        } catch (SQLException e) {
            throw new DaoException(e, op);
        } finally {
            endLog(begin, op, rownum);
            closeRSC(rs, ps, conn, op.connection != null);
        }
    }

    void closeRSC(final ResultSet rs, final Statement st, final Connection conn, final boolean keepConnection) {
        try {
            closeRSC0(rs, st, keepConnection ? null : conn);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private void closeRSC0(final ResultSet rs, final Statement st, final Connection conn) throws SQLException {
        try {
            if (rs != null) {
                rs.close();
            }
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    public DataSource getDataSource(final String bizName, final String pattern, final boolean isReadDataSource) {
        checkEmpty(bizName, "bizName must not be empty");
        DataSource ds = null;
        try {
            if (pattern == null) {
                if (isReadDataSource) {
                    ds = DataSourceFactory.getInstance().getReadDataSource(bizName);
                } else {
                    ds = DataSourceFactory.getInstance().getWriteDataSource(bizName);
                }
            } else {
                if (isReadDataSource) {
                    ds = DataSourceFactory.getInstance().getReadDataSource(bizName, pattern);
                } else {
                    ds = DataSourceFactory.getInstance().getWriteDataSource(bizName, pattern);
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e, new OpDefault(bizName, pattern, isReadDataSource));
        }
        if (ds == null) {
            throw new IllegalArgumentException("cannot fetch a datasource for bizName " + bizName);
        }
        return ds;
    }

    public Connection getConnection(final String bizName, final String pattern, final boolean isReadConnection,Connection defaultConnection) {
        if(defaultConnection != null) {
            return defaultConnection;
        }
        checkEmpty(bizName, "bizName must not be empty");
        Connection conn = null;
        try {
            if (pattern == null) {
                if (isReadConnection) {
                    conn = DataSourceFactory.getInstance().getReadConnection(bizName);
                } else {
                    conn = DataSourceFactory.getInstance().getWriteConnection(bizName);
                }
            } else {
                if (isReadConnection) {
                    conn = DataSourceFactory.getInstance().getReadConnection(bizName, pattern);
                } else {
                    conn = DataSourceFactory.getInstance().getWriteConnection(bizName, pattern);
                }
            }
            if (DEBUG && conn != null) {
                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new DaoException(e, new OpDefault(bizName, pattern, isReadConnection));
        }
        if (conn == null) {
            throw new IllegalArgumentException("cannot fetch a connection for bizName " + bizName);
        }
        return conn;
    }

    @Override
    public long getNextSequence(String sequenceName) {
        checkEmpty(sequenceName, "sequenceName must not be empty");
        if (getSequence() != null) {
            return getSequence().getNextSequence(sequenceName);
        }
        throw new UnsupportedOperationException("setting an implementation of interface('com.panda.xpower.dao.ISequence') in system config");
    }
}