package com.cnscud.xpower.ddd;


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 数据源工厂，通过此工厂可以获取只读、可写的数据库连接池或者数据库连接
 * 
 */
public interface IDataSourceFactory {
	
	String EMPTY_PATTERN = "";

    /**
     * 获取一个只读的数据库连接（通常是丛库）
     * 
     * @param name 实例名称
     * @return 数据库连接
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    Connection getReadConnection(String name) throws SQLException;

    /**
     * 获取一个只读的数据库连接（通常是丛库）
     * 
     * @param name 实例名称
     * @param pattern 匹配规则（这在有路由规则/散库规则时有效）
     * @return 数据库连接
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    Connection getReadConnection(String name, String pattern) throws SQLException;

    /**
     * 获取一个只读的数据库连接池（通常是丛库）
     * 
     * @param name 实例名称
     * @return 数据库连接池
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    DataSource getReadDataSource(String name) throws SQLException;

    /**
     * 获取一个只读的数据库连接池（通常是丛库）
     * 
     * @param name 实例名称
     * @param pattern 匹配规则（这在有路由规则/散库规则时有效）
     * @return 数据库连接池
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    DataSource getReadDataSource(String name, String pattern) throws SQLException;

    /**
     * 获取一个可写的数据库连接（通常是主库）
     * 
     * @param name 实例名称
     * @return 数据库连接
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    Connection getWriteConnection(String name) throws SQLException;

    /**
     * 获取一个可写的数据库连接（通常是主库）
     * 
     * @param name 实例名称
     * @param pattern 匹配规则（这在有路由规则/散库规则时有效）
     * @return 数据库连接
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    Connection getWriteConnection(String name, String pattern) throws SQLException;

    /**
     * 获取一个可写的数据库连接池（通常是主库）
     * 
     * @param name 实例名称
     * @return 数据库连接池
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    DataSource getWriteDataSource(String name) throws SQLException;

    /**
     * 获取一个可写的数据库连接池（通常是主库）
     * 
     * @param name 实例名称
     * @param pattern 匹配规则（这在有路由规则/散库规则时有效）
     * @return 数据库连接池
     * @throws SQLException 任何数据库异常或者不存在匹配名称的实例
     */
    DataSource getWriteDataSource(String name, String pattern) throws SQLException;
}


