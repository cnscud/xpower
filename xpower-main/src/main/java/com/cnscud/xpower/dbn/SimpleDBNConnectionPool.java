package com.cnscud.xpower.dbn;

import com.github.zkclient.IZkDataListener;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * The simple datasource pool.
 *
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-27
 */
public class SimpleDBNConnectionPool {

    final Logger logger = LoggerFactory.getLogger(getClass());


    private Map<String, DataSource> instances = new ConcurrentHashMap<>();
    private final Set<String> watcherSchema = new HashSet<String>();


    public DataSource getInstance(String bizName) {
        try {
            return findDbInstance(bizName);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection(String bizName) throws SQLException {
        DataSource ds = getDataSource(bizName);
        return ds.getConnection();
    }

    public DataSource getDataSource(String bizName) throws SQLException {
        return findDbInstance(bizName);
    }


    protected void destroyInstance(final String bizName) {
        synchronized (instances) {
            DataSource oldInstanceIf = instances.remove(bizName);
            logger.warn(format("destoryInstance %s and %s", bizName, oldInstanceIf != null ? "close datasource" : "do nothing"));
            if (oldInstanceIf != null) {
                closeDataSource(oldInstanceIf);
            }
        }
    }

    protected void closeDataSource(DataSource ds) {
        if (ds instanceof HikariDataSource) {
            try {
                ((HikariDataSource) ds).close();
            }
            catch (Exception e) {
                logger.error("Close datasource failed. ", e);
            }
        }
    }


    private DataSource createInstance(Map<String, String> dbcfg) {
        return new HikariDataSourceFactory().buildDataSource(dbcfg);
    }


    private DataSource findDbInstance(final String bizName) throws SQLException {
        DataSource ins = instances.get(bizName);
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
                Map<String, String> dbcfg = SchemeNodeHelper.getInstance(bizName);
                if (dbcfg == null) {
                    throw new SQLException("No such datasouce: " + bizName);
                }
                ins = createInstance(dbcfg);
                //log.warn("ins put "+ins);
                instances.put(bizName, ins);


                if (watcherSchema.add(bizName)) {
                    SchemeNodeHelper.watchInstance(bizName, new IZkDataListener() {

                        public void handleDataDeleted(String dataPath) throws Exception {
                            logger.warn(dataPath + " was deleted, so destroy the bizName " + bizName);
                            destroyInstance(bizName);
                        }

                        public void handleDataChange(String dataPath, byte[] data) throws Exception {
                            logger.warn(dataPath + " was changed, so destroy the bizName " + bizName);
                            destroyInstance(bizName);
                        }
                    });
                }
                success = true;
            }
            catch (SQLException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new SQLException("cannot build datasource for bizName: " + bizName, t);
            }
            finally {
                if (!success) {
                    instances.remove(bizName);
                }
            }
        }
        return ins;
    }

}
