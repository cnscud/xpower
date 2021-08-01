package com.cnscud.xpower.dao;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 操作实体类
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public abstract class Op {

    public final String sql;

    public final String bizName;

    private final long index;

    public static final int DEFAULT_INDEX = -1;

    private List<Object> paramList = null;

    private String routePattern = null;
    /**事务上下文Connection*/
    Connection connection;

    /**
     * 构建一个不带散库功能的数据库操作
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public Op(CharSequence sql, String bizName) {
        this(sql, bizName, DEFAULT_INDEX);
    }

    /**
     * 构建一个带指定散库功能的数据库操作
     * 
     * @param sql
     *            sql语句，可以是预编译的语句，也就是带参数的语句
     * @param bizName
     *            数据源名称，通常就是业务名称
     * @param index
     *            散库的数据库索引，-1代表不散库
     * @throws IllegalArgumentException
     *             如果bizName是空的话
     */
    public Op(CharSequence sql, String bizName, long index) {
        if (bizName == null || bizName.length() == 0) {
            throw new IllegalArgumentException("bizName must not be null");
        }
        this.sql = sql != null ? sql.toString() : null;
        this.bizName = bizName;
        this.index = index;
    }

    /**
     * 路由规则
     * <p>
     * 如果要自定义路由规则，覆盖此类即可
     * </p>
     * 
     * @return 默认情况下，如果index=-1，则不进行分库，否则路由规则为 <code>bizName+"_"+index</code>.
     */
    public String getRoutePattern() {
        if (routePattern == null) {
            routePattern = index == DEFAULT_INDEX ? bizName : bizName + "_" + index;
        }
        return routePattern;
    }

    /**
     * 设置路由规则
     * 
     * @param routePattern
     *            路由规则
     * @return 当前实例
     */
    public Op setRoutePattern(String routePattern) {
        this.routePattern = routePattern;
        return this;
    }

    /**
     * 设置预编译sql的参数，如果需要的话
     * 
     * @param ps
     *            预编译sql
     * @throws SQLException
     *             任何sql异常
     */
    protected void setParams(PreparedStatement ps) throws SQLException {
        if (paramList != null && paramList.size() > 0) {
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
        }
    }

    protected List<Object> getParamList() {
        if (paramList == null) {
            paramList = new ArrayList<Object>();
        }
        return paramList;
    }

    /**
     * 添加预编译参数。预编译参数严格对应于SQL中的?位置
     * 
     * @param params
     *            预编译参数
     * @return 当前实例
     */
    public Op addParams(Object... params) {
        if (params != null) {
            for (Object arg : params) {
                if (arg instanceof Collection<?>) {
                    getParamList().addAll((Collection<?>) arg);
                } else if (arg instanceof Iterable<?>) {
                    for (Object x : (Iterable<?>) arg) {
                        getParamList().add(x);
                    }
                } else if (arg != null && arg.getClass().isArray()) {
                    int len = Array.getLength(arg);
                    for (int i = 0; i < len; i++) {
                        getParamList().add(Array.get(arg, i));
                    }
                } else {
                    getParamList().add(arg);
                }
            }
        }
        return this;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "Op [bizName=" + bizName + ", routePattern=" + getRoutePattern() + ", sql=" + sql + "]";
    }
}
