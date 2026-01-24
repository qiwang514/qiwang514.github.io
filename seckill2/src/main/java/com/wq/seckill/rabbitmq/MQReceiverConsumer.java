package com.wq.seckill.rabbitmq;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.wq.seckill.pojo.SeckillMessage;
import com.wq.seckill.pojo.User;
import com.wq.seckill.service.GoodsService;
import com.wq.seckill.service.OrderService;
import com.wq.seckill.service.UserService;
import com.wq.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//消息接收者
@Service
@Slf4j
public class MQReceiverConsumer {

    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private OrderService orderService;


    //下单操作
    @RabbitListener(queues = "seckillQueue")
    public void queue(String message) {
        log.info("接收到的消息：" + message);
        //从队列中取出的是一个String  但是我们需要的是MQSenderMessage  需要工具类

        SeckillMessage seckillMessage
                = JSONUtil.toBean(message, SeckillMessage.class);

        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        //通过id  得到goodsvo
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //下单操作
        orderService.seckill(user, goodsVo);

    }
}
