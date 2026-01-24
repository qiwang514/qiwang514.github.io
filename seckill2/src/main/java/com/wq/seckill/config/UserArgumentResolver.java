package com.wq.seckill.config;

import com.wq.seckill.pojo.User;
import com.wq.seckill.service.UserService;
import com.wq.seckill.util.CookieUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {


    @Resource
    private UserService userService;

    //判断当前解析的参数类型是否是需要的
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //获取参数是不是 user 类型
        Class<?> aClass = methodParameter.getParameterType();
        //如果为 t, 就执行 resolveArgument
        return aClass == User.class;
    }

    //上面返回为true  就执行下面resolveArgument
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
//        HttpServletRequest request =
//                nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response =
//                nativeWebRequest.getNativeResponse(HttpServletResponse.class);
//
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
//
//        if (!StringUtils.hasText(ticket)) {
//            return null;
//        }
//        User user = userService.getUserByCookie(ticket,request,response);
//        return userService.getUserByCookie(ticket, request, response);
        return UserContext.getUser();
    }
}
