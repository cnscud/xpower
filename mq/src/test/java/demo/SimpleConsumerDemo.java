package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import io.jafka.consumer.StringConsumers;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年5月5日
 */
public class SimpleConsumerDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<StringConsumers> consumers = new ArrayList<>();
        final String topic = "demo";
        for (int i = 0; i < 3; i++) {
            String groupId = "gs-" + i;
            consumers.add(StringConsumers.buildConsumer("192.168.6.22:2181/xpower/jafka", topic, groupId, s -> {
                System.out.printf("groupId=%s message=%s\n", groupId, s);
            }));
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        consumers.forEach(StringConsumers::close);
    }

}
