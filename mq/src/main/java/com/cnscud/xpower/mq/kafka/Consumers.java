package com.cnscud.xpower.mq.kafka;

import com.cnscud.xpower.mq.unified.IMessageListener;
import com.cnscud.xpower.configcenter.ConfigCenter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用的消费队列
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class Consumers implements Closeable{

    private ExecutorService executor;
    private volatile boolean stop = false;

    private Consumers(Properties props, final String topic, final String groupId, final int nThreads, final IMessageListener<String> listener) {
        props.put("group.id", groupId);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        //订阅
        consumer.subscribe(Collections.singletonList(topic));

        executor = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2), new ThreadPoolExecutor.CallerRunsPolicy());

        stop = false;
        executor.submit(new Thread() {
            @Override
            public void run() {

                while ( !stop ) {
                    //获取队列内容
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                        listener.onMessage(record.value());
                    }
                }
            }
        });

        //
        Runtime.getRuntime().addShutdownHook(new Thread((Runnable) this::close));
    }


    @Override
    public void close() {
        if(executor != null) {
            System.out.println("call close " + LocalDateTime.now());
            stop = true;
            executor.shutdown();
            executor = null;
        }
    }


    public static Consumers buildConsumer(final String topic,//
                                          final String groupId,//
                                          final IMessageListener<String> listener,//
                                          final int threads, //
                                          Properties props) {
        if (props == null || props.isEmpty()) {
            props = new Properties();
        }

        if (!props.containsKey("bootstrap.servers") ) {
            props.put("bootstrap.servers", ConfigCenter.getInstance().getDataAsString("/xpower/config/kafka"));
        }
        if (!props.containsKey("key.serializer")) {
            props.put("key.deserializer", StringDeserializer.class.getName());
        }
        if (!props.containsKey("value.serializer")) {
            props.put("value.deserializer", StringDeserializer.class.getName());
        }

        final Properties kafkaProperty = props;
        System.getProperties().forEach((k, v) -> {
            final String KAFKA_PREFIX = "kafka.";
            String name = (String) k;
            if (name.startsWith(KAFKA_PREFIX)) {
                kafkaProperty.put(name.substring(KAFKA_PREFIX.length()), (String) v);
            }
        });
        
        return new Consumers(kafkaProperty, topic, groupId, threads, listener);
    }

    /**
     * 创建消息队列消费者
     * @param topic topic主题
     * @param groupId 分组groupID，决定是否和别人共享消费者
     * @param listener 处理逻辑，默认是处理字符串
     * @param firstOffset 从何处开始消费，可选值最旧消息`smallest`和最新消息`largest`
     * @return 消费者对象
     */
    public static Consumers buildConsumer(final String topic,//
                                          final String groupId, //
                                          final IMessageListener<String> listener,//
                                          final String firstOffset) {
        return buildConsumer(topic, groupId, listener, 4, firstOffset);
    }

    /**
     * 创建消息队列消费者
     * @param topic topic主题
     * @param groupId 分组groupID，决定是否和别人共享消费者
     * @param listener 处理逻辑，默认是处理字符串
     * @param threads 消费线程数，消费线程数要大于或等于分片总数，目前默认分片数数2
     * @param firstOffset 从何处开始消费，可选值最旧消息`smallest`和最新消息`largest`
     * @return 消费者对象
     */
    public static Consumers buildConsumer(final String topic,//
                                          final String groupId,//
                                          final IMessageListener<String> listener, //
                                          final int threads,//
                                          final String firstOffset) {
        if(!"smallest".equals(firstOffset) && !"largest".equals(firstOffset) && !"earliest".equals(firstOffset) && !"latest".equals(firstOffset)) {
            throw new IllegalArgumentException("`firstOffset`必须明确消息是从旧`smallest`开始消费，还是从最新`largest`开始消费");
        }

        String offset = firstOffset;
        /*
        earliest: automatically reset the offset to the earliest offset
        latest: automatically reset the offset to the latest offset
        none: throw exception to the consumer if no previous offset is found for the consumer's group
        */

        //翻译一下
        if("smallest".equals(firstOffset)){
            offset = "earliest";
        }
        if("largest".equals(firstOffset)){
            offset = "latest";
        }

        Properties props = new Properties();
        props.put("auto.offset.reset", offset);
        return buildConsumer(topic, groupId, listener, threads, props);
    }

}