package com.cnscud.xpower.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * JSON的数据封装对象  (Jackson 1.x)
 * 
 * @author Ady Liu (imxylz@gmail.com)
 * @author Tony Chen
 * @since 2012年10月29日
 */
public class JsonUtils {
    final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
    final static ObjectMapper objectMapper;

    final static ObjectMapper objectMapperWithNull;

    static {
        StdSerializerProvider sp = new StdSerializerProvider();
        sp.setNullValueSerializer(new NullSerializer());
        objectMapper = new ObjectMapper(null, sp, null);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // objectMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        // objectMapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StdSerializerProvider spRaw = new StdSerializerProvider();
        objectMapperWithNull = new ObjectMapper(null, spRaw, null);
        objectMapperWithNull.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapperWithNull.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //
        setup(objectMapper);
        setup(objectMapperWithNull);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    private static void setup(ObjectMapper om) {
        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
        // LocalDate
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        // LocalDateTime
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        // LocalTime
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        om.registerModule(simpleModule);

    }

    //private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static class LocalDateSerializer extends JsonSerializer<LocalDate>{
        @Override
        public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(DateTimeUtils.toString(value));
        }
    }
    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate>{
        @Override
        public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return DateTimeUtils.toLocalDate(str);
        }
    } 
    private static class LocalTimeSerializer extends JsonSerializer<LocalTime>{
        @Override
        public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.toString());
        }
    }
    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime>{
        @Override
        public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return LocalTime.parse(str);
        }
    } 
    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{
        @Override
        public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(value.format(dateTimeFormatter));
        }
    }
    private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>{
        @Override
        public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String str = jp.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return DateTimeUtils.toLocalDateTime(str);
        }
    }
    private static class NullSerializer extends JsonSerializer<Object> {

        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString("");
        }
    }

    /**
     * 将json字符串转换为map
     * 
     * @param jsonStr
     *            要转换的字符串
     * @return 转换成的map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonStr(String jsonStr) {
        try {
            return getObjectMapper().readValue(jsonStr, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Can not getMapFromJsonStr which jsonStr:" + jsonStr, e);
        }
    }

    @Deprecated
    public static String getJsonFromMap(Map<String, ?> map) {
        try {
            return toJson(map, false, false);
        } catch (Exception e) {
            throw new RuntimeException("Can not getJsonFromMap which map:" + map, e);
        }
    }

    @Deprecated
    public static String getJsonFromMap(Map<String, ?> map, boolean withNull) {
        try {
            return toJson(map, false, withNull);
        } catch (Exception e) {
            throw new RuntimeException("Can not getJsonFromMap which map:" + map, e);
        }
    }

    /**
     * json格式字符串转换为java对象
     * 
     * @param content
     * @param valueType
     * @return
     * @throws IOException
     */
    public static <T> T toBean(String content, Class<T> valueType) throws IOException {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonParseException jpe) {
            logger.error(content);
            throw jpe;
        }
    }
    /**
     * 反序列化对象
     * @param content 内容
     * @param valueType 对象类型
     * @return 实例化对象
     * @throws RuntimeException 如果解析失败
     * @throws NullPointerException 如果content是null
     * @since 2016年2月2日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static <T> T toBean2(String content, Class<T> valueType){
        try {
            return objectMapper.readValue(content, valueType);
        } catch (IOException jpe) {
            throw new RuntimeException("parse json failed: " + content, jpe);
        }
    }

    public static <T> T toBean(Map<String, Object> map, Class<T> classType) {
        return objectMapper.convertValue(map, classType);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String content) throws IOException {
        if (StringUtils.isBlank(content)) {
            return new HashMap<String, Object>();// 不要使用EMPTY_MAP
        }
        try {
            return objectMapper.readValue(content, Map.class);
        }catch(Exception ex) {
            logger.error("error map json: "+content);
            throw ex;
        }
    }
    public static Map<String, Object> toMap2(String content){
        try {
            return toMap(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    public static JsonNode parse(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String content) {
        if (StringUtils.isBlank(content)) {
            return new ArrayList<T>();// 不要使用EMPTY_LIST
        }
        try {
            return toBean(content, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Can not toList which jsonStr: " + content, e);
        }
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
    public static <T> List<T> toList(String content, Class<T> elementClass) throws IOException {
        if (StringUtils.isBlank(content)) {
            return new ArrayList<T>();// 不要使用EMPTY_LIST
        }
        JavaType type = getObjectMapper().getTypeFactory().constructCollectionType(List.class, elementClass);
        return getObjectMapper().readValue(content, type);
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
     * @since 2014年11月13日
     */
    public static <T> List<T> toList2(String content, Class<T> elementClass) {
        if (StringUtils.isBlank(content)) {
            return new ArrayList<T>();// 不要使用EMPTY_LIST
        }
        try {
            return toList(content, elementClass);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }

    public static String toJson(Object object) throws IOException {
        return toJson(object, false, false);
    }

    public static String toJson2(Object object) {
        return toJson2(object, false, false);
    }

    public static String toJson(Object obj, boolean withIndent, boolean withNull) throws IOException {
        ObjectMapper mo = withNull ? objectMapperWithNull : objectMapper;
        if (withIndent) {
            return mo.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } else {
            return mo.writeValueAsString(obj);
        }
    }

    /**
     * 序列化对象（RuntimeException）
     * 
     * @param obj
     *            可序列化JSON对象
     * @param withIndent
     *            是否格式化
     * @param withNull
     *            是否输出null
     * @return 序列化后的json字符串
     */
    public static String toJson2(Object obj, boolean withIndent, boolean withNull) {
        try {
            return toJson(obj, withIndent, withNull);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }

    /**
     * 
     * @param code
     * @param msg
     * @return
     * @since 2014年1月6日
     */
    public static String toJson(int code, String msg) {
        try {
            return toJson(ImmutableMap.of("code", code, "msg", msg));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }
    public static String toJson(int code, String msg, Object data, boolean indent) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("code", code);
        result.put("msg", msg);
        if (data != null) {
            result.put("data", data);
        }

        return toJson2(result, indent,true);
    }
    
    @Deprecated
    public static String toJson(Object object, boolean withNull) {
        try {
            return toJson(object, false, withNull);
        } catch (Exception e) {
            throw new RuntimeException("Can not toJson", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static Map<String, List<Map<String, Object>>> getCustomMapFromJsonStr(String jsonStr) {
        try {
            return toBean(jsonStr, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Can not getMapFromJsonStr which jsonStr:" + jsonStr, e);
        }
    }

    @Deprecated
    public static <T> T json2Type(String jsonValue, Class<T> clazz) {
        try {
            return toBean(jsonValue, clazz);
        } catch (JsonParseException jpe) {
            logger.error(String.format("json2Type error. [%s][%s]", jsonValue, clazz.getName()), jpe);
        } catch (JsonMappingException jme) {
            logger.error(String.format("json2Type error. [%s][%s]", jsonValue, clazz.getName()), jme);
        } catch (IOException e) {
            logger.error(String.format("json2Type error. [%s][%s]", jsonValue, clazz.getName()), e);
        }
        return null;
    }

    /**
     * 当json数据 key值动态变化时
     * 解析json数据下的普通key {"key":"val"},{"key2":"val2"}
     * 例如数据结构 {"123a":"val"},{"123b","val"}
     * @param data      data map
     * @param dataKey   需要解析的data数据集
     * @param keys      data里面需要解析的字段
     * @return
     */
    public static List<Map<String,Object>> dynamicParse(Map<String,Object> data, String dataKey, String... keys) {
        LinkedList<Map<String, Object>> list = new LinkedList<>();
        try {
            Map<String, ?> map = Maps2.getMap(data, dataKey);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String dynamicKey = iterator.next();
                Map<String, ?> val = Maps2.getMap(map, dynamicKey);
                Map<String, Object> objectMap = new HashMap<>();
                for (String key : keys) {
                    Object vo = val.get(key);
                    if (vo != null) objectMap.put(key, vo);
                }
                list.add(objectMap);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }


}
