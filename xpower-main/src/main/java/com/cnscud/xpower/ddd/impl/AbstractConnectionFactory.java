package com.cnscud.xpower.ddd.impl;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.ddd.impl.specification.HsqldbConnectionFactory;
import com.cnscud.xpower.ddd.impl.specification.MySqlConnectionFactory;
import com.cnscud.xpower.ddd.impl.specification.OracleConnectionFactory;

/**
 * Abstract Connection Factory
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-5
 */
public abstract class AbstractConnectionFactory implements ConnectionProperties {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DataSource dataSource;

    protected String bizName;

    public String getBizName() {
        return bizName;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected AbstractConnectionFactory(String bizName) {
        this.bizName = bizName;
    }

    public static AbstractConnectionFactory getConnectionFactory(String bizName, Map<String, String> args) {
        final String type = args.get("type");
        if ("mysql".equals(type)) {
            return new MySqlConnectionFactory(bizName, args);
        } else if ("hsqldb".equals(type)) {
            return new HsqldbConnectionFactory(bizName, args);
        } else if ("oracle".equals(type)) {
            return new OracleConnectionFactory(bizName, args);
        }
        throw new IllegalArgumentException("unknown database type: " + type);
    }

    protected String getArg(Map<String, String> args, String name, String defaultValue) {
        if (args.containsKey(name)) {
            return args.get(name);
        }
        return defaultValue;
    }
}
