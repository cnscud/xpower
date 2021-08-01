/**
 * $Id: DataSourceAdapter.java 766 2011-10-26 10:25:20Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代理数据源（拦截密码显示？）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-10-26
 */
class DataSourceAdapter implements DataSourceExtend {

    private final HikariDataSource target;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    
    private final String bizName;


    public DataSourceAdapter(final String bizName, HikariDataSource target) {
        this.bizName = bizName;
        this.target = target;
    }

    @Override
    public Connection getConnection() throws SQLException {
        boolean error = false;
        try {
            return target.getConnection();
        } catch (SQLException ex) {
            error = true;
            throw ex;
        } finally {
            logStatus(error);
        }
    }

    private void logStatus(boolean error) {
        if (error || logger.isDebugEnabled()) {
            logger.info(String.format("datasource_status [%s] numActive=%s, numIdel=%s", //
                    bizName, //
                    target.getMaximumPoolSize(), //
                    target.getMinimumIdle()));
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        boolean error = false;
        try {
            return target.getConnection(username, password);
        } catch (SQLException ex) {
            error = true;
            throw ex;
        } finally {
            logStatus(error);
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return target.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        target.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        target.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return target.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return target.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return target.isWrapperFor(iface);
    }

    @Override
    public void close() throws SQLException {
        target.close();
    }

    // compatibility between JDK6 and 7/8
    // see http://www.oracle.com/technetwork/java/javase/compatibility-417013.html
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

}
