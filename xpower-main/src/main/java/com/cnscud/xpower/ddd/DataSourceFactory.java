package com.cnscud.xpower.ddd;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.cnscud.xpower.configcenter.SystemConfig;
import com.cnscud.xpower.ddd.impl.DistDataSourceFactory;

/**
 * 
 */
public abstract class DataSourceFactory implements IDataSourceFactory {

    private static volatile IDataSourceFactory instance = null;

    public static IDataSourceFactory getInstance() {
        if (instance == null) {
            synchronized (DataSourceFactory.class) {
                if (instance == null) {
                    String clazz = SystemConfig.getInstance().getString(IDataSourceFactory.class.getName(), DistDataSourceFactory.class.getName());
                    Object object = null;
                    try {
                        object = Class.forName(clazz).newInstance();
                    } catch (InstantiationException e) {
                        throw new IllegalArgumentException(e);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                    if (object instanceof IDataSourceFactory) {
                        instance = (IDataSourceFactory) object;
                    } else {
                        throw new IllegalArgumentException("error 'IDataSourceFactory' class: " + clazz + ", must be " + IDataSourceFactory.class.getName());
                    }
                }
            }
        }
        return instance;
    }

    /**
     * @param instance the instance to set
     */
    public static void setDataSourceFactory(IDataSourceFactory instance) {
        DataSourceFactory.instance = instance;
    }

    private IDataSourceFactory connectionPool;

    public DataSourceFactory() {

    }

    public Connection getReadConnection(String name) throws SQLException {
        return connectionPool.getReadConnection(name);
    }

    public Connection getReadConnection(String name, String pattern) throws SQLException {
        return connectionPool.getReadConnection(name, pattern);
    }

    public DataSource getReadDataSource(String name) throws SQLException {
        return connectionPool.getReadDataSource(name);
    }

    public DataSource getReadDataSource(String name, String pattern) throws SQLException {
        return connectionPool.getReadDataSource(name, pattern);
    }

    public Connection getWriteConnection(String name) throws SQLException {
        return connectionPool.getWriteConnection(name);
    }

    public Connection getWriteConnection(String name, String pattern) throws SQLException {
        return connectionPool.getWriteConnection(name, pattern);
    }

    public DataSource getWriteDataSource(String name) throws SQLException {
        return connectionPool.getWriteDataSource(name);
    }

    public DataSource getWriteDataSource(String name, String pattern) throws SQLException {
        return connectionPool.getWriteDataSource(name, pattern);
    }

}
