package com.wq.seckill.vo;
//枚举类开发
//失败类

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    //枚举对象写在属性的前方

    //通用
    SUCCESS(200, "SUCCESS"),
    BING_ERROR(500212,"参数绑定异常"),
    ERROR(500,"ERROR，服务端异常"),
    //登录
    LOGIN_ERROR(500210,"用户ID/密码错误"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    MOBILE_NOT_EXIST(500212,"手机号码不存在"),

    ENTRY_STOCK(500500,"库存不足"),
    REPEAT_ERROR(500501,"该商品每人限购一件"),
    SESSION_ERROR(500502, "用户信息有误"),
    REQUEST_ILLEGAL(500503, "请求非法"),
    SEC_KILL_WAIT(500504, "排队中...."),
    CAPTCHA_ERROR(500505, "验证码错误"),
    ACCESS_LIMIT_REACHED(500506, "请求频繁，请稍后再试"),
    SEC_KILL_RETRY(500507, "秒杀失败，请稍后再试"),
    //秒杀模块
    //失败
    //密码更新失败
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败");






    private final Integer code;
    private final String message;



}
