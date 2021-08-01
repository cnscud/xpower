/**
 * $Id: MultiProxyDataSourceFactory.java 309 2011-07-28 03:51:03Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.cnscud.xpower.ddd.IDataSourceFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-27
 */
public class MultiProxyDataSourceFactory extends DistDataSourceFactory implements IDataSourceFactory {

    protected Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

    public MultiProxyDataSourceFactory() {
    }

    public MultiProxyDataSourceFactory(Map<String, DataSource> dataSources) {
        super();
        this.dataSources = dataSources;
    }

    protected DataSource getDataSource(String name) {
        return getDataSources().get(name);
    }

    protected DataSource getDataSource(String name, String pattern) {
        return getDataSources().get(pattern);
    }

    /**
     * @return the dataSource
     */
    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    @Override
    public Connection getReadConnection(String name) throws SQLException {
        DataSource ds = getDataSource(name);
        if (ds == null) {
            return super.getReadConnection(name);
        } else {
            return ds.getConnection();
        }
    }

    @Override
    public Connection getReadConnection(String name, String pattern) throws SQLException {
        DataSource ds = getDataSource(name, pattern);
        if (ds == null) {
            return super.getReadConnection(name, pattern);
        } else {
            return ds.getConnection();
        }
    }

    @Override
    public DataSource getReadDataSource(String name) throws SQLException {
        DataSource ds = getDataSource(name);
        if (ds == null) {
            return super.getReadDataSource(name);
        } else {
            return ds;
        }
    }

    @Override
    public DataSource getReadDataSource(String name, String pattern) throws SQLException {
        DataSource ds = getDataSource(name, pattern);
        if (ds == null) {
            return super.getReadDataSource(name, pattern);
        } else {
            return ds;
        }
    }

    @Override
    public Connection getWriteConnection(String name) throws SQLException {
        DataSource ds = getDataSource(name);
        if (ds == null) {
            return super.getWriteConnection(name);
        } else {
            return ds.getConnection();
        }
    }

    @Override
    public Connection getWriteConnection(String name, String pattern) throws SQLException {
        DataSource ds = getDataSource(name, pattern);
        if (ds == null) {
            return super.getWriteConnection(name, pattern);
        } else {
            return ds.getConnection();
        }
    }

    @Override
    public DataSource getWriteDataSource(String name) throws SQLException {
        DataSource ds = getDataSource(name);
        return ds != null ? ds : super.getWriteDataSource(name);
    }

    @Override
    public DataSource getWriteDataSource(String name, String pattern) throws SQLException {
        DataSource ds = getDataSource(name, pattern);
        return ds != null ? ds : super.getWriteDataSource(name, pattern);
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSources(Map<String, DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
