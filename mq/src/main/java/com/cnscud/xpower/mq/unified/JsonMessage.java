package com.cnscud.xpower.mq.unified;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json message builder
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-27
 */
public class JsonMessage extends StringMessage {

    private final static ObjectMapper mapper = new ObjectMapper();

    public static class Json{
        final Map<String,Object> map = new HashMap<String, Object>();
        public Json addField(String key,Object value) {
            map.put(key, value);
            return this;
        }
        @Override
        public String toString() {
            return map.toString();
        }
    }
    public JsonMessage(String topic, List<String> data) {
        super(topic, data);
    }

    public JsonMessage(String topic, String key, List<String> data) {
        super(topic, key, data);
    }

    public JsonMessage(String topic, String data) {
        super(topic, data);
    }

    public JsonMessage(String topic) {
        super(topic);
    }

    @Override
    public JsonMessage add(String message) {
        super.add(message);
        return this;
    }
    public JsonMessage add(Json json) {
        return add(json.map);
    }
    public JsonMessage add(Map<String, Object> map) {
        try {
            String message = mapper.writeValueAsString(map);
            return add(message);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}