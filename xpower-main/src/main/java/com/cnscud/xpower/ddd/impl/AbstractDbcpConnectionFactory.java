/*
package com.cnscud.xpower.ddd.impl;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

*/
/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-5
 *//*

public abstract class AbstractDbcpConnectionFactory extends AbstractConnectionFactory {

    BasicDataSource basicDataSource = null;

    public AbstractDbcpConnectionFactory(String bizName, Map<String, String> args) {
        super(bizName);
        basicDataSource = buildDataSource(args);
        setDataSource(new DataSourceAdapter(bizName, basicDataSource));
    }

    protected Map<String, String> prepareArgs(Map<String, String> args) {
        if (!args.containsKey(MIN)) {
            args.put(MIN, "0");//
        }
        if (!args.containsKey(MAX)) {
            args.put(MAX, "100");//
        }
        if (!args.containsKey(INIT)) {
            args.put(INIT, "0");//
        }
        return args;
    }

    protected BasicDataSource buildDataSource(Map<String, String> args) {
        args = prepareArgs(args);// 二次处理参数
        BasicDataSource ds = new BasicDataSource();
        ds.setDefaultAutoCommit(true);
        ds.setUsername(args.get(USER));
        ds.setPassword(args.get(PASSWORD));
        ds.setUrl(getUrl(args));
        ds.setDriverClassName(getDriverClassName());
        //
        int min = Integer.parseInt(args.get(MIN));
        int max = Integer.parseInt(args.get(MAX));
        ds.setInitialSize(Integer.parseInt(args.get(INIT)));// 初始连接池大小
        ds.setMaxActive(max);// 最大支持100个活动连接
        ds.setMinIdle(min);// 最小闲置连接数，默认为0
        ds.setMaxIdle(max > 10 ? max / 10 : max);// 最大闲置连接数，默认为最大连接数的1/10
        ds.setMaxWait(1000L);// 向线程池借一个线程时最大等待多长时间，如果超过此时间借不到线程则抛出一个异常
        ds.setTestOnReturn(false);
        if (args.containsKey(VALIDATIONQUERY)) {
            ds.setValidationQuery(args.get(VALIDATIONQUERY));
            ds.setTestWhileIdle(true);// 闲置时检测连接是否有效
        }
        ds.setMinEvictableIdleTimeMillis(10 * 1000L);// 闲置连接被踢时间，默认10秒
        ds.setNumTestsPerEvictionRun(10);// 默认一次检测多少个闲置连接数是否有效，默认10个
        ds.setTimeBetweenEvictionRunsMillis(30 * 1000L);// 多少时间检测一次闲置连接是否该踢出（减少闲置连接），默认30秒
        return ds;
    }

    protected String getUrl(Map<String, String> args) {
        return args.get(URL);
    }

    protected abstract String getDriverClassName();

}
*/
