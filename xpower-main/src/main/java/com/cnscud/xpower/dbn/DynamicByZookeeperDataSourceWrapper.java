package com.cnscud.xpower.dbn;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Datasource wrapper.
 *
 * @author Felix Zhang 2021-08-05 14:14
 * @version 1.0.0
 */
public class DynamicByZookeeperDataSourceWrapper implements DataSource {

    protected SimpleDBNConnectionPool simpleDBNConnectionPool;
    protected String bizName;

    public DynamicByZookeeperDataSourceWrapper(SimpleDBNConnectionPool simpleDBNConnectionPool, String bizName) {
        this.simpleDBNConnectionPool = simpleDBNConnectionPool;
        this.bizName = bizName;
    }

    protected DataSource pickDataSource() throws SQLException{
        return simpleDBNConnectionPool.getDataSource(bizName);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pickDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return pickDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return pickDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return pickDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return pickDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        pickDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        pickDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return pickDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
