/**
 * 
 */
package com.cnscud.xpower.dao;

import java.sql.SQLException;

/**
 * 单数据库事务操作
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年3月29日
 */
@FunctionalInterface
public interface ITransactionHandler {

    /**
     * 操作事务DAO，同一个业务名称bizName下，无须关心数据库连接
     * 
     * @param transactionDao
     *            数据库操作DAO
     * @throws SQLException
     *             任何sql异常
     */
    void execute(ITransactionDao transactionDao) throws SQLException;
}
