package com.wq.seckill.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
//消息的生产者 秒杀消息
public class MQSenderMessage {


    @Resource
    private RabbitTemplate rabbitTemplate;

    //发送秒杀消息
    public void senderSeckillMessage(String message){
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
