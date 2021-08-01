package com.cnscud.xpower.utils;

import static java.lang.String.format;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多线程执行工具
 * 
 * @author adyliu (adyliu@sohu-inc.com)
 * @since 2011-5-18
 */
public class RunnerUtils {

    static class Monitor implements Runnable {

        final ThreadPoolExecutor executor;

        public Monitor(ThreadPoolExecutor executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            while (!executor.isShutdown()) {
                if (log.isDebugEnabled()) {
                    if (executor != null) {
                        log.debug("ThreadPoolExecutor activeCount={} coreSize={} maxSize={} largetsSize={} queueSize={} reminning={} completedCount={}",//
                                executor.getActiveCount(),//
                                executor.getCorePoolSize(),//
                                executor.getMaximumPoolSize(),//
                                executor.getLargestPoolSize(),//
                                //
                                executor.getQueue().size(),//
                                executor.getQueue().remainingCapacity(), //
                                executor.getCompletedTaskCount()//
                        );
                    }
                    if (scheduledExecutor != null) {
                        log.debug("ScheduledThreadPoolExecutor activeCount={} coreSize={} maxSize={} largetsSize={} queueSize={} reminning={} completedCount={}",//
                                scheduledExecutor.getActiveCount(),//
                                scheduledExecutor.getCorePoolSize(),//
                                scheduledExecutor.getMaximumPoolSize(),//
                                scheduledExecutor.getLargestPoolSize(),//
                                scheduledExecutor.getQueue().size(),//
                                scheduledExecutor.getQueue().remainingCapacity(),//
                                scheduledExecutor.getCompletedTaskCount()//
                        );
                    }
                }
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }

    private static volatile ThreadPoolExecutor executor;

    private static final Logger log = LoggerFactory.getLogger(RunnerUtils.class);

    private static volatile ScheduledThreadPoolExecutor scheduledExecutor;

    private static void initExecutor() {
        if (executor == null) {
            synchronized (RunnerUtils.class) {
                if (executor == null) {
                    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(11, 100, 1, TimeUnit.MINUTES, //
                            new ArrayBlockingQueue<Runnable>(10000),//
                            new NamedThreadFactory("runner-",false)) {
                        protected void afterExecute(Runnable r, Throwable t) {
                            super.afterExecute(r, t);
                            printException(r, t);
                        }
                    };
                    executor = threadPoolExecutor;
                    // not use ScheduledExecutorService thread(waste resource)
                    executor.submit(new Monitor((ThreadPoolExecutor) executor));
                }
            }
        }
    }

    private static void initScheduledExecutor() {
        if (scheduledExecutor == null) {
            synchronized (RunnerUtils.class) {
                if (scheduledExecutor == null) {
                    scheduledExecutor = new ScheduledThreadPoolExecutor(20, new NamedThreadFactory("runner-", false)) {
                        protected void afterExecute(Runnable r, Throwable t) {
                            super.afterExecute(r, t);
                            printException(r, t);
                        }
                    };
                    scheduledExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
                    scheduledExecutor.allowCoreThreadTimeOut(true);
                }
            }
        }
    }

    /**
     * 输出任务异常堆栈
     * <p>
     * 线程池默认捕获了所有异常，通常通过feture.get()能获取到异常，如果调用者没有主动获取异常，那么此异常则被吃掉了。
     * </p>
     * 
     * @param r
     *            任务名称
     * @param t
     *            异常堆栈
     */
    private static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone())
                    future.get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null)
            log.error(t.getMessage(), t);
    }

    /**
     * 周期性调度一个任务
     * 
     * @param task
     *            任务
     * @param initialDelay
     *            初始延时（毫秒）
     * @param delay
     *            周期延时（毫秒）
     */
    public static ScheduledFuture<?> schedule(Runnable task, long initialDelay, long delay) {
        initScheduledExecutor();
        return scheduledExecutor.scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> schedule(Runnable task, long delay) {
        initScheduledExecutor();
        return scheduledExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭线程池，并等待线程池中所有任务执行完毕
     */
    public static synchronized void shutdown() {
        if (null != executor) {
            executor.shutdown();
            executor = null;
        }
        if (null != scheduledExecutor) {
            scheduledExecutor.shutdown();
            scheduledExecutor = null;
        }
    }

    /**
     * 立即关闭线程池，不等待线程池中所有任务执行完毕
     */
    public static synchronized void shutdownNow() {
        if (null != executor) {
            executor.shutdownNow();
            executor = null;
        }
        if (null != scheduledExecutor) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    /**
     * 异步执行一个任务
     * 
     * @param task
     *            任务
     * @return 任务结果
     */
    public static <V> Future<V> submit(Callable<V> task) {
        initExecutor();
        return executor.submit(task);
    }

    /**
     * 异步执行一个任务
     * 
     * @param task
     *            任务
     */
    public static Future<?> submit(Runnable task) {
        initExecutor();
        return executor.submit(task);
    }

}
