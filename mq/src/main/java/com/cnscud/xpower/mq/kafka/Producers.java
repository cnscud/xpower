package com.cnscud.xpower.mq.kafka;

import com.cnscud.xpower.mq.unified.StringMessage;
import com.cnscud.xpower.configcenter.ConfigCenter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/** 生产者
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class Producers {

    final Logger log = LoggerFactory.getLogger(getClass());

    private final Producer<String, List<String>> producer;

    private Producers(Properties props) {

        //如果不支持, 读取zk里设置的值
        if (!props.containsKey("bootstrap.servers") ) {
            props.put("bootstrap.servers", ConfigCenter.getInstance().getDataAsString("/xpower/config/kafka"));
        }
        if (!props.containsKey("key.serializer")) {
            props.put("key.serializer", StringSerializer.class.getName());
        }
        if (!props.containsKey("value.serializer")) {
            props.put("value.serializer", StringSerializer.class.getName());
        }

        log.info("init Producers :"+ props.toString());

        producer = new KafkaProducer<String, List<String>>(props);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                producer.close();
            }
        });
    }
    
    public Producer<String, List<String>> getProducer(){
        return this.producer;
    }
    
    /**
     * send the message to message-queue
     * @param message message
     */
    public void send(StringMessage message) {
        if(message != null && message.getData() !=null ) {
            for(String v: message.getData()) {
                ProducerRecord pr = new ProducerRecord<String, String>(message.getTopic(), message.getKey(), v);
                producer.send(pr);
            }
        }
    }
    
    private static volatile Producers instance = null;
    /**
     * 创建一个生产者
     * <p>
     * 由于每一个生产者都需要和服务器端建立连接，因此，默认情况下应该维持此实例为单实例，减少连接数
     * </p>
     * @param props 扩展参数
     * @return 带有特定参数的生产者
     */
    public static Producers buildProducer(Properties props) {
        if(props == null || props.isEmpty()) {
            if(instance == null) {
                synchronized (Producers.class) {
                    if(instance == null) {
                        instance = new Producers(new Properties());
                    }
                }
            }
            return instance;
        }
        return new Producers(props);
    }
    
    /**
     * 创建一个生产者
     * <p>
     * 由于每一个生产者都需要和服务器端建立连接，因此，默认情况下应该维持此实例为单实例，减少连接数
     * </p>
     * @return 带有特定参数的生产者
     */
    public static Producers buildProducer() {
        //指望別人能夠正確看註釋，太困难了，哎～～～
        return buildProducer(null);
    }
    
    //
}