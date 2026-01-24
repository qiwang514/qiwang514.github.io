package com.wq.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wq.seckill.exception.GlobalException;
import com.wq.seckill.mapper.UserMapper;
import com.wq.seckill.pojo.User;
import com.wq.seckill.service.UserService;
import com.wq.seckill.util.CookieUtil;
import com.wq.seckill.util.MD5Util;
import com.wq.seckill.util.UUIDUtil;
import com.wq.seckill.util.ValidatorUtil;
import com.wq.seckill.vo.LoginVo;
import com.wq.seckill.vo.RespBean;
import com.wq.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {



    @Resource
    private UserMapper userMapper;

    @Resource
    //配置RedisTemplate 操作redis
    private RedisTemplate redisTemplate;


    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        //接收loginvo的两个变量
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //判断是否为空
//        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        //检验手机号是否合格
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }



        //查询Db  看看用户是否存在
        User user = userMapper.selectById(mobile);
        if (user == null) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //如果用户存在 则比对密码
        //password是中间密码 客户端已经加密一次了
        if (!MD5Util.midPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        //给每个用户生成唯一票据
        String ticket = UUIDUtil.uuid();
        //保存到session30分钟
//        request.getSession().setAttribute(ticket,user);

        //为了实现分布式session 把登录的用户存放到redis
        redisTemplate.opsForValue().set("user:"+ticket,user);


        //将ticket保存到cookie
        CookieUtil.setCookie(request, response, "userTicket",ticket);

        //成功

        //需要返回的userTicket （票据）
        return RespBean.success(ticket);

    }


    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasText(userTicket)) {
            return null;
        }
        //根据userTicket去redis中寻找user
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            //刷新一下时间 延长cookie
            CookieUtil.setCookie(request, response, "userTicket", userTicket);

        }
        return user;
    }


    //更新用户的密码
    @Override
    public RespBean updatePassword(String userTicket, String password,
                                   HttpServletRequest request, HttpServletResponse response) {

        //password 是明文密码

        User user
                = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int i = userMapper.updateById(user);
        if (i == 1) {

            //更新成功
            //删除该用户在redis的内容
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
