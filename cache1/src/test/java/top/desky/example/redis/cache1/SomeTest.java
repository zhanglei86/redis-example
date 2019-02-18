package top.desky.example.redis.cache1;

import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class SomeTest extends BaseTestCase {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testStr() {
        String key = "username";
        String value = "password";

        redisTemplate.opsForValue().set(key, value);
        Object sv = redisTemplate.opsForValue().get(key);
        Assert.assertEquals(value, String.valueOf(sv));
    }

    @Test
    public void testHash() {
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
    public void testList() {
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
    public void testSet() {
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
    public void testZset() {
        ZSetOperations ops = redisTemplate.opsForZSet();

        String key = "userZSet";
        Calendar cal1 = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            //延迟3秒
            cal1.add(Calendar.SECOND, 3);
            long second3later = cal1.getTimeInMillis() / 1000;
            ops.add(key, "order100" + i, second3later);
        }

        Set<ZSetOperations.TypedTuple<String>> items = ops.rangeWithScores(key, 0, 1);
        items.forEach(item -> {
            log.info("zset遍历==>{}", JSON.toJSONString(item));
        });
    }

    @Test
    public void del() {
        redisTemplate.opsForList().remove("userList", 100, "张无忌");
        redisTemplate.opsForSet().remove("userSet", "张无忌");
        redisTemplate.opsForHash().delete("userHash", "dada");
        stringRedisTemplate.delete("username");
    }

}
