package top.desky.example.redis.cache2.test;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import top.desky.example.redis.cache2.Cache2Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Cache2Application.class})
public class BaseTestCase extends AbstractJUnit4SpringContextTests {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private long beginTime;
    private long endTime;

    static {
        System.setProperty("appName", "cache2-testCase");
    }

    @Before
    public void begin() {
        beginTime = System.currentTimeMillis();
    }

    @After
    public void end() {

        endTime = System.currentTimeMillis();

        System.err.println("");
        System.err.println("#######################################################");
        System.err.println("elapsed time : " + (endTime - beginTime) + "ms");
        System.err.println("#######################################################");
        System.err.println("");
    }

    public void printData(Object data) {
        System.err.println("data ==> " + JSON.toJSONString(data));
    }

    @Test
    public void testCase() {
        System.out.println("base testCase finish!");
    }

}
