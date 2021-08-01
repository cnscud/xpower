package com.cnscud.xpower.mq.unified;

import java.util.List;

/**
 * Unified String Message for mq.
 *
 * @author Felix Zhang 2021-04-15 15:24
 * @version 1.0.0
 */
public class StringMessage extends MqMessage<String, String>  {

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
