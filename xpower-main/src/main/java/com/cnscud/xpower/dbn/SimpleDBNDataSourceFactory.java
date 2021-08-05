package com.cnscud.xpower.dbn;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 单例化, 方便调用.
 *
 */
public class SimpleDBNDataSourceFactory {

    private static final SimpleDBNDataSourceFactory instance = new SimpleDBNDataSourceFactory();

    private final SimpleDBNConnectionPool connectionPool = new SimpleDBNConnectionPool();

    public static SimpleDBNDataSourceFactory getInstance(){
        return instance;
    }

    public Connection getConnection(String name) throws SQLException {
        return connectionPool.getConnection(name);
    }


    public DataSource getDataSource(String name) throws SQLException {
        return new DynamicByZookeeperDataSourceWrapper(connectionPool, name);
    }


}
