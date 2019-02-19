package top.desky.example.redis.cache3.test.delay;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.desky.example.redis.cache3.bo.CallCdr;
import top.desky.example.redis.cache3.util.TimeConstant;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by zealous on 2019-02-19.
 */
public class RDelayedQueueTest {
    private static final Logger log = LoggerFactory.getLogger(RDelayedQueueTest.class);
    private static final String STR_HOST = "redis://localhost:6379";

    private static RBlockingQueue<CallCdr> blockingFairQueue;
    private static RDelayedQueue<CallCdr> delayedQueue;

    static {
        Config config = new Config();
        config.useSingleServer().setAddress(STR_HOST).setDatabase(2);
        RedissonClient client = Redisson.create(config);

        blockingFairQueue = client.getBlockingQueue("delay_queue");
        delayedQueue = client.getDelayedQueue(blockingFairQueue);
    }

    public static void main(String[] args) {
        put();
        get();
    }

    private static void put() {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 一分钟以后将消息发送到指定队列, 相当于1分钟后取消订单
            // 延迟队列包含callCdr 1分钟，然后将其传输到blockingFairQueue中
            CallCdr callCdr = new CallCdr(30000.00);
            callCdr.setPutTime();
            //在1分钟后就可以在blockingFairQueue 中获取callCdr了
            delayedQueue.offer(callCdr, 1, TimeUnit.MINUTES);
        }

        //delayedQueue.destroy();
    }

    private static void get() {
        CallCdr callCdr = null;
        while (true) {
            try {
                callCdr = blockingFairQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("订单取消时间:{}, 对比订单生成时间==>{}", TimeConstant.DEFAULT_FORMATTER_LT2.format(LocalTime.now()), callCdr.getPutTime());
        }
    }

}
