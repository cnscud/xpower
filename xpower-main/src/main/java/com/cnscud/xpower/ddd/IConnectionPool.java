package com.cnscud.xpower.ddd;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 数据库连接池
 * 
 * @author adyliu (imxylz@gmail.com)
 * @sine 2011-7-5
 */
public interface IConnectionPool {

    /**
     * 获取一个数据源连接配置
     * 
     * @param bizName 业务名称
     * @return 数据源配置
     */
    IDataSourceInstance getInstance(String bizName);

    /**
     * 根据业务名称以及路由规则获取一个只读的连接
     * 
     * @param bizName 业务名称
     * @param pattern 路由规则
     * @return 数据库连接
     * @throws SQLException 任何数据库异常
     */
    Connection getReader(String bizName, String pattern) throws SQLException;

    /**
     * 根据业务名称以及路由规则获取一个只读的数据源
     * 
     * @param bizName 业务名称
     * @param pattern 路由规则
     * @return 数据源
     * @throws SQLException 任何数据库异常
     */
    DataSource getReaderDataSource(String bizName, String pattern) throws SQLException;

    /**
     * 根据业务名称以及路由规则获取一个可写的连接
     * 
     * @param bizName 业务名称
     * @param pattern 路由规则
     * @return 数据库连接
     * @throws SQLException 任何数据库异常
     */
    Connection getWriter(String bizName, String pattern) throws SQLException;

    /**
     * 根据业务名称以及路由规则获取一个可写的数据源
     * 
     * @param bizName 业务名称
     * @param pattern 路由规则
     * @return 数据源
     * @throws SQLException 任何数据库异常
     */
    DataSource getWriterDataSource(String bizName, String pattern) throws SQLException;
}
