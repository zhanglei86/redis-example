package top.desky.example.redis.cache1.dal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.desky.example.redis.cache1.dal.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
