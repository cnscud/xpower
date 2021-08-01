package com.cnscud.xpower.configcenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 全局配置
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年1月19日
 */
public class GlobalConfig {
    public static enum GlobalConfigKey {
        fake,//我们是诚实的
        product, // 是否是生产环境
        passport_maxLoginCountPerHour, // 每小时密码尝试登录的次数
        passport_ticketPersistenDay, // 记住密码ticket有效期
        passport_ticketTempMinute,// 临时密码ticket有效期

        //
        api_ip_whitelist,//API-WEB的IP白名单
        ;
    }

    Map<String, String> cache = new ConcurrentHashMap<>();

    public int get(GlobalConfigKey key, int defaultValue) {
        return get(key, defaultValue, Integer::parseInt);
    }

    public double get(GlobalConfigKey key, double defaultValue) {
        return get(key, defaultValue, Double::parseDouble);
    }

    public long get(GlobalConfigKey key, long defaultValue) {
        return get(key, defaultValue, Long::parseLong);
    }

    public boolean is(GlobalConfigKey key, boolean defaultValue) {
        return get(key, defaultValue, Boolean::parseBoolean);
    }

    public String get(GlobalConfigKey key, String defaultValue) {
        return cache.getOrDefault(key.name(), defaultValue);
    }

    public LocalDateTime get(GlobalConfigKey key, LocalDateTime defaultValue) {
        return get(key, defaultValue, LocalDateTime::parse);
    }

    public LocalDate get(GlobalConfigKey key, LocalDate defaultValue) {
        return get(key, defaultValue, LocalDate::parse);
    }
    
    public LocalTime get(GlobalConfigKey key, LocalTime defaultValue) {
        return get(key, defaultValue, LocalTime::parse);
    }

    public <T> T get(GlobalConfigKey key, T defaultValue, Function<String, T> f) {
        String value = cache.get(key.name());
        if (value != null) {
            try {
                return f.apply(value);
            } catch (Exception ex) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    void putAll(Properties m) {
        // 只修改，不删除
        for (String name : m.stringPropertyNames()) {
            cache.put(name, m.getProperty(name));
        }
    }

    boolean isEmpty() {
        return cache.isEmpty();
    }
}
