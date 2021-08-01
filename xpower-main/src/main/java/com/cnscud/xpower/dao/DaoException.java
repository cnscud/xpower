package com.cnscud.xpower.dao;
/**
 * DAO操作的异常
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-18
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 3407678156140162583L;

    private Op op;

    public DaoException() {
    }

    /**
     * @param message
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(Throwable cause, Op op) {
        super(cause);
        this.op = op;
    }

    /**
     * @param message
     * @param cause
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message, Throwable cause, Op op) {
        super(message, cause);
        this.op = op;
    }

    public DaoException(String message, Op op) {
        super(message);
        this.op = op;
    }

    /**
     * DAO操作的实体
     * 
     * @return the op 操作实体
     */
    public Op getOp() {
        return op;
    }
}