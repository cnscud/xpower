package demo;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cnscud.xpower.configcenter.ConfigCenter;
import com.cnscud.xpower.configcenter.IDistributedLock;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年2月26日
 */
public class DistributedLockDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final AtomicBoolean isLocked = new AtomicBoolean(false);
        final String lockPath = "/lock/demo";
        
        final int thread_size = 10;
        Thread[] ts = new Thread[thread_size];
        for (int i = 0; i < thread_size; i++) {
            final int index = i;
            ts[i] = new Thread(() -> {
                System.out.println("start thread " + index);
                final IDistributedLock lock = ConfigCenter.getInstance().getLock(lockPath);
                 lock.lock();
                //while (!lock.tryLock(10, TimeUnit.MILLISECONDS))
                //    ;
                if (!isLocked.compareAndSet(false, true)) {
                    System.err.println("the lock is locked by others");
                }
                try {
                    for (int m = 0; m < 5; m++) {
                        System.out.printf(System.currentTimeMillis() + " baby one i=%d m=%d\n", index, m);
                    }
                } finally {
                    isLocked.compareAndSet(true, false);
                    lock.unlock();
                }
            });
        }
        for (Thread t : ts) {
            t.start();
        }
        for (Thread t : ts) {
            t.join();
        }
        //
        LocalDateTime now = ConfigCenter.getInstance().executeInDistributedLock("/demo/poa_rebuild", () -> LocalDateTime.now());
        System.out.println("now => " + now);
    }

}
