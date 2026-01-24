package com.wq.seckill.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
//消息接收者
public class MQReceiver {


    //=================================
    //普通模式
    @RabbitListener(queues = "queue")
    //接收消息
    public void receive(Object msg){
        log.info("接收到消息-->"+msg);
    }
//=================================
    //广播模式

    @RabbitListener(queues = "queue_fanout01")
    //接收消息fanout  监听队列
    public void receive1(Object msg){
        log.info("从queue_fanout01 队列接收到消息fanout-->"+msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    //接收消息fanout  监听队列
    public void receive2(Object msg){
        log.info("从queue_fanout02 队列接收到消息fanout-->"+msg);
    }

//=================================
    //路由模式
    @RabbitListener(queues = "queue_direct01")
    //接收消息  监听队列
    public void direct01(Object msg){
        log.info("从queue_direct01 队列接收到消息direct-->"+msg);
    }

    @RabbitListener(queues = "queue_direct02")
    //接收消息 监听队列
    public void direct02(Object msg){
        log.info("从queue_direct02 队列接收到消息direct-->"+msg);
    }


    //=================================
    //topic模式

    @RabbitListener(queues = "queue_topic01")
    //接收消息topic  监听队列
    public void topic01(Object msg){
        log.info("从queue_topic01 队列接收到消息topic-->"+msg);
    }

    @RabbitListener(queues = "queue_topic02")
    //接收消息topic  监听队列
    public void topic02(Object msg){
        log.info("从queue_topic02 队列接收到消息topic-->"+msg);
    }



    //=================================
    //header模式
    @RabbitListener(queues = "queue_header01")
    public void Header01(Message message){
        log.info("从queue_header01 队列接收到消息header-->"+message);
        log.info("从queue_header01 队列接收到消息header-->"+new String(message.getBody()));

    }

    @RabbitListener(queues = "queue_header02")
    public void Header02(Message message){
        log.info("从queue_header02 队列接收到消息header-->"+message);
        log.info("从queue_header02 队列接收到消息header-->"+new String(message.getBody()));
    }


}
