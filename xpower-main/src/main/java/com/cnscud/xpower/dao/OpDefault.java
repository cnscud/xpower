package com.cnscud.xpower.dao;

/**
 * 默认的参数类型
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-8-9
 */
public class OpDefault extends Op {

    final String pattern;

    final boolean readConnection;

    /**
     * 默认的Op操作
     * 
     * @param bizName
     *            业务名称
     * @param pattern
     *            路由规则
     * @param isReadConnection
     *            是否是只读
     */
    public OpDefault(String bizName, String pattern, boolean isReadConnection) {
        super(null, bizName);
        this.pattern = pattern;
        this.readConnection = isReadConnection;
    }

    @Override
    public String getRoutePattern() {
        return pattern;
    }

    /**
     * @return the isReadConnection
     */
    public boolean isReadConnection() {
        return readConnection;
    }

}