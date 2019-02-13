package top.desky.example.redis.cache1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import top.desky.example.redis.cache1.dal.dao.UserRepository;
import top.desky.example.redis.cache1.dal.model.User;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Cacheable(cacheManager = "cacheManager", value = "users", key = "#userId", unless = "#result.money < 10000")
    @GetMapping(value = "/{userId}")
    public Object getUser(@PathVariable Long userId) {
        log.info("获取user信息根据ID-> {}.", userId);
        return userRepository.findById(userId).orElse(new User());
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public User updatePersonByID(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @CacheEvict(value = "users", allEntries = true)
    @DeleteMapping("/{id}")
    public void deleteUserByID(@PathVariable Long id) {
        log.info("删除用户根据ID-> {}", id);
        userRepository.deleteById(id);
    }
}
