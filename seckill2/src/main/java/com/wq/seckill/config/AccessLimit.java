package com.wq.seckill.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
//自定义注解
public @interface AccessLimit {
    int second();
    int maxCount();
    boolean needLogin() default true;

}
