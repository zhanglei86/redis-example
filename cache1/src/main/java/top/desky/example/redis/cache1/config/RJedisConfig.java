package top.desky.example.redis.cache1.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@EnableCaching
//@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RJedisConfig extends CachingConfigurerSupport {
    private static final Logger log = LoggerFactory.getLogger(RJedisConfig.class);

    @Autowired
    private RedisConnectionFactory factory;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Bean
    public void run() {
        log.info("run==> redis连接工厂：{}", stringRedisTemplate.getConnectionFactory());
        final String key = "zea";

        //添加key
        stringRedisTemplate.opsForValue().set(key, "lous");
        //获取key
        log.info("run==> 从redis中获取key是{}, value是{}", key, stringRedisTemplate.opsForValue().get(key));
        //删除key
        stringRedisTemplate.delete(key);
    }

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

    /**
     * 跟spring-cache整合时才用到？另spring-cache默认用的SimpleCacheConfiguration.
     *
     * @return
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        List<String> cacheNames = Arrays.asList("redis_default", "redis_develop");

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration.entryTtl(Duration.ofSeconds(3600));

        // 管理器
        RedisCacheManager.RedisCacheManagerBuilder rcmBuilder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory);
        RedisCacheManager rcm = rcmBuilder.build();
        // 其他的
        SimpleCacheManager scm = new SimpleCacheManager();
        ConcurrentMapCacheManager cmcm = new ConcurrentMapCacheManager();

        return rcm;
    }

    // 简单K-V操作
    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
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
