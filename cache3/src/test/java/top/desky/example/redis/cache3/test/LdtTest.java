package top.desky.example.redis.cache3.test;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LdtTest {

    public static final ZoneId DEFAULT_ZONEID = ZoneId.systemDefault();

    @Test
    public void test1() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime ldt1 = now.minusDays(1);
        LocalDateTime ldt2 = now.plusDays(1);
        boolean b1 = ldt1.isBefore(ldt2);

        Date d1 = Date.from(ldt1.atZone(DEFAULT_ZONEID).toInstant());
        Date d2 = Date.from(ldt2.atZone(DEFAULT_ZONEID).toInstant());
        boolean b2 = d1.before(d2);

        System.out.println("d1==>" + d1 + ", d2==>" + d2);
        System.out.println("b1==>" + b1);
        System.out.println("b2==>" + b2);

    }
}
