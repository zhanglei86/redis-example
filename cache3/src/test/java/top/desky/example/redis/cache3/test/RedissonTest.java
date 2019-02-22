package top.desky.example.redis.cache3.test;

import org.junit.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.desky.example.redis.cache3.BaseTestCase;

import java.math.BigDecimal;

/**
 * Created by zealous on 2019-02-19.
 */
public class RedissonTest extends BaseTestCase {

    @Autowired
    private RedissonClient client;

    @Test
    public void test1() {
        RMap<String, Object> map = client.getMap("myMap");
        map.put("z1", "zhanglei");
        map.put("z2", BigDecimal.ZERO);
        map.put("z3", Integer.MAX_VALUE);
        map.put("z4", Long.MAX_VALUE);
    }

}
