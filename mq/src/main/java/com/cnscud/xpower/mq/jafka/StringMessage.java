package com.cnscud.xpower.mq.jafka;

import java.util.List;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class StringMessage extends io.jafka.producer.StringProducerData {

    public StringMessage(String topic, List<String> data) {
        super(topic, data);
    }

    public StringMessage(String topic, String key, List<String> data) {
        super(topic, key, data);
    }

    public StringMessage(String topic, String data) {
        super(topic, data);
    }

    public StringMessage(String topic) {
        super(topic);
    }

    public StringMessage add(String message) {
        super.add(message);
        return this;
    }
}