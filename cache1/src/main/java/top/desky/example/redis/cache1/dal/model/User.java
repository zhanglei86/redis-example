package top.desky.example.redis.cache1.dal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "SEQ_USER", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private Long id;
    private String name;
    private long money;

    public User() {
    }

    public User(String name, long money) {
        this.name = name;
        this.money = money;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', money=%d}", id, name, money);
    }

    //省略Getter 和 Setter
}
