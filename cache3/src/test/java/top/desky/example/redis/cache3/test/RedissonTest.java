package top.desky.example.redis.cache3.test;

import org.junit.Test;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.desky.example.redis.cache3.BaseTestCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        //RLock lock = client.getLock("myLock");
    }

    @Test
    public void test2() {
        RQueue<String> queue = client.getQueue("anyQueue");
        queue.add("zealous");
        boolean b1 = queue.contains("zea");
        String str1 = queue.peek();
        String str2 = queue.poll();

        RDelayedQueue<String> delayedQueue = client.getDelayedQueue(queue);
        // 10秒钟以后将消息发送到指定队列
        delayedQueue.offer("msg1", 10, TimeUnit.SECONDS);
        // 一分钟以后将消息发送到指定队列
        delayedQueue.offer("msg2", 1, TimeUnit.MINUTES);

        int i = 0;
        while (true) {
            System.out.println("====" + i);
            i++;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ite) {
                ite.printStackTrace();
            }
            List<String> list = queue.readAll();
            list.forEach(System.out::println);
        }
    }
}
