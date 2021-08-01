/**
 * 
 */
package demo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.cnscud.xpower.http.Request;
import com.cnscud.xpower.http.Response;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2015年8月18日
 */
public class ImDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final int THREAD_SIZE = 10;
        final int MAX_SIZE = 4000;
        //
        final AtomicInteger cnt = new AtomicInteger(MAX_SIZE);
        //
        final AtomicLong total = new AtomicLong(0);
        final AtomicInteger ok = new AtomicInteger(0);
        final AtomicInteger fail = new AtomicInteger(0);
        Thread[] ts = new Thread[THREAD_SIZE];
        for (int i = 0; i < ts.length; i++) {
            Thread t = new Thread(() -> {
                while (cnt.decrementAndGet() >= 0) {
                    long st = System.currentTimeMillis();
                    String ret = "";
                    try {
                        Request req = new Request("http://127.0.0.1:9204/chat/37").setSoTimeout(120 * 1000).setForceOk(false);
                        Response resp = req.get();
                        ret = resp.asString();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        long cost = System.currentTimeMillis() - st;
                        System.out.println("COST " + cost + " ret=" + ret);
                        total.addAndGet(cost);
                        if (ret.contains("SINGLE")) {
                            ok.incrementAndGet();
                        } else {
                            fail.incrementAndGet();
                        }
                    }
                }
            });
            ts[i] = t;
            t.start();
        }
        for (Thread t : ts) {
            t.join();
        }
        System.out.println("Request " + MAX_SIZE + " cost " + total.get() + " avg=" + (total.get() / MAX_SIZE));
        System.out.println("OK=" + ok.get() + " FAIL=" + fail.get());
    }

}
