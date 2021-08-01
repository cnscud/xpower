/**
 * 
 */
package demo;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.cnscud.xpower.mq.jafka.Consumers;
import com.cnscud.xpower.mq.jafka.IMessageListener;
import com.cnscud.xpower.mq.jafka.Producers;
import com.cnscud.xpower.mq.jafka.StringMessage;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-27
 */
public class ProducerDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        //BasicConfigurator.configure();
        //Logger.getRootLogger().setLevel(Level.INFO);
        final CountDownLatch latch = new CountDownLatch(1);
        Producers.buildProducer().send(new StringMessage("demo").add("hello world"+LocalDateTime.now()));
        //
        Consumers cs = Consumers.buildConsumer("demo", "demogroup", new IMessageListener<String>() {
            
            @Override
            public void onMessage(String message) {
                System.out.println("[MESSAGE]"+message);
                latch.countDown();
            }
        },"largest");
        latch.await(10, TimeUnit.SECONDS);
        cs.close();
    }

}
