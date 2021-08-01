package com.cnscud.xpower.ddd.impl;

import java.util.Map;

import javax.sql.DataSource;

import com.cnscud.xpower.ddd.IConnectionPool;
import com.cnscud.xpower.ddd.IDataSourceInstance;
import com.cnscud.xpower.ddd.schema.Instance;

/**
 * 带有路由功能的数据源
 */
public class RouterInstance implements IDataSourceInstance {

    private final Instance instance;

    private final IConnectionPool connectionPool;

    public RouterInstance(IConnectionPool pool, Instance instance) {
        this.connectionPool = pool;
        this.instance = instance;
    }

    @Override
    public String getBizName() {
        return instance.getName();
    }

    public void close() {
        // Do nothing for Router.
    }

    protected IDataSourceInstance findInstance(String pattern) {
        for (Map<String, String> route : instance.getParams()) {
            if (pattern.matches(route.get("expression"))) {
                return connectionPool.getInstance(route.get("instance"));
            }
        }
        throw new IllegalArgumentException("NOT FOUND datasource for pattern: " + pattern);
    }

    public DataSource getReader(String pattern) {
        return findInstance(pattern).getReader(pattern);
    }

    public long getUpdatetime() {
        return instance.getUpdateTime();
    }

    public DataSource getWriter(String pattern) {
        return findInstance(pattern).getWriter(pattern);
    }

    @Override
    public String toString() {
        return "RouterInstance: " + instance.getName();
    }
}
