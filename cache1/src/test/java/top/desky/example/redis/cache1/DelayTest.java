package top.desky.example.redis.cache1;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 延迟队列测试，基于zset实现
 * Created by zealous on 2019-02-18.
 */
public class DelayTest extends BaseTestCase {

    @Autowired
    private RedisTemplate<String, Object> template;

    private static final String KEY = "orderId";
    private static final String OD = "order100";

    @Test
    public void test1() {
        productionDelayMessage();
        consumerDelayMessage();
    }

    @Test
    public void test2() {
        CountDownLatch cdl = new CountDownLatch(threadNum);
        productionDelayMessage();

        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                consumerDelayMessage();
            }).start();
            cdl.countDown();
        }

        /**
         * 并发问题，解决：
         * 1).分布式锁；
         * 2).只有remove返回成功才输出
         */

        System.out.println("ok");
    }

    private static final int threadNum = 10;

    class DelayMessage implements Runnable {
        public void run() {

        }
    }

    //生产者,生成5个订单放进去
    private void productionDelayMessage() {
        Calendar cal1 = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            //延迟3秒
            cal1.add(Calendar.SECOND, 3);
            long second3later = cal1.getTimeInMillis() / 1000;
            String value = OD + i;
            template.opsForZSet().add(KEY, value, second3later);

            long start = System.currentTimeMillis();
            log.info("{}ms, redis生成了一个订单任务：订单ID为{}", start, value);
        }
    }

    //消费者，取订单
    private void consumerDelayMessage() {
        ZSetOperations ops = template.opsForZSet();
        while (true) {
            Set<ZSetOperations.TypedTuple<String>> items = ops.rangeWithScores(KEY, 0, 1);
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
            items.forEach(item -> {
                long score = item.getScore().longValue();
                if (nowSecond >= score) {
                    String orderId = String.valueOf(item.getValue());

                    Long res = ops.remove(KEY, orderId);
                    if (res != null && res > 0) {
                        long start = System.currentTimeMillis();
                        log.info("{}ms, redis消费了一个任务：消费的订单orderId为{}", start, orderId);
                    }
                }
            });

        }
    }

}
