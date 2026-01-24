package com.wq.seckill.util;


import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    //校验手机号码的正则表达式
    private static final Pattern mobile_pattern = Pattern.compile("^[1][3-9][0-9]{9}$");

    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }
        //正则表达式校验
        Matcher matcher = mobile_pattern.matcher(mobile);

        return matcher.matches();

    }

    @Test
    public void t1(){

        String mobile = "11111111111111111";
        System.out.println(isMobile(mobile));
    }


}
//完成一些校验工作
