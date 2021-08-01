package com.cnscud.xpower.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 使用{@link Connection}自己来操作数据库，通常情况下我们不需要关心连接的释放（关闭）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public class OpWithConnection<T> extends Op {

    final boolean isReadConnection;
    final IConnectionHandler<T> handler;

    /**
     * 构建一个数据库连接操作，允许自己控制数据库连接({@link Connection})的执行
     * 
     * @param bizName
     *            数据源名称，通常是业务名称
     * @param index
     *            散表的数据库索引，-1代表不散表
     * @param isReadConnection
     *            是否是只读连接，也就是是否是从库操作，如果有的话
     */
    public OpWithConnection(String bizName, long index, boolean isReadConnection) {
        this(bizName, index, isReadConnection, null);
    }

    /**
     * 构建一个数据库连接操作，允许自己控制数据库连接({@link Connection})的执行
     * 
     * @param bizName
     *            数据源名称，通常是业务名称
     * @param index
     *            散表的数据库索引，-1代表不散表
     * @param isReadConnection
     *            是否是只读连接，也就是是否是从库操作，如果有的话
     * @param handler
     *            数据库连接处理器
     */
    public OpWithConnection(String bizName, long index, boolean isReadConnection, IConnectionHandler<T> handler) {
        super("", bizName, index);
        this.isReadConnection = isReadConnection;
        this.handler = handler;
    }

    /**
     * 构建一个数据库连接操作，允许自己控制数据库连接({@link Connection})的执行
     * 
     * @param bizName
     *            数据源名称，通常是业务名称
     * @param isReadConnection
     *            是否是只读连接，也就是是否是从库操作，如果有的话
     * @param handler
     *            数据库连接处理器
     */
    public OpWithConnection(String bizName, boolean isReadConnection, IConnectionHandler<T> handler) {
        this(bizName, DEFAULT_INDEX, isReadConnection, handler);
    }

    /**
     * 构建一个数据库连接操作，允许自己控制数据库连接({@link Connection})的执行
     * 
     * @param bizName
     *            数据源名称，通常是业务名称
     * @param isReadConnection
     *            是否是只读连接，也就是是否是从库操作，如果有的话
     * @param handler
     *            数据库连接处理器
     */
    public OpWithConnection(String bizName, boolean isReadConnection) {
        this(bizName, DEFAULT_INDEX, isReadConnection, null);
    }

    /**
     * 操作Connection，使用完后不需要关闭Connection，API会自动关闭数据库连接。 当然了如果你在连接内部打开了Statement/ResultSet等，还是需要自己手动关闭的。
     * 
     * @param conn
     *            数据库连接
     * @return 自定义的返回类型
     * @throws SQLException
     *             任何sql异常
     */
    protected T doInConnection(Connection conn) throws SQLException {
        Objects.requireNonNull(handler, "the connection handler must not be null");
        return handler.doInConnection(conn);
    }

    protected final void setParams(PreparedStatement ps) throws SQLException {
        // 去掉此方法的调用，事实上此方法不会被调用
    }
}