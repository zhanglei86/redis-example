package top.desky.example.redis.cache1;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 延迟队列测试，基于spring-cache实现
 * Created by zealous on 2019-02-18.
 */
public class DelayTest extends BaseTestCase {

    @Autowired
    private RedisTemplate<String, Object> template;

    private static final String KEY = "order_spring";

    @Test
    public void test1() {
        producer();
        consumer();
    }

    @Test
    public void test2() {
        CountDownLatch cdl = new CountDownLatch(threadNum);
        producer();

        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                consumer();
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
    private void producer() {
        String orderId;
        long expire;
        for (int i = 1; i <= 10; i++) {
            orderId = "str" + i;
            //延迟x秒
            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.SECOND, i * 10);
            expire = (int) (cal1.getTimeInMillis() / 1000);

            log.info("订单==>{}完成，将会在{}的时间被取消", orderId, expire);
            template.opsForZSet().add(KEY, orderId, expire);
        }
    }

    //消费者，取订单
    private void consumer() {
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
                int score = item.getScore().intValue();
                if (nowSecond >= score) {
                    String orderId = String.valueOf(item.getValue());

                    Long res = ops.remove(KEY, orderId);
                    if (res != null && res > 0) {
                        String now = DateTimeFormatter.ofPattern("HH:mm:ss-SSS").format(LocalTime.now());
                        log.info("订单是==>{}, 订单取消时间:{}", orderId, now);
                    }
                }
            });
        }
    }

}
