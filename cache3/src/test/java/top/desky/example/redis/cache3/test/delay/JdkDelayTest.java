package top.desky.example.redis.cache3.test.delay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by zealous on 2019-02-19.
 */
public class JdkDelayTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("00000001");
        list.add("00000002");
        list.add("00000003");
        list.add("00000004");
        list.add("00000005");
        DelayQueue<JdkOrderDelay> queue = new DelayQueue<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            //延迟三秒取出
            queue.put(new JdkOrderDelay(list.get(i), TimeUnit.NANOSECONDS.convert(3, TimeUnit.SECONDS)));
            try {
                queue.take().print();
                System.out.println("After " + (System.currentTimeMillis() - start) + " MilliSeconds");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
