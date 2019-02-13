package top.desky.example.redis.cache1.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JacksonUtils {
    /* 默认时间转换格式 */
    private static String pattern = "yyyy-MM-dd HH:mm:ss";

    /* null不序列化 时间转换 yyyy-MM-dd HH:mm:ss 格式 */
    public static ObjectMapper createObjectMapperNullNotEcho() {
        ObjectMapper objectMapper = createObjectMapper(false, pattern);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /* null不序列化  时间转换指定格式 */
    public static ObjectMapper createObjectMapperNullNotEcho(String pattern) {
        ObjectMapper objectMapper = createObjectMapper(false, pattern);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /* 可指定是否替换null为空字符串"" 时间转换 yyyy-MM-dd HH:mm:ss 格式*/
    public static ObjectMapper createObjectMapper(boolean nullToString) {
        return createObjectMapper(nullToString, pattern);
    }

    /* 可指定是否替换null为空字符串"" 时间转换指定格式 */
    public static ObjectMapper createObjectMapper(boolean nullToString, String pattern) {
        ObjectMapper objectMapper = nullToString ? new ObjectMappingNullToString() : new ObjectMapper();
        if (pattern != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            objectMapper.setDateFormat(simpleDateFormat);
        }
        return objectMapper;
    }

    public static class ObjectMappingNullToString extends ObjectMapper {
        public ObjectMappingNullToString() {
            super();
            // 空值处理为空串
            this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
                @Override
                public void serialize(Object value, JsonGenerator jg, SerializerProvider sp) throws IOException {
                    jg.writeString("");
                }
            });
        }
    }

    //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
    public static Jackson2JsonRedisSerializer useJackson() {
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        return serializer;
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) throws JsonProcessingException {
        if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        ObjectMapper objectMapper = createObjectMapper(true, null);
        // ObjectMapper objectMapper = createObjectMapperNullNotEcho(null);
        return objectMapper.writeValueAsString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = createObjectMapper(true, null);
        return objectMapper.readValue(json, clazz);
    }
}
