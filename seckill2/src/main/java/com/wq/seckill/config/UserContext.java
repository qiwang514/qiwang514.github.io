package com.wq.seckill.config;

import com.wq.seckill.pojo.User;

public class UserContext {
    //每个线程都有自己的ThreadLocal  共享数据存在这里 保证线程安全
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();
    public static void setUser(User user) {
        userHolder.set(user);
    }
    public static User getUser() {
        return userHolder.get();
    }
}
