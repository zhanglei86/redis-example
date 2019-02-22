package top.desky.example.redis.cache3.test.lock;

import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import top.desky.example.redis.cache3.BaseTestCase;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by zealous on 2019-02-21.
 */
public class RLockTest extends BaseTestCase {

    @Autowired
    private RedissonClient client;

    private static final String KEY = "myLock";

    @Test
    public void test1() {
        IntStream.rangeClosed(1, 5)
                .parallel()
                .forEach(i -> executeLock());
        executeLock();
    }

    public void executeLock() {
        RLock lock = client.getLock(KEY);
        boolean locked = false;
        try {
            log.info("1.try lock");
            //locked = lock.tryLock();
            locked = lock.tryLock(1, 2, TimeUnit.MINUTES);
            log.info("2.get lock result:{}", locked);
            if (locked) {
                TimeUnit.MINUTES.sleep(1);
                log.info("3.get lock and finish");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("4.enter unlock");
            if (locked) {
                lock.unlock();
            }
        }
    }
}
