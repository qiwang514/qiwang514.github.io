package com.wq.seckill.controller;


import com.wq.seckill.pojo.User;
import com.wq.seckill.service.UserService;
import com.wq.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    //更改密码
    @Resource
    private UserService userService;

    @RequestMapping("/info")
    @ResponseBody
    //方法 返回登陆用户的信息 同时接收携带的参数
    public RespBean info(User user) {

//        System.out.println("address->"+address);
        return RespBean.success(user);

    }

    //更新密码
    @RequestMapping("/updpwd")
    @ResponseBody
    public RespBean updatePassword(String userTicket, String password,
                                   HttpServletRequest request, HttpServletResponse response) {

        //装配修改密码的组件
        return userService.updatePassword(userTicket, password, request, response);

    }
}
