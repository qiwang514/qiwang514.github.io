package com.wq.seckill.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.wq.seckill.config.AccessLimit;
import com.wq.seckill.pojo.Order;
import com.wq.seckill.pojo.SeckillMessage;
import com.wq.seckill.pojo.SeckillOrder;
import com.wq.seckill.pojo.User;
import com.wq.seckill.rabbitmq.MQSenderMessage;
import com.wq.seckill.service.GoodsService;
import com.wq.seckill.service.OrderService;
import com.wq.seckill.service.SeckillOrderService;
import com.wq.seckill.vo.GoodsVo;
import com.wq.seckill.vo.RespBean;
import com.wq.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@RequestMapping("/seckill")
@Controller
public class SeckillController implements InitializingBean {


    @Resource
    private GoodsService goodsService;

    //复购service
    @Resource
    private SeckillOrderService seckillOrderService;

    //抢购service
    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;

    //定义map  内存标记 记录是否秒杀商品还有内存
    private HashMap<Long,Boolean> entryStockMap = new HashMap<>();

    //装配消息生产者
    @Resource
    private MQSenderMessage mqSenderMessage;

//    //方法  处理用户抢购请求
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//        System.out.println("-----------开始秒杀-1.0-----------");//后续会做优化
//        if (user == null) {
//            //用户没有登录
//            return "login";
//        }
//        model.addAttribute("user", user);
//
//        //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //判断库存  是否可以进行秒杀
//        if (goodsVo.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//
//
//        //判断用户是否复购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
//                .eq("user_id", user.getId())
//                .eq("goods_id", goodsId));
//
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//        //开始抢购 装配service
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//
//
//        //秒杀成功 进入订单详情页面
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        //进入订单详情页面
//        return "orderDetail";
//
//    }
//================================================================================
//    //方法  处理用户抢购请求
//    //在redis加入了订单 所以要装配redis
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//        System.out.println("-----------开始秒杀-2.0-----------");
//        if (user == null) {
//            //用户没有登录
//            return "login";
//        }
//
//        model.addAttribute("user", user);
//        //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //判断库存  是否可以进行秒杀  也走了数据库
//        if (goodsVo.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//
//
////        //判断用户是否复购
////        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
////                .eq("user_id", user.getId())
////                .eq("goods_id", goodsId));
////
////        if (seckillOrder != null) {
////            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
////            return "seckillFail";//错误页面
////        }
////        ============================优化复购================================================
//
//        //在redis获取对应的秒杀订单
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
//                .get("order:" + user.getId() + ":" + goodsVo.getId());
//        if (null != o) {
//            //复购
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//
//        //开始抢购 装配service
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";//错误页面
//        }
//
//
//
//        //秒杀成功 进入订单详情页面
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        //进入订单详情页面
//        return "orderDetail";
//
//    }
//
//
//
//
//
//    }
//方法  处理用户抢购请求
//在redis加入了订单 所以要装配redis
//redia库存预减
//@RequestMapping("/doSeckill")
//public String doSecKill(Model model, User user, Long goodsId) {
//    System.out.println("-----------开始秒杀-3.0-----------");
//    if (user == null) {
//        //用户没有登录
//        return "login";
//    }
//
//    model.addAttribute("user", user);
//    //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
//    GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//    //判断库存  是否可以进行秒杀  也走了数据库
//    if (goodsVo.getStockCount() < 1){
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//
//    //在redis获取对应的秒杀订单
//    SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
//            .get("order:" + user.getId() + ":" + goodsVo.getId());
//    if (null != o) {
//        //复购
//        model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//
//    //库存预减
//    //当商品没有了 直接返回 减少去执行orderService.seckill的请求 防止线程堆积
//    //decrement具有原子性
//    Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
//    if (decrement < 0){
//        //恢复库存为0
//        redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
//        //没有库存
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//
//    }
//
//
//    //开始抢购 装配service
//    Order order = orderService.seckill(user, goodsVo);
//    if (order == null) {
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//    //秒杀成功 进入订单详情页面
//    model.addAttribute("order", order);
//    model.addAttribute("goods", goodsVo);
//
//    //进入订单详情页面
//    return "orderDetail";
//
//}

//减轻redis 加入内存标记
//@RequestMapping("/doSeckill")
//public String doSecKill(Model model, User user, Long goodsId) {
//    System.out.println("-----------开始秒杀-4.0-----------");
//    if (user == null) {
//        //用户没有登录
//        return "login";
//    }
//
//    model.addAttribute("user", user);
//    //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
//    GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//    //判断库存  是否可以进行秒杀  也走了数据库
//    if (goodsVo.getStockCount() < 1){
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//
//    //在redis获取对应的秒杀订单
//    SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
//            .get("order:" + user.getId() + ":" + goodsVo.getId());
//    if (null != o) {
//        //复购
//        model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//        return "seckillFail";//错误页面
//    }
//    //对map进行判断 内存标记
//    if (entryStockMap.get(goodsId)){
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//
//    //库存预减
//    //当商品没有了 直接返回 减少去执行orderService.seckill的请求 防止线程堆积
//    //decrement具有原子性
//    Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
//    if (decrement < 0){
//
//        //秒杀的商品  没有库存了
//        entryStockMap.put(goodsId,true);
//
//        //恢复库存为0
//        redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
//        //没有库存
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//
//    }
//
//
//    //开始抢购 装配service
//    Order order = orderService.seckill(user, goodsVo);
//    if (order == null) {
//        model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//        return "seckillFail";//错误页面
//    }
//
//    //秒杀成功 进入订单详情页面
//    model.addAttribute("order", order);
//    model.addAttribute("goods", goodsVo);
//
//    //进入订单详情页面
//    return "orderDetail";
//
//}


//加入消息队列 实现异步请求
//加入分布式锁 5.0 ----> 7.0
    @RequestMapping("/doSeckill")
    public String doSecKill(Model model, User user, Long goodsId) {
        System.out.println("-----------开始秒杀-5.0-----------");
        if (user == null) {
            //用户没有登录
            return "login";
        }

        model.addAttribute("user", user);
        //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存  是否可以进行秒杀  也走了数据库
        if (goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "seckillFail";//错误页面
        }


        //在redis获取对应的秒杀订单
        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsVo.getId());
        if (null != o) {
            //复购
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "seckillFail";//错误页面
        }
        //对map进行判断 内存标记
        if (entryStockMap.get(goodsId)){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "seckillFail";//错误页面
        }

