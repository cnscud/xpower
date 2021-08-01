package demo.kafka;

import com.cnscud.xpower.mq.kafka.Consumers;
import com.cnscud.xpower.mq.unified.IMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年5月5日
 */
public class KafkaConsumerDemo {

    public static void main(String[] args) {
        List<Consumers> consumers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String groupId = "gs-" + i;

            Consumers cs = Consumers.buildConsumer("demo", groupId, new IMessageListener<String>() {

                @Override
                public void onMessage(String message) {
                    System.out.println("[MESSAGE- "+ groupId +"]"+message);
                }
            },"largest");

        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(100));
        consumers.forEach(Consumers::close);
    }

}
