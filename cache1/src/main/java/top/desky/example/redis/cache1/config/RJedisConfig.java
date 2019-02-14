package top.desky.example.redis.cache1.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EnableCaching
//@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RJedisConfig extends CachingConfigurerSupport {
    private static final Logger log = LoggerFactory.getLogger(RJedisConfig.class);

    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(useJackson());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(useJackson());
        template.afterPropertiesSet();
        return template;
    }

    // 简单K-V操作
    @Bean
    public ValueOperations<String, Object> valueOperations(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        testInit(redisTemplate);
        return redisTemplate.opsForValue();
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

    /**
     * 跟spring-cache整合时才用到？另spring-cache默认用的SimpleCacheConfiguration.
     *
     * @return 管理器
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认过期时间，也是使用Duration设置
        config = config.entryTtl(Duration.ofMinutes(10))
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(useJackson()))
                // 不缓存空值
                .disableCachingNullValues();

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames = new HashSet<>();
        cacheNames.add("redis_default");
        cacheNames.add("redis_develop");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("redis_default", config);
        configMap.put("redis_develop", config.entryTtl(Duration.ofSeconds(120)));

        // TODO 这里配置了默认过期时间，然并卵用。

        // 管理器
        RedisCacheManager rcm = RedisCacheManager.builder(factory)
                // 一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(configMap)
                .build();
        // 其他的
        SimpleCacheManager scm = new SimpleCacheManager();
        ConcurrentMapCacheManager cmcm = new ConcurrentMapCacheManager();

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

    //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
    private static RedisSerializer<Object> useJackson() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        return serializer;
    }

}
