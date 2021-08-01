package com.cnscud.xpower.mq.unified;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名的线程池
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年1月27日
 */
public class NamedThreadFactory implements ThreadFactory {

    static final AtomicInteger poolNumber = new AtomicInteger(1);

    final ThreadGroup group;

    final String namePrefix;
    final boolean daemon;

    final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String prefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix;
        this.daemon = daemon;
    }

    public Thread newThread(Runnable r) {
        String name = String.format("%s:%s#%s", namePrefix, poolNumber.getAndIncrement(), threadNumber.getAndIncrement());
        Thread t = new Thread(group, r, name, 0);
        t.setDaemon(this.daemon);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}