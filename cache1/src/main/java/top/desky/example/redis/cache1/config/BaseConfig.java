package top.desky.example.redis.cache1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.desky.example.redis.cache1.dal.dao.UserRepository;
import top.desky.example.redis.cache1.dal.model.User;

@Configuration
public class BaseConfig {

    private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Bean
    public Character initUser() {
        log.info("开始初始化user ->user count ->{}", userRepository.count());
        User james = new User("James", 2000);
        User potter = new User("Potter", 4000);
        User dumbledore = new User("Dumbledore", 999999);

        userRepository.save(james);
        userRepository.save(potter);
        userRepository.save(dumbledore);
        log.info("初始化完成 数据-> {}.", userRepository.findAll());

        return 'c';
    }

}
