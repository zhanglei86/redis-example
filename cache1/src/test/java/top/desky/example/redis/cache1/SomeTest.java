package top.desky.example.redis.cache1;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SomeTest extends BaseTestCase {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void AstringRedis() {
        String key = "username";
        String value = "password";

        redisTemplate.opsForValue().set(key, value);
        Serializable sv = redisTemplate.opsForValue().get(key);
        Assert.assertEquals(value, String.valueOf(sv));
    }

    @Test
    public void BlistRedis() {
        String key = "userList";

        List<String> trap = new ArrayList<>();
        trap.add("张三");
        trap.add("张三");
        trap.add("张无忌");
        trap.add("新垣结衣");
        //循环向userList左添加值
        trap.forEach(value -> redisTemplate.opsForList().leftPush(key, value));
        //redisTemplate.opsForList().leftPushAll(key, trap);

        //向userList右添加值
        redisTemplate.opsForList().rightPush(key, "rightValue");

        //取出userList的值
        log.info("{}==>{}", key, redisTemplate.opsForList().range(key, 0, 10));
    }

    @Test
    public void CsetRedis() {
        String key = "userSet";

        List<String> trap = new ArrayList<>();
        trap.add("张三");
        trap.add("里斯");
        trap.add("里斯");
        trap.add("张无忌");
        trap.add("新垣结衣");
        System.out.print(trap.toString());
        //循环向userlist左添加值
        trap.forEach(value -> redisTemplate.opsForSet().add(key, value));

        log.info("取出userSet->{}", redisTemplate.opsForSet().members(key));
    }

    @Test
    public void DhashRedis() {
        String key = "userHash";

        //添加
        redisTemplate.opsForHash().put(key, "phone", 10086);
        redisTemplate.opsForHash().put(key, "address", "Shanghai");
        redisTemplate.opsForHash().put(key, "sex", "man");
        redisTemplate.opsForHash().put(key, "dada", "达达");
        //修改
        redisTemplate.opsForHash().put(key, "address", "Beijing");
    }

    @Test
    public void Zdel() {
        redisTemplate.opsForList().remove("userList", 100, "张无忌");
        redisTemplate.opsForSet().remove("userSet", "张无忌");
        redisTemplate.opsForHash().delete("userHash", "dada");
        stringRedisTemplate.delete("username");
    }

}
