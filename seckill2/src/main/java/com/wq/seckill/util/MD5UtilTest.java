package com.wq.seckill.util;


import org.junit.jupiter.api.Test;

//测试MD5Unit方法的使用
public class MD5UtilTest {

    @Test
    public void test1() {
        //密码12345
        //一次加密
//        System.out.println(MD5Util.inputPassToMidpass("12345"));
//        //二次加密
//        System.out.println(MD5Util.midPassToDBPass("13a4ea7a48838c78a1b537aafc121308","ab125739"));
//
//        //直接加密
        System.out.println(MD5Util.inputPassToDBPass("12345","ab125739"));
    }
    @Test
    public void test2() {


    }
}
