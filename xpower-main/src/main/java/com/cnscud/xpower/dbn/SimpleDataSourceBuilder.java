package com.cnscud.xpower.dbn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Hikari DataSource.
 * 后续: 可以根据参数来使用不同的DataSource, 例如Druid.
 *
 * @fixme: 优化参数
 *
 * @author Felix Zhang 2021-08-05 11:14
 * @version 1.0.0
 */
public class SimpleDataSourceBuilder {


    public HikariDataSource buildDataSource(Map<String, String> args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getUrl(args));
        config.setUsername(args.get("username"));
        config.setPassword(args.get("password"));
        config.setDriverClassName(getDriverClassName(args));

        String maximumPoolSizeKey = "maximum-pool-size";
        int maximumPoolSize = 30;
        if(StringUtils.isNotEmpty(args.get(maximumPoolSizeKey))){
            maximumPoolSize = Integer.parseInt(args.get(maximumPoolSizeKey));
        }

        config.addDataSourceProperty("cachePrepStmts", "true"); //是否自定义配置，为true时下面两个参数才生效
        config.addDataSourceProperty("prepStmtCacheSize", maximumPoolSize); //连接池大小默认25，官方推荐250-500
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); //单条语句最大长度默认256，官方推荐2048
        config.addDataSourceProperty("useServerPrepStmts", "true"); //新版本MySQL支持服务器端准备，开启能够得到显著性能提升
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        config.setMaximumPoolSize(maximumPoolSize); //
        config.setMinimumIdle(10);//最小闲置连接数，默认为0
        config.setMaxLifetime(600000);//最大生存时间
        config.setConnectionTimeout(30000);//超时时间30秒
        config.setIdleTimeout(60000);

        config.setConnectionTestQuery("select 1");
        //logger.info("mysql hikari max {} getUrl {}",mathMax,getUrl(args));

        return new HikariDataSource(config);
    }

    private String getDriverClassName(Map<String, String> args) {
        return args.get("driver-class-name");
    }

    private String getUrl(Map<String, String> args) {
        return args.get("url");
    }


}
