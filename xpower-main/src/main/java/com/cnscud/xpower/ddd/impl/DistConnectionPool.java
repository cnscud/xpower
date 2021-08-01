package com.cnscud.xpower.ddd.impl;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zkclient.IZkDataListener;
import com.cnscud.xpower.ddd.IConnectionPool;
import com.cnscud.xpower.ddd.IDataSourceInstance;
import com.cnscud.xpower.ddd.schema.Instance;
import com.cnscud.xpower.ddd.schema.SchemaInstanceHelper;

/**
 * The default datasource pool
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-27
 */
public class DistConnectionPool implements IConnectionPool {

    final Logger log = LoggerFactory.getLogger(getClass());

    

    private Map<String, IDataSourceInstance> instances = new ConcurrentHashMap<String, IDataSourceInstance>();
    private final Set<String> watcherSchema = new HashSet<String>();

    private IDataSourceInstance createInstance(Instance instance) {
        switch (instance.getType()) {
        case CLUSTER:
            return new ClusterInstance(instance);
        case ROUTE:
            return new RouterInstance(this, instance);
        default:
            break;
        }
        throw new NullPointerException("Instance type must be either cluster or router: " + instance);
    }

    @Override
    public IDataSourceInstance getInstance(String bizName) {
        try {
            return findDbInstance(bizName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Connection getReader(String bizName, String pattern) throws SQLException {
        DataSource ds = getReaderDataSource(bizName, pattern);
        return ds.getConnection();
    }

    @Override
    public DataSource getReaderDataSource(String bizName, String pattern) throws SQLException {
        IDataSourceInstance ins = findDbInstance(bizName);
        return ins.getReader(pattern);
    }

    @Override
    public Connection getWriter(String bizName, String pattern) throws SQLException {
        DataSource ds = getWriterDataSource(bizName, pattern);
        return ds.getConnection();
    }

    @Override
    public DataSource getWriterDataSource(String bizName, String pattern) throws SQLException {
        IDataSourceInstance ins = findDbInstance(bizName);
        return ins.getWriter(pattern);
    }

    protected void destroyInstance(final String bizName) {
        synchronized (instances) {
            IDataSourceInstance oldInstanceIf = instances.remove(bizName);
            log.warn(format("destoryInstance %s and %s", bizName, oldInstanceIf != null ? "close datasource" : "do nothing"));
            if (oldInstanceIf != null) {
                oldInstanceIf.close();
            }
        }
    }

    private IDataSourceInstance findDbInstance(final String bizName) throws SQLException {
        IDataSourceInstance ins = instances.get(bizName);
        if (ins != null) {
            return ins;
        }
        synchronized (instances) {// 同步操作
            ins = instances.get(bizName);
            if (ins != null) {
                return ins;
            }
            boolean success = false;
            try {
                Instance db = SchemaInstanceHelper.getInstance(bizName);
                if (db == null) {
                    throw new SQLException("No such datasouce: " + bizName);
                }
                ins = createInstance(db);
                //log.warn("ins put "+ins);
                instances.put(bizName, ins);
                if (watcherSchema.add(bizName)) {
                    SchemaInstanceHelper.watchInstance(bizName, new IZkDataListener() {

                        public void handleDataDeleted(String dataPath) throws Exception {
                            log.warn(dataPath + " was deleted, so destroy the bizName " + bizName);
                            destroyInstance(bizName);
                        }

                        public void handleDataChange(String dataPath, byte[] data) throws Exception {
                            log.warn(dataPath + " was changed, so destroy the bizName " + bizName);
                            destroyInstance(bizName);
                        }
                    });
                }
                success = true;
            } catch (SQLException e) {
                throw e;
            } catch (Throwable t) {
                throw new SQLException("cannot build datasource for bizName: " + bizName, t);
            } finally {
                if (!success) {
                    instances.remove(bizName);
                }
            }
        }
        return ins;
    }

}
