/**
 *
 */
package demo.kafka;


import com.cnscud.xpower.mq.kafka.Consumers;
import com.cnscud.xpower.mq.kafka.Producers;
import com.cnscud.xpower.mq.unified.IMessageListener;
import com.cnscud.xpower.mq.unified.StringMessage;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-27
 */
public class KafkaProducerDemo {

    public static void main(String[] args) throws Exception {

        final CountDownLatch latch = new CountDownLatch(5);
        Producers.buildProducer().send(new StringMessage("demo").add("hello felix" + LocalDateTime.now()).add("echo echo 2222" + LocalDateTime.now()));

        Consumers cs = Consumers.buildConsumer("demo", "abcd", new IMessageListener<String>() {

            @Override
            public void onMessage(String message) {
                System.out.println("[MESSAGE]" + message);
                latch.countDown();
            }
        }, "largest");

        Thread.sleep(2000);
        Producers.buildProducer().send(new StringMessage("demo").add("world felix" + LocalDateTime.now()).add("world echo echo 2222"));

        //如果处理了5次, 或者5秒后, 会退出
        latch.await(5, TimeUnit.SECONDS);
        cs.close();


    }

}
