package top.desky.example.redis.cache3.test.delay;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.desky.example.redis.cache3.util.TimeConstant;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列测试，基于redisson实现
 * Created by zealous on 2019-02-19.
 */
public class RDelayedQueueTest {
    private static final Logger log = LoggerFactory.getLogger(RDelayedQueueTest.class);

    private static RedissonClient client;
    private static RBlockingQueue<String> blockingQueue;

    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(1);
        client = Redisson.create(config);

        blockingQueue = client.getBlockingQueue("order_redisson");
    }

    public static void main(String[] args) {
        producer();
        consumer();
    }

    private static void producer() {
        RDelayedQueue<String> delayedQueue = client.getDelayedQueue(blockingQueue);

        String orderId;
        long expire;
        for (int i = 1; i <= 10; i++) {
            orderId = "str" + i;
            expire = i * 10;
            log.info("订单==>{}完成，将会在{}秒后被取消", orderId, expire);
            delayedQueue.offer(orderId, expire, TimeUnit.SECONDS);
        }
    }

    private static void consumer() {
        String orderId = null;
        String now;
        int i = 0;
        while (true) {
            log.info("====" + i);
            try {
                orderId = blockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            now = TimeConstant.DEFAULT_FORMATTER_LT2.format(LocalTime.now());
            log.info("订单是==>{}, 订单取消时间:{}", orderId, now);

            i++;
        }
    }

}
