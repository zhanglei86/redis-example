package top.desky.example.redis.cache2.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.desky.example.redis.cache2.util.RedisLockUtils;

import java.util.concurrent.TimeUnit;

/**
 * 参考[distributed lock]的笔记
 */
public class LockTest extends BaseTestCase {

    @Autowired
    private RedisLockUtils lockUtils;

    @Test
    public void test1() {
        final String lKey = "zLock";

        try {
            lockUtils.getLock(lKey, 20 * 1000, 100 * 1000);
            log.info("拿到了锁，睡一会儿");
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ire) {
            ire.printStackTrace();
        } finally {
            lockUtils.releaseLock(lKey);
        }
        log.info("释放了锁");
    }
}
