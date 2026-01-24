package com.wq.seckill.controller;

import com.wq.seckill.pojo.User;
import com.wq.seckill.service.GoodsService;
import com.wq.seckill.service.UserService;
import com.wq.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private UserService userService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    //手动渲染 需要模板解析器
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;


//    @RequestMapping("/toList")
//    //进入到商品列表页
//    //校验是否已经登录成功
////    public String toList(HttpSession session, Model model,
////                         @CookieValue("userTicket") String ticket) {
////    public String toList(Model model,
////                         @CookieValue("userTicket") String ticket,
////                         HttpServletRequest request, HttpServletResponse response) {
////
////        //cookie没有生成
////        if (!StringUtils.hasText(ticket)) {
////            return "login";
////        }
////        //通过ticket 获取session中的用户
//////        User user = (User) session.getAttribute(ticket);
////
////
////        User user = userService.getUserByCookie(ticket, request, response);
////        if (null == user) {
////            return "login";
////        }
////
////        //将user放入model
////        model.addAttribute("user", user);
////
////        return "goodsList";
////    }
//
//
//
//    public String toList(Model model,
//                         User user) {
//
//        if (null == user) {
//            return "login";
//        }
//        //将user放入model
//        model.addAttribute("user", user);
//        //将商品列表信息 放入到model 携带下一个模板使用
//        model.addAttribute("goodsList",goodsService.findGoodsVo());
//        return "goodsList";
//    }
//    @RequestMapping("/toList")
//进入到商品列表页
//校验是否已经登录成功
//    public String toList(HttpSession session, Model model,
//                         @CookieValue("userTicket") String ticket) {
//    public String toList(Model model,
//                         @CookieValue("userTicket") String ticket,
//                         HttpServletRequest request, HttpServletResponse response) {
//
//        //cookie没有生成
//        if (!StringUtils.hasText(ticket)) {
//            return "login";
//        }
//        //通过ticket 获取session中的用户
////        User user = (User) session.getAttribute(ticket);
//
//
//        User user = userService.getUserByCookie(ticket, request, response);
//        if (null == user) {
//            return "login";
//        }
//
//        //将user放入model
//        model.addAttribute("user", user);
//
//        return "goodsList";
//    }

//    ==========================================================================================================
//进入商品列表页 使用redis优化 装配redis模板
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model,
                         User user,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        if (null == user) {
            return "login";
        }

        //先到redis获取页面 如果有 直接返回
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //若之前登陆过 会有商品列表list的key 保存在redis中
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)){
            // 若有页面 直接跳转
            return html;
        }

        //将user放入model
        model.addAttribute("user", user);
        //将商品列表信息 放入到model 携带下一个模板使用
        model.addAttribute("goodsList",goodsService.findGoodsVo());

        //从redis没有获取页面 就手动渲染页面并存到redis中
        WebContext webContext =
                new WebContext(request, response, request.getServletContext(),
                        request.getLocale(), model.asMap());

        //此处的goodsList  是resource对应的列表页面 和redis中的不是一个
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.hasText(html)){
            //保存 redis 设置每60s 更新一次
            // 此处的goodsList就是redis中的key
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }

        return html;
    }






//    @RequestMapping("/toDetail/{goodsId}")
//    //进入到商品详情页面
//    public String toDetail(Model model,User user,@PathVariable Long goodsId) {
//        if (null == user) {
//            //该用户没有登陆过
//            return "login";
//        }
//        model.addAttribute("user", user);
//        //通过id获取秒杀商品信息
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //将查询到的goodsVo放入到nodel 携带给下一个模板使用
//        model.addAttribute("goods", goodsVo);
//
//        //返回秒杀商品的详情  同时返回该商品的秒杀状态和秒杀剩余时间
//        //配合前端展示秒杀商品的状态
//
//
//        //开始秒杀时间
//        Date startDate = goodsVo.getStartDate();
//        //结束秒杀时间
//        Date endDate = goodsVo.getEndDate();
//        //当前时间
//        Date nowdate = new Date();
//
//        //变量 seckillstatus 秒杀状态 0：未开始 1 秒杀进行中  2秒杀已结束
//        int secKillStatus = 0;
//
//        //remainseconds 秒杀剩余秒数：>0 :表示还有某某时间才开始秒杀 0：秒杀进行中 -1:秒杀已经结束
//        int remainSeconds = 0;
//
//        if (nowdate.before(startDate)) {
//            remainSeconds=(int)(startDate.getTime() - nowdate.getTime())/1000;
//        }else if (nowdate.after(endDate)) {
//            secKillStatus = 2;
//            remainSeconds=-1;
//
//        }else {
//            //秒杀进行中
//            secKillStatus = 1;
//            remainSeconds=0;
//        }
//        //将状态和剩余时间 放到 model中 携带给模板页使用
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//
//        return "goodsDetail";
//    }



//    ==========================================================================================================
    //优化redis
    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    //进入到商品详情页面
    public String toDetail(Model model,
                           User user,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           @PathVariable Long goodsId) {
        if (null == user) {
            //该用户没有登陆过
            return "login";
        }

        //先到redis获取页面 如果有 直接返回
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //若之前登陆过 会有商品列表list的key 保存在redis中
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasText(html)){
            // 若有页面 直接跳转
            return html;
        }


        model.addAttribute("user", user);
        //通过id获取秒杀商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //将查询到的goodsVo放入到nodel 携带给下一个模板使用
        model.addAttribute("goods", goodsVo);

        //返回秒杀商品的详情  同时返回该商品的秒杀状态和秒杀剩余时间
        //配合前端展示秒杀商品的状态

        //开始秒杀时间
        Date startDate = goodsVo.getStartDate();
        //结束秒杀时间
        Date endDate = goodsVo.getEndDate();
        //当前时间
        Date nowdate = new Date();

        //变量 seckillstatus 秒杀状态 0：未开始 1 秒杀进行中  2秒杀已结束
        int secKillStatus = 0;

        //remainseconds 秒杀剩余秒数：>0 :表示还有某某时间才开始秒杀 0：秒杀进行中 -1:秒杀已经结束
        int remainSeconds = 0;

        if (nowdate.before(startDate)) {
            remainSeconds=(int)(startDate.getTime() - nowdate.getTime())/1000;
        }else if (nowdate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds=-1;

        }else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds=0;
        }
        //将状态和剩余时间 放到 model中 携带给模板页使用
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //手动页面渲染
        //从redis没有获取页面 就手动渲染页面并存到redis中
        WebContext webContext =
                new WebContext(request, response, request.getServletContext(),
                        request.getLocale(), model.asMap());

        //此处的goodsList  是resource对应的列表页面 和redis中的不是一个
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (StringUtils.hasText(html)){
            //保存 redis 设置每60s 更新一次
            // 此处的goodsList就是redis中的key
            valueOperations.set("goodsDetail:"+goodsId,html,60, TimeUnit.SECONDS);
        }


        return html;
    }


}
