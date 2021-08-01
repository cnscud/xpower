package com.cnscud.xpower.mq.jafka;

import java.util.Properties;

import com.cnscud.xpower.configcenter.ConfigCenter;

import io.jafka.producer.Producer;
import io.jafka.producer.ProducerConfig;
import io.jafka.producer.serializer.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 生产者
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class Producers {

    final Logger log = LoggerFactory.getLogger(getClass());

    private final Producer<String, String> producer;

    private Producers(Properties props) {
        if (!props.containsKey("zk.connect") && !props.containsKey("broker.list")) {
            props.put("zk.connect", ConfigCenter.getFullConnection()+"/xpower/jafka");
        }
        if (!props.containsKey("serializer.class")) {
            props.put("serializer.class", StringEncoder.class.getName());
        }
        log.info("init Producers :"+ props.toString());
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                producer.close();
            }
        });
    }
    
    public Producer<String, String> getProducer(){
        return this.producer;
    }
    
    /**
     * send the message to message-queue
     * @param message message
     */
    public void send(StringMessage message) {
        producer.send(message);
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
     * @param props 扩展参数
     * @return 带有特定参数的生产者
     */
    public static Producers buildProducer() {
        //指望別人能夠正確看註釋，太困难了，哎～～～
        return buildProducer(null);
    }
    
    //
}