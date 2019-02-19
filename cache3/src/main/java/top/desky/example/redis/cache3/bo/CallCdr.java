package top.desky.example.redis.cache3.bo;

import top.desky.example.redis.cache3.util.TimeConstant;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Created by zealous on 2019-02-19.
 */
@Deprecated
public class CallCdr implements Serializable {

    private String name;
    private int age;
    private String wife;
    private Double salary;
    private String putTime;

    public CallCdr() {
    }

    public CallCdr(Double salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWife() {
        return wife;
    }

    public void setWife(String wife) {
        this.wife = wife;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getPutTime() {
        return putTime;
    }

    public void setPutTime() {
        LocalTime now = LocalTime.now();
        this.putTime = TimeConstant.DEFAULT_FORMATTER_LT2.format(now);
    }
}