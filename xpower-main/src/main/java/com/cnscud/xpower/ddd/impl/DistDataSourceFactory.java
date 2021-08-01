/**
 * $Id: DistDataSourceFactory.java 301 2011-07-27 10:56:48Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.cnscud.xpower.ddd.IConnectionPool;
import com.cnscud.xpower.ddd.IDataSourceFactory;

public class DistDataSourceFactory implements IDataSourceFactory {

    private final IConnectionPool connectionPool = new DistConnectionPool();

    public Connection getReadConnection(String name) throws SQLException {
        return connectionPool.getReader(name, EMPTY_PATTERN);
    }

    public Connection getReadConnection(String name, String pattern) throws SQLException {
        return connectionPool.getReader(name, pattern);
    }

    public DataSource getReadDataSource(String name) throws SQLException {
        return connectionPool.getReaderDataSource(name, EMPTY_PATTERN);
    }

    public DataSource getReadDataSource(String name, String pattern) throws SQLException {
        return connectionPool.getReaderDataSource(name, pattern);
    }

    public Connection getWriteConnection(String name) throws SQLException {
        return connectionPool.getWriter(name, EMPTY_PATTERN);
    }

    public Connection getWriteConnection(String name, String pattern) throws SQLException {
        return connectionPool.getWriter(name, pattern);
    }

    public DataSource getWriteDataSource(String name) throws SQLException {
        return connectionPool.getWriterDataSource(name, EMPTY_PATTERN);
    }

    public DataSource getWriteDataSource(String name, String pattern) throws SQLException {
        return connectionPool.getWriterDataSource(name, pattern);
    }
}
