package top.desky.example.redis.cache3.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Created by zealous on 2018/10/15.
 */
//@Configuration
public class RRedissonConfig {

    private final static Logger log = LoggerFactory.getLogger(RRedissonConfig.class);

    @Value("${spring.redis.cluster.nodes}")
    private String cluster;
    @Value("${spring.redis.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient rClient() {
        String[] nodes = cluster.split(",");
        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }

        Config config = new Config();
        ClusterServersConfig csConfig = config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress(nodes);
        // 密码
        if (StringUtils.isNotBlank(password)) {
            csConfig.setPassword(password);
        }

        try {
            return Redisson.create(config);
        } catch (Exception e) {
            log.error("redisson error, detail==>{}", e.getLocalizedMessage());
        }
        return null;
    }

}
