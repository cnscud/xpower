package com.cnscud.xpower.ddd.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

/**
 * @author baa Peng 2020-12-18 14:12
 * @version 1.0.0
 */
public abstract class AbstractHikariCpConnectionFactory extends AbstractConnectionFactory {

    public AbstractHikariCpConnectionFactory(String bizName, Map<String, String> args) {
        super(bizName);
        HikariDataSource basicDataSource = buildDataSource(args);
        setDataSource(new DataSourceAdapter(bizName, basicDataSource));
    }

    protected HikariDataSource buildDataSource(Map<String, String> args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getUrl(args));
        config.setUsername(args.get(USER));
        config.setPassword(args.get(PASSWORD));
        config.setDriverClassName(getDriverClassName());
        int mathMax = Math.max(Integer.parseInt(args.get(MAX)), 250);
        config.addDataSourceProperty("cachePrepStmts", "true"); //是否自定义配置，为true时下面两个参数才生效
        config.addDataSourceProperty("prepStmtCacheSize", mathMax); //连接池大小默认25，官方推荐250-500
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); //单条语句最大长度默认256，官方推荐2048
        config.addDataSourceProperty("useServerPrepStmts", "true"); //新版本MySQL支持服务器端准备，开启能够得到显著性能提升
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.setMaximumPoolSize(mathMax / 10); //最大闲置连接数，默认为最大连接数的1/10
        config.setMinimumIdle(10);//最小闲置连接数，默认为0
        config.setMaxLifetime(600000);//最大生存时间
        config.setConnectionTimeout(30000);//超时时间30秒
        config.setIdleTimeout(60000);
        config.setConnectionTestQuery("select 1");
        //logger.info("mysql hikari max {} getUrl {}",mathMax,getUrl(args));
        return new HikariDataSource(config);
    }

    protected String getUrl(Map<String, String> args) {
        return args.get(URL);
    }

    protected abstract String getDriverClassName();
}
