package com.cnscud.xpower.dao;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 数据库字段和值映射
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-07-30
 */
public class FieldValue {

    final Map<String, Object> m = new LinkedHashMap<>();

    public FieldValue() {
    }

    public FieldValue(Map<String, Object> map) {
        m.putAll(map);
    }

    public FieldValue(String field, Object value) {
        put(field, value);
    }

    public FieldValue(String field, Object value, String field2, Object value2) {
        put(field, value).put(field2, value2);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3) {
        put(field, value).put(field2, value2).put(field3, value3);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3, //
            String field4, Object value4) {
        put(field, value).put(field2, value2).put(field3, value3).put(field4, value4);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3, //
            String field4, Object value4, String field5, Object value5) {
        put(field, value).put(field2, value2).put(field3, value3).put(field4, value4).put(field5, value5);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3, //
            String field4, Object value4, String field5, Object value5, String field6, Object value6) {
        put(field, value).put(field2, value2).put(field3, value3).put(field4, value4)//
                .put(field5, value5).put(field6, value6);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3, //
            String field4, Object value4, String field5, Object value5, String field6, Object value6, //
            String field7, Object value7) {
        put(field, value).put(field2, value2).put(field3, value3).put(field4, value4)//
                .put(field5, value5).put(field6, value6).put(field7, value7);
    }

    public FieldValue(String field, Object value, String field2, Object value2, String field3, Object value3, //
            String field4, Object value4, String field5, Object value5, String field6, Object value6, //
            String field7, Object value7, String field8, Object value8) {
        put(field, value).put(field2, value2).put(field3, value3).put(field4, value4)//
                .put(field5, value5).put(field6, value6).put(field7, value7).put(field8, value8);
    }

    public FieldValue put(String field, Object value) {
        m.put(field, value);
        return this;
    }

    public FieldValue put(Map<String, Object> map) {
        m.putAll(map);
        return this;
    }

    public Set<String> keys() {
        return m.keySet();
    }

    public Collection<Object> values() {
        return m.values();
    }

    public void forEach(BiConsumer<String, Object> action) {
        m.forEach(action);
    }

    public boolean isEmpty() {
        return m.isEmpty();
    }
}
