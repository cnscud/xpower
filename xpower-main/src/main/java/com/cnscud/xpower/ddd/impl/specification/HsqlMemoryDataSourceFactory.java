/**
 * $Id: HsqlMemoryDataSourceFactory.java 282 2011-07-25 04:09:36Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl.specification;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.cnscud.xpower.ddd.impl.SingleProxyDataSourceFactory;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @sine 2011-7-24
 */
public class HsqlMemoryDataSourceFactory extends SingleProxyDataSourceFactory {

    private boolean defaultAutoCommit = true;

    private String username;

    private String password;

    private String url;

    private String driverClassName;

    @Override
    public DataSource getDataSource() {
        if (super.getDataSource() == null) {
            BasicDataSource ds = new BasicDataSource();
            ds.setDefaultAutoCommit(defaultAutoCommit);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setUrl(url);
            ds.setDriverClassName(driverClassName);
            setDataSource(ds);
        }
        return super.getDataSource();
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

}
