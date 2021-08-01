package com.cnscud.xpower.ddd.impl;

import static java.lang.String.format;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.ddd.IDataSourceInstance;
import com.cnscud.xpower.ddd.schema.Instance;

public class ClusterInstance implements IDataSourceInstance {

    private RoundRobinWithWeight<DataSource> _readers = new RoundRobinWithWeight<DataSource>();

    private RoundRobinWithWeight<DataSource> _writers = new RoundRobinWithWeight<DataSource>();

    private volatile boolean init = false;

    private final Instance instance;

    final Logger logger = LoggerFactory.getLogger(getClass());

    

    public ClusterInstance(Instance instance) {
        this.instance = instance;
    }

    public String getBizName() {
        return instance.getName();
    }

    protected synchronized DataSource buildDbServer(Map<String, String> args, char c) {
        boolean buildok = false;
        try {
            AbstractConnectionFactory factory = AbstractConnectionFactory.getConnectionFactory(getBizName(), args);
            DataSource ds = factory.getDataSource();
            buildok = ds != null;
            return ds;
        } finally {
            if (logger.isDebugEnabled()) {
                Map<String, String> dargs = new HashMap<String, String>(args);
                dargs.put("password", "******");
                logger.info(format("build datasource [%s:%s] %s. %s", instance.getName(), c,//
                        buildok ? "OK" : "FAIL", buildok ? "" : dargs));
            }
        }

    }

    public void close() {
        logger.warn("close all database connections for " + instance.getName());
        for (DataSource writer : _writers.getAll()) {
            closeDataSource(writer);
        }
        for (DataSource reader : _readers.getAll()) {
            closeDataSource(reader);
        }
    }

    private void closeDataSource(DataSource ds) {
        if (ds instanceof BasicDataSource) {
            try {
                ((BasicDataSource) ds).close();
            } catch (SQLException e) {
                logger.error("Close datasource failed. ", e);
            }
        } else if (ds instanceof DataSourceExtend) {
            try {
                ((DataSourceExtend) ds).close();
            } catch (SQLException e) {
                logger.error("Close datasource failed. ", e);
            }
        }
    }

    public DataSource getReader(String pattern) {
        init();
        return _readers.get();
    }

    @Override
    public long getUpdatetime() {
        return instance.getUpdateTime();
    }

    public DataSource getWriter(String pattern) {
        init();
        return _writers.get();
    }

    private synchronized void init() {
        if (init) {
            return;
        }
        init = true;
        for (Map<String, String> options : instance.getParams()) {
            String rw = options.get("rw");
            if (rw.indexOf('r') > -1) {
                _readers.put(buildDbServer(options, 'r'), 1);
            }
            if (rw.indexOf('w') > -1) {
                _writers.put(buildDbServer(options, 'w'), 1);
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("SinglerInstance: ").append(instance.getName()).append(" ");
        return buf.toString();
    }
}
