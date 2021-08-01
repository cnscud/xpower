/**
 * $Id: SingleProxyDataSource.java 535 2011-09-08 08:39:10Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import com.cnscud.xpower.ddd.DataSourceFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-9-8
 */
public class SingleProxyDataSource implements DataSource {

    final String bizName;

    /**
     */
    public SingleProxyDataSource(String bizName) {
        this.bizName = bizName;
        if (bizName == null) {
            throw new IllegalArgumentException("bizName must not be null");
        }
    }

    protected DataSource getDataSource() throws SQLException {
        return DataSourceFactory.getInstance().getWriteDataSource(bizName);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    // compatibility between JDK6 and 7/8
    // see http://www.oracle.com/technetwork/java/javase/compatibility-417013.html
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
