package com.cnscud.xpower.dao;
/**
 * 多结果异常，如果返回的结果集多余一条时抛出此异常，通常用户查询一个唯一结果时得到多个结果。
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-11
 */
public class MultiResultException extends DaoException {

    private static final long serialVersionUID = -6577504007746939491L;

    public MultiResultException() {
    }

    public MultiResultException(String s) {
        super(s);
    }

    public MultiResultException(String s, Op op) {
        super(s, op);
    }

    public MultiResultException(Throwable cause) {
        super(cause);
    }

    public MultiResultException(String message, Throwable cause) {
        super(message, cause);
    }
}