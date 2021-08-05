/**
 * $Id: DistDataSourceFactory.java 301 2011-07-27 10:56:48Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.dbn;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SimpleDBNDataSourceFactory {

    private static SimpleDBNDataSourceFactory instance = new SimpleDBNDataSourceFactory();

    private final SimpleDBNConnectionPool connectionPool = new SimpleDBNConnectionPool();

    public static SimpleDBNDataSourceFactory getInstance(){
        return instance;
    }

    public Connection getConnection(String name) throws SQLException {
        return connectionPool.getConnection(name);
    }


    public DataSource getDataSource(String name) throws SQLException {
        return connectionPool.getDataSource(name);
    }


}
