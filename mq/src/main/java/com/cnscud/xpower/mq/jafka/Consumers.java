package com.cnscud.xpower.mq.jafka;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cnscud.xpower.configcenter.ConfigCenter;

import io.jafka.consumer.Consumer;
import io.jafka.consumer.ConsumerConfig;
import io.jafka.consumer.ConsumerConnector;
import io.jafka.consumer.MessageStream;
import io.jafka.producer.serializer.StringDecoder;
import io.jafka.utils.Closer;
import io.jafka.utils.ImmutableMap;

/**
 * 通用的消费队列
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class Consumers implements Closeable{

    private ExecutorService executor;
    private final ConsumerConnector connector;
    private Consumers(Properties props, final String topic, final String groupId, final int nThreads, final IMessageListener<String> listener) {
        props.put("groupid", groupId);
        ConsumerConfig consumerConfig = new ConsumerConfig(props);
        connector = Consumer.create(consumerConfig);

        Map<String, List<MessageStream<String>>> topicMessageStreams = connector.createMessageStreams(//
                ImmutableMap.of(topic, nThreads), new StringDecoder());
        List<MessageStream<String>> streams = topicMessageStreams.get(topic);
        //
        executor = new ThreadPoolExecutor(nThreads, nThreads,//
                0L, TimeUnit.MILLISECONDS,//
                new LinkedBlockingQueue<Runnable>(),//
                new NamedThreadFactory("consumer:"+groupId+":"+topic, false)//
                ) {
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                printException(r, t);
            }
        };
        for (final MessageStream<String> stream : streams) {
            executor.submit(new Runnable() {

                public void run() {
                    for (String message : stream) {
                        listener.onMessage(message);
                    }
                }
            });
        }
        //
        Runtime.getRuntime().addShutdownHook(new Thread((Runnable) this::close));
    }

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
        if (t != null) {
            t.printStackTrace();
        }
    }
    @Override
    public void close() {
        if(executor != null) {
            executor.shutdown();
            Closer.closeQuietly(connector);
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
        if (!props.containsKey("zk.connect")) {
            props.put("zk.connect", ConfigCenter.getFullConnection() + "/xpower/jafka");
            props.put("zk.sessiontimeout.ms", "30000");// 为了VPN
            props.put("zk.connectiontimeout.ms", "30000");// 为了VPN
        }
        final Properties jafkaProperty = props;
        System.getProperties().forEach((k, v) -> {
            final String JAFKA_PREFIX = "jafka.";
            String name = (String) k;
            if (name.startsWith(JAFKA_PREFIX)) {
                jafkaProperty.put(name.substring(JAFKA_PREFIX.length()), (String) v);
            }
        });
        
        return new Consumers(props, topic, groupId, threads, listener);
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
        if(!"smallest".equals(firstOffset) && !"largest".equals(firstOffset)) {
            throw new IllegalArgumentException("`firstOffset`必须明确消息是从旧`smallest`开始消费，还是从最新`largest`开始消费");
        }
        Properties props = new Properties();
        props.put("autooffset.reset", firstOffset);
        return buildConsumer(topic, groupId, listener, threads, props);
    }



}