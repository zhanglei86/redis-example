package top.desky.example.redis.cache3.test.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zealous on 2019-02-19.
 */
public class JdkOrderDelay implements Delayed {

    private String orderId;
    private long timeout;

    JdkOrderDelay(String orderId, long timeout) {
        this.orderId = orderId;
        this.timeout = timeout + System.nanoTime();
    }

    public int compareTo(Delayed other) {
        if (other == this)
            return 0;
        JdkOrderDelay t = (JdkOrderDelay) other;
        long d = (getDelay(TimeUnit.NANOSECONDS) - t
                .getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }

    // 返回距离你自定义的超时时间还有多少
    public long getDelay(TimeUnit unit) {
        return unit.convert(timeout - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    void print() {
        System.out.println(orderId + "编号的订单要删除啦。。。。");
    }
}