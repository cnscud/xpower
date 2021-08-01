package com.cnscud.xpower.configcenter;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年2月26日
 */
public interface IDistributedLock {

    void lock() throws LockingException;

    boolean tryLock(long timeout, TimeUnit unit);

    void unlock() throws LockingException;

    class LockingException extends RuntimeException {
        public LockingException(String msg, Exception e) {
            super(msg, e);
        }

        public LockingException(String msg) {
            super(msg);
        }
    }
}
