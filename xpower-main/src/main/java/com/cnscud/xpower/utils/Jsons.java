package com.cnscud.xpower.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * JSON的数据封装对象 (Jackson 2.x)
 * 
 * @author Ady Liu (imxylz@gmail.com)
 * @since 2019-08-01
 */
public class Jsons {
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /** 默认日期时间格式 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    // private static final Logger logger = LoggerFactory.getLogger(Jsons.class);
    final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // objectMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        // objectMapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
    }

    public static <T> T fromJson(byte[] bs, Class<T> valueType) {
        return runas(() -> objectMapper.readValue(bs, valueType));
    }

    public static <T> T fromJson(Map<String, Object> map, Class<T> classType) {
        return objectMapper.convertValue(map, classType);
    }

    /**
     * 反序列化对象
     * 
     * @param content
     *            内容
     * @param valueType
     *            对象类型
     * @return 实例化对象
     * @throws RuntimeException
     *             如果解析失败
     * @throws NullPointerException
     *             如果content是null
     * @since 2019-08-01
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static <T> T fromJson(String content, Class<T> valueType) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return runas(() -> objectMapper.readValue(content, valueType));
    }
    
    public static <T> T fromJson(String content, TypeReference valueTypeRef) {
        return runas(() -> objectMapper.readValue(content, valueTypeRef));
    }

    public static List<Object> fromJsonAsList(byte[] bs) {
        return fromJson(bs, List.class);
    }

    public static List<Object> fromJsonAsList(String content) {
        if (StringUtils.isBlank(content)) {
            return new ArrayList<>();// 不要使用EMPTY_LIST
        }
        return fromJson(content, List.class);
    }

    /**
     * 序列化列表对象
     * 
     * @param content
     *            json字符串
     * @param elementClass
     *            列表对象类型
     * @return List<T> 对象
     * @throws IOException
     *             解析异常
     * @since 2014年11月13日
     */
    public static <T> List<T> fromJsonAsList(String content, Class<T> elementClass) {
        if (StringUtils.isBlank(content)) {
            return new ArrayList<T>();// 不要使用EMPTY_LIST
        }
        JavaType type = getObjectMapper().getTypeFactory().constructCollectionType(List.class, elementClass);
        return runas(() -> objectMapper.readValue(content, type));
    }

    public static Map<String, Object> fromJsonAsMap(byte[] bs) {
        return fromJson(bs, Map.class);
    }

    public static Map<String, Object> fromJsonAsMap(String content) {
        if (StringUtils.isBlank(content)) {
            return new HashMap<String, Object>();// 不要使用EMPTY_MAP
        }
        return runas(() -> objectMapper.readValue(content, Map.class));
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static JsonNode parse(String content) throws IOException {
        return objectMapper.readTree(content);
    }

    private static <T> T runas(Callable<T> call) {
        try {
            return call.call();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static byte[] toBytes(Object obj) throws IOException {
        return objectMapper.writeValueAsBytes(obj);
    }

    public static String toJson(Object object) {
        return runas(() -> objectMapper.writeValueAsString(object));
    }
    public static String defaultObjectJson(Object object) {
        return object == null ? "{}" : toJson(object);
    }
    public static String defaultArrayJson(Object object) {
        return object == null ? "[]" : toJson(object);
    }
    public static String toIndentJson(Object object) {
        return runas(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
    }

    public static <T> T copy(T object) {
        return runas(() -> fromJson(toJson(object), (Class<T>) object.getClass()));
    }

    public static <T> T copy(Object object, Class<T> toClass) {
        return runas(() -> fromJson(toJson(object), toClass));
    }

    public static void toOutputStream(Object value, OutputStream out) throws IOException {
        objectMapper.writeValue(out, value);
    }

}
