package com.wq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wq.seckill.pojo.User;
import com.wq.seckill.vo.LoginVo;
import com.wq.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {

    //完成用户的登录校验
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    //根据cookie的ticket值 获取用户的信息
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    //更新密码
    RespBean updatePassword(String userTicket,String password,
                            HttpServletRequest request, HttpServletResponse response);

}