        //分布式锁
        //准备工作UUID作为锁的值
        String uuid = UUID.randomUUID().toString();



        Boolean lock =
                redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);

        //获取锁成功
        if (lock) {

            //定义 lua 脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 使用 redis 执行 lua 执行
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);

            //写自己的业务
            Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
            if (decrement < 0){

                //秒杀的商品  没有库存了
                entryStockMap.put(goodsId,true);

                //恢复库存为0
                redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
                //没有库存

                //释放锁-lua脚本
                redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
                model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
                return "seckillFail";//错误页面

            }
            //释放锁
            redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);


        } else {
            //3 获取锁失败、返回信息, 这次抢购失败,请继续抢购
            model.addAttribute("errmsg", RespBeanEnum.SEC_KILL_RETRY.getMessage());
            return "secKillFail";//错误页面
        }


//        //库存预减
//        //当商品没有了 直接返回 减少去执行orderService.seckill的请求 防止线程堆积
//        //decrement具有原子性
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
//        if (decrement < 0){
//
//            //秒杀的商品  没有库存了
//            entryStockMap.put(goodsId,true);
//
//            //恢复库存为0
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
//            //没有库存
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "seckillFail";//错误页面
//
//        }

        //抢购 向消息队列发送秒杀请求 一旦发送消息 立马快速返回结果

    //    构建一个对象seckillMessage

        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        System.out.println("准备发送秒杀消息：" + JSONUtil.toJsonStr(seckillMessage));
        mqSenderMessage.senderSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
        model.addAttribute("errmsg", "排队中.....");
        return "seckillFail";//错误页面

    }

//加入秒杀安全  返回的是RespBean
//    @RequestMapping("/{path}/doSeckill")
//    @ResponseBody
//    public RespBean doSecKill(@PathVariable String path, Model model, User user, Long goodsId) {
//        System.out.println("-----------开始秒杀-6.0-----------");
//        if (user == null) {
//            //用户没有登录
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//
//        //增加逻辑 校验用户携带的路径是否正确
//        Boolean b = orderService.checkPath(user, goodsId, path);
//        if (!b){
//                //失败
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }
//
//
//        //获取到goodsvo  作为秒杀方法的参数  为了获取goodsvo  需要装配service
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //判断库存  是否可以进行秒杀  也走了数据库
//        if (goodsVo.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);//错误页面
//        }
//
//
//        //在redis获取对应的秒杀订单
//        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue()
//                .get("order:" + user.getId() + ":" + goodsVo.getId());
//        if (null != o) {
//            //复购
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//        //对map进行判断 内存标记
//        if (entryStockMap.get(goodsId)){
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//        }
//
//
//        //库存预减
//        //当商品没有了 直接返回 减少去执行orderService.seckill的请求 防止线程堆积
//        //decrement具有原子性
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
//        if (decrement < 0){
//
//            //秒杀的商品  没有库存了
//            entryStockMap.put(goodsId,true);
//
//            //恢复库存为0
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
//            //没有库存
//
//            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
//
//        }
//
//        //抢购 向消息队列发送秒杀请求 一旦发送消息 立马快速返回结果
//
//        //    构建一个对象seckillMessage
//
//        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//        mqSenderMessage.senderSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
//        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
//
//    }


//方法 获取秒杀路径
    @RequestMapping("/path")
    @ResponseBody
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    public RespBean getSeckillPath(User user, Long goodsId,String captcha,HttpServletRequest request) {
        if (user == null || goodsId < 0 || ! StringUtils.hasText(captcha)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }



        //增加 校验用户输入的验证码是否正确
        Boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        String path = orderService.createPath(user, goodsId);

        return RespBean.success(path);
    }

    //生成验证码的方法

    @RequestMapping("/captcha")
    public void happyCaptcha(HttpServletRequest request,
                             HttpServletResponse response,
                             User user, Long goodsId) {

        //生成验证码并返回
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM) //设置展现样式为动画
                .type(CaptchaType.NUMBER) //设置验证码内容为数字
                .length(6) //设置字符长度为 6
                .width(220) //设置动画宽度为 220
                .height(80) //设置动画高度为 80
                .font(Fonts.getInstance().zhFont()) //设置汉字的字体
                .build().finish(); //生成并输出验证码
        //默认把验证码的值保存在session中  key为happy-captcha

        //把验证码的值 保存到redis中   考虑分布式问题
        //redis中  key：captcha:userId:goodsId
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,
                (String)request.getSession().getAttribute("happy-captcha"),60, TimeUnit.SECONDS);




    }


    //该方法在所有属性初始化后 自动运行
    //将所有秒杀商品的库存量 加载到redis中
    @Override
    public void afterPropertiesSet() throws Exception {
        //查询所有的商品
        //判断是否为空
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        //遍历list 将商品的库存量 放到redis中
        //商品库存量对应的key ：seckillGoods:商品id
        list.forEach(goodsVo -> {

            //初始化map

            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            entryStockMap.put(goodsVo.getId(),false);
        });


    }
}
