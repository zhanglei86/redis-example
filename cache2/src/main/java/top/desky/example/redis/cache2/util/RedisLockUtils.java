package top.desky.example.redis.cache2.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCommands;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * 通过redis实现分布式锁
 */
@Component
public class RedisLockUtils {
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private String lockedValue;

    /**
     * redis操作类
     */
    @Autowired
    private RedisTemplate<String, Object> template;

    /**
     * 获取锁
     *
     * @param lockedKey
     * @param expire
     * @return
     */
    private boolean getLock(String lockedKey, long expire) {
        lockedValue = UUID.randomUUID().toString();
        //获取锁
        String exeResult = template.execute((RedisCallback<String>) connection -> {
            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            /**
             * NX： 表示只有当锁定资源不存在的时候才能 SET 成功。利用 Redis 的原子性，
             *      保证了只有第一个请求的线程才能获得锁，而之后的所有线程在锁定资源被释放之前都不能获得锁。
             *
             * PX： expire 表示锁定的资源的自动过期时间，单位是毫秒。具体过期时间根据实际场景而定
             */
            return commands.set(lockedKey, lockedValue, "NX", "PX", expire);
        });

        //是否获取到锁
        boolean result = LOCK_SUCCESS.equals(exeResult);

        return result;
    }

    /**
     * 获取锁
     * 如果获取不到，自动尝试多次，直到花费的时间超过tryTimeOut时间
     *
     * @param lockedKey
     * @param expire
     * @param tryTimeOut
     * @return
     */
    public boolean getLock(String lockedKey, long expire, long tryTimeOut) {
        //单位都是毫秒
        long startTime = System.currentTimeMillis();
        Random random = new Random();

        while ((System.currentTimeMillis() - startTime) <= tryTimeOut) {
            if (getLock(lockedKey, expire)) {
                return true;
            }
            try {
                Thread.sleep(50, random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockedKey
     * @return
     */
    public boolean releaseLock(String lockedKey) {

        if (lockedValue == null || lockedValue.length() == 0) {
            return false;
        }
        // 使用Lua脚本删除Redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
        // 删除前要通过value来判断是否为自己的锁
        String script = new StringBuffer()
                .append("if redis.call('get', KEYS[1]) == ARGV[1] then ")
                .append("   return redis.call('del', KEYS[1]) ")
                .append("else ")
                .append("   return 0 ")
                .append("end ")
                .toString();

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText(script);
        //执行脚本
        Long exeResult = template.execute(redisScript, Arrays.asList(lockedKey), lockedValue);

        boolean result = (RELEASE_SUCCESS == exeResult);

        return result;
    }
}
