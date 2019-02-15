package top.desky.example.redis.cache2.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * jedis的配置，仅用于简单的key-value场景
 * Created by zealous on 2018/12/4.
 */
//@EnableCaching
@Configuration
public class RJedisConfig extends CachingConfigurerSupport {
    private static final Logger log = LoggerFactory.getLogger(RJedisConfig.class);

    @Autowired
    private RedisConnectionFactory factory;

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> template) {
        testInit(template);
        return template.opsForValue();
    }

    private void testInit(RedisTemplate<String, Object> redisTemplate) {
        log.info("run==> redis连接工厂：{}", redisTemplate.getConnectionFactory());
        final String key = "zea";

        //添加key
        redisTemplate.opsForValue().set(key, "lous");
        //获取key
        log.info("run==> 从redis中获取key是{}, value是{}", key, redisTemplate.opsForValue().get(key));
        //删除key
        //redisTemplate.delete(key);
    }

    @Bean
    //@Override
    public CacheManager cacheManager(RedisTemplate<String, Object> template) {
        List<String> cacheNames = Arrays.asList("mmkt_default", "mmkt_develop");
        RedisCacheManager rcm = new RedisCacheManager(template, cacheNames);
        rcm.setUsePrefix(true);
        rcm.setDefaultExpiration(3600 * 2);
        return rcm;
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        final String SEQ = ":";
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName() + SEQ);
            sb.append(method.getName() + SEQ);
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }


    /**
     * 序列化定制
     *
     * @return
     */
    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
                Object.class);

        // 初始化objectmapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        return jackson2JsonRedisSerializer;
    }

    /**
     * 操作模板
     *
     * @param connectionFactory
     * @param jackson2JsonRedisSerializer
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        // 设置key/hashkey序列化
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 设置值序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

}
