/**
 * $Id: SingleProxyDataSourceFactory.java 309 2011-07-28 03:51:03Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.cnscud.xpower.ddd.IDataSourceFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-24
 */
public class SingleProxyDataSourceFactory implements IDataSourceFactory {

    protected DataSource dataSource;

    public SingleProxyDataSourceFactory() {
    }

    public SingleProxyDataSourceFactory(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getReadConnection(String name) throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getReadConnection(String name, String pattern) throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public DataSource getReadDataSource(String name) throws SQLException {
        return getDataSource();
    }

    @Override
    public DataSource getReadDataSource(String name, String pattern) throws SQLException {
        return getDataSource();
    }

    @Override
    public Connection getWriteConnection(String name) throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getWriteConnection(String name, String pattern) throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public DataSource getWriteDataSource(String name) throws SQLException {
        return getDataSource();
    }

    @Override
    public DataSource getWriteDataSource(String name, String pattern) throws SQLException {
        return getDataSource();
    }
}
