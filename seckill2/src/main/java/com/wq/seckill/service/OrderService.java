package com.wq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wq.seckill.pojo.Order;
import com.wq.seckill.pojo.User;
import com.wq.seckill.vo.GoodsVo;

public interface OrderService extends IService<Order> {

    //秒杀方法 人和产品
    Order seckill(User user, GoodsVo goodsVo);

    //方法 生成秒杀路径的值（唯一）
    String createPath(User user, Long goodsId);

    //方法 对秒杀路径进行校验
    Boolean checkPath(User user, Long goodsId,String path);


    //方法 验证用户输入的验证码是否正确
    Boolean checkCaptcha(User user, Long goodsId,String captcha);
}
