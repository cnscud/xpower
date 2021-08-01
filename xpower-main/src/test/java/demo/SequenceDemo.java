package demo;

import java.util.concurrent.atomic.AtomicLong;

import com.cnscud.xpower.dao.DaoFactory;
import com.cnscud.xpower.dao.IDao;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年1月12日
 */
public class SequenceDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        IDao dao = DaoFactory.getIDao();
        final int thread_num = 3;
        final int per_num = 10;
        Thread[] ts = new Thread[thread_num];
        //
        long now = dao.getNextSequence("demo");
        final AtomicLong realValue = new AtomicLong(now);
        for (int i = 0; i < thread_num; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < per_num; j++) {
                    long x = dao.getNextSequence("demo");
                    // System.out.println("demo => " + dao.getNextSequence("demo"));
                    // System.out.println("walle => " + dao.getNextSequence("demo"));
                    long oldValue = 0;
                    while ((oldValue = realValue.get()) < x) {
                        realValue.compareAndSet(oldValue, x);
                    }
                }
            });
        }
        for (int i = 0; i < thread_num; i++) {
            ts[i].start();
        }
        for (int i = 0; i < thread_num; i++) {
            ts[i].join();
        }
        System.out.println("expected value => " + (now + thread_num * per_num));
        System.out.println("real value => " + realValue.get());
    }

}
