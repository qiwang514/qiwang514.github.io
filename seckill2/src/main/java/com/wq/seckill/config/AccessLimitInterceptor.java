package com.wq.seckill.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.wq.seckill.pojo.User;
import com.wq.seckill.service.UserService;
import com.wq.seckill.util.CookieUtil;
import com.wq.seckill.vo.RespBean;
import com.wq.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

//自定义拦截器
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {



    //装配需要的组件
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    //得到user对象 并放到ThreadLocal
    //去处理Access注解
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            //获取登录 的 user 对象
            User user = getUser(request, response);
            //存入到Threadlocal
            UserContext.setUser(user);

            //处理自定义的注解
            //把handler 转成HandlerMethod
            HandlerMethod hm = (HandlerMethod) handler;
            //获取目标方法的注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
//                目标方法没有注解 该接口 没有处理防刷 直接放行
                return true;

            }
            //获取注解的属性值
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            if (needLogin) {
                //必须登录才能访问目标方法
                if (user==null){
                    //用户没有登录 返回提示
                    render(response,RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }
            String uri = request.getRequestURI();
            String key = uri + ":"+user.getId();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count =(Integer) valueOperations.get(key);
            if (count == null) {
                //这是第一次访问 没有key 初始化key
                valueOperations.set(key,1,second, TimeUnit.SECONDS);

            }else if (count < maxCount){
                //正常访问
                valueOperations.increment(key);
            }else {
                //刷接口 限流
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }



        }

        return true;
    }
    //构建返回对象 以流的形式返回
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        //构建RespBean
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }

    //单独编写方法 得到登录对象user
    private User getUser(HttpServletRequest request,HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (!StringUtils.hasText(ticket)) {
            return null;//用户未登录
        }
        return  userService.getUserByCookie(ticket,request,response);


    }


}
