package top.desky.example.redis.cache2.test.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Set;

/**
 * 延迟队列测试，基于jedis实现
 * Created by zealous on 2018/9/30.
 */
public class JedisDelayTest {
    private static final Logger log = LoggerFactory.getLogger(JedisDelayTest.class);

    private static JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
    private static Jedis j = jedisPool.getResource();

    private static final String KEY = "order_jedis";

    public static void main(String[] args) {
        producer();
        consumer();
    }

    private static void producer() {
        String orderId;
        long expire;
        for (int i = 1; i <= 10; i++) {
            orderId = "str" + i;
            //延迟x秒
            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.SECOND, i * 10);
            expire = (int) (cal1.getTimeInMillis() / 1000);

            log.info("订单==>{}完成，将会在{}的时间被取消", orderId, expire);
            j.zadd(KEY, expire, orderId);
        }
    }

    private static void consumer() {
        String orderId;
        String now;
        while (true) {
            Set<Tuple> items = j.zrangeWithScores(KEY, 0, 1);
            if (items == null || items.isEmpty()) {
                log.info("当前没有等待的任务");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Calendar cal = Calendar.getInstance();
            int nowSecond = (int) (cal.getTimeInMillis() / 1000);

            int score = (int) ((Tuple) items.toArray()[0]).getScore();
            if (nowSecond >= score) {
                orderId = ((Tuple) items.toArray()[0]).getElement();
                j.zrem(KEY, orderId);

                now = DateTimeFormatter.ofPattern("HH:mm:ss-SSS").format(LocalTime.now());
                log.info("订单是==>{}, 订单取消时间:{}", orderId, now);
            }
        }
    }

}
