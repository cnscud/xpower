/**
 * 
 */
package com.cnscud.xpower.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接拦截器
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年4月21日
 */
@FunctionalInterface
public interface IConnectionHandler<T> {
    
    /**
     * 操作Connection，使用完后不需要关闭Connection，API会自动关闭数据库连接。 当然了如果你在连接内部打开了Statement/ResultSet等，还是需要自己手动关闭的。
     * 
     * @param conn
     *            数据库连接
     * @return 自定义的返回类型
     * @throws SQLException
     *             任何sql异常
     */
    T doInConnection(Connection conn) throws SQLException;
}
