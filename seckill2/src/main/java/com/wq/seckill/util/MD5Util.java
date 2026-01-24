package com.wq.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.codec.digest.DigestUtils.md5;

//工具类 根据密码设计方案提供相应的方法
public class MD5Util {

    //最基本的md5方法
    public static String getMD5(String str) {
        return DigestUtils.md5Hex(str);
    }

//    准备一个salt
    //前端使用的salt
    public static final String SALT ="abcdefgh";

    //加密加salt  md5（password+SALT）
    public static String inputPassToMidpass(String inputPass) {
        System.out.println(" SALT.charAt(0)=>"+SALT.charAt(0));
        System.out.println(" SALT.charAt(6)=>"+SALT.charAt(6));
        String str = SALT.charAt(0)+inputPass+SALT.charAt(6);
        return getMD5(str);

    }

    //把Midpass加salt(自带的salt)  再次加密
    public static String midPassToDBPass(String midpass,String salt) {

        String str = salt.charAt(1) + midpass + salt.charAt(3);
        return getMD5(str);
    }

    public static String inputPassToDBPass(String inputPass,String salt) {
        String midpass = inputPassToMidpass(inputPass);
        String DBpass = midPassToDBPass(midpass, salt);
        return DBpass;
    }




}
