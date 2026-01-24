package com.wq.seckill.controller;


import com.wq.seckill.mapper.UserMapper;
import com.wq.seckill.service.UserService;
import com.wq.seckill.vo.LoginVo;
import com.wq.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Resource
    private UserService userService;


    @RequestMapping("/toLogin")
    //编写方法 可以进入到登录页面
    public String toLogin(){
        return "login";
    }



    @RequestMapping("/doLogin")
    @ResponseBody
    //处理登录请求
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){

        log.info("{}",loginVo);//观察

        return userService.doLogin(loginVo, request, response);
    }




}
