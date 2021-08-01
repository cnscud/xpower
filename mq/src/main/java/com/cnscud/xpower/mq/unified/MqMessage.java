package com.cnscud.xpower.mq.unified;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-27
 */
public class MqMessage<K, V> {

    private String topic;
    private K key;
    private List<V> data;

    public MqMessage(String topic, List<V> data) {
        this.topic = topic;
        this.data = data;
    }

    public MqMessage(String topic, K key, List<V> data) {
        this.topic = topic;
        this.key = key;
        this.data = data;

    }

    public MqMessage(String topic, V data) {
        getData().add(data);
    }

    public MqMessage(String topic) {
        this.topic = topic;
    }

    public MqMessage add(V message) {
        getData().add(message);
        return this;
    }


    public List<V> getData() {
        if (data == null) {
            data = new ArrayList<V>();
        }
        return data;
    }

    public void setData(List<V> data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }
}