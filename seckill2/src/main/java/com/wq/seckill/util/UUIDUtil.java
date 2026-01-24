package com.wq.seckill.util;

import java.util.UUID;

//生成UUID的工具类
public class UUIDUtil {


    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
