package top.desky.example.redis.cache2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
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
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Bean
    public Character run() {
        log.info("run==> redis连接工厂：{}", stringRedisTemplate.getConnectionFactory());
        final String key = "zea";

        //添加key
        stringRedisTemplate.opsForValue().set(key, "lous");
        //获取key
        log.info("run==> 从redis中获取key是{}, value是{}", key, stringRedisTemplate.opsForValue().get(key));
        //删除key
        //stringRedisTemplate.delete(key);
        return 'o';
    }

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

    // 简单K-V操作
    @Bean
    public ValueOperations<String, Object> valueOperations(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        List<String> cacheNames = Arrays.asList("mmkt_default", "mmkt_develop");
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate(), cacheNames);
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

}
