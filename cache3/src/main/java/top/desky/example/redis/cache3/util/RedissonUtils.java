package top.desky.example.redis.cache3.util;

import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zealous on 2018/10/17.
 */
@Component
public class RedissonUtils {

    private final static Logger log = LoggerFactory.getLogger(RedissonUtils.class);

    @Autowired
    private RedissonClient client;

    /**
     * 公平锁
     *
     * @param key 键
     * @return 锁。注意返回null时，redis不可用，要确保业务正常
     */
    private RLock getFLock(String key) {
        if (client == null) {
            log.error("异常，初始化redisson连接失败，对象为null问题");
        }

        RLock rLock = null;
        try {
            rLock = client.getFairLock(key);
        } catch (Exception e) {
            log.info("redis高可用生效中。。");
            log.error("获取公平锁发生异常，详情是==>{}", e.fillInStackTrace());
        }
        return rLock;
    }

    /**
     * 加锁
     *
     * @param key         关键字，key值
     * @param waitTime    竞争锁最多等待时间,单位是秒
     * @param releaseTime 锁自动释放时间,单位是秒
     * @return
     * @throws InterruptedException
     */
    public RLock lock(String key, int waitTime, int releaseTime) throws InterruptedException {
        RLock rLock = this.getFLock(key);
        rLock.tryLock(waitTime, releaseTime, TimeUnit.SECONDS);
        return rLock;
    }

    //解锁
    public void unLock(RLock rLock) {
        if (rLock != null) {
            try {
                rLock.unlock();
            } catch (Exception ex) {
                log.warn("redis解锁发生异常，堆栈是==>{}", ex.fillInStackTrace());
            }
        }
    }

    /**
     * 阻塞队列
     */
    private RBoundedBlockingQueue<Map<String, String>> topicQueue() {
        if (client == null) {
            log.error("异常，初始化redisson连接失败，对象为null问题");
        }

        RBoundedBlockingQueue<Map<String, String>> queue = client.getBoundedBlockingQueue("topicQueue");
        queue.trySetCapacity(50);
        return queue;
    }

    /**
     * 阻塞队列, put
     *
     * @param topicMsg
     */
    public void putQueue(Map<String, String> topicMsg) {
        try {
            topicQueue().put(topicMsg);
        } catch (InterruptedException e) {
            log.warn("向redis阻塞队列中放置数据出现异常 => {}", e);
        }
    }

    /**
     * 阻塞队列, take
     */
    public Map<String, String> takeQueue() {
        Map<String, String> topicMsg = null;
        try {
            topicMsg = topicQueue().take();
        } catch (InterruptedException e) {
            log.warn("从redis阻塞队列获取数据出现异常 => {}", e);
        }
        return topicMsg;
    }
}
