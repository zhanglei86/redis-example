package top.desky.example.redis.cache2.service;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * redis 订阅推送案例
 *
 * @author atp
 */
@Service
public class RedisPubSub {
    private static final Logger logger = LoggerFactory.getLogger(RedisPubSub.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ChannelTopic topic = new ChannelTopic("/redis/pubsub");

    @Scheduled(initialDelay = 5000, fixedDelay = 2000)
    private void schedule() {
        logger.info("publish message");
        publish("admin", "hey you must go now!");
    }

    /**
     * 推送消息
     */
    private void publish(String publisher, String content) {
        logger.info("message send {} by {}", content, publisher);

        SimpleMessage pushMsg = new SimpleMessage();
        pushMsg.setContent(content);
        pushMsg.setCreateTime(new Date());
        pushMsg.setPublisher(publisher);

        redisTemplate.convertAndSend(topic.getTopic(), pushMsg);
    }

    @Component
    public class MessageSubscriber {

        public void onMessage(SimpleMessage message, String pattern) {
            logger.info("topic {} received {} ", pattern, JSON.toJSON(message));
        }
    }

    public static class SimpleMessage {

        private String publisher;
        private String content;
        private Date createTime;

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

    }

}