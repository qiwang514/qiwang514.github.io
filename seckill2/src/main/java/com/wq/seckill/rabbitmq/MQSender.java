package com.wq.seckill.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
//消息的发送者 生产者
@Service
public class MQSender {
    //装配rabbitTemplate 操作rabbitmq对象
    @Resource
    private RabbitTemplate rabbitTemplate;


    //发生消息的方法
    public void send(Object msg){
        log.info("发送的消息-->"+msg);
        rabbitTemplate.convertAndSend("queue",msg);
    }

    //广播模式
    //编写方法 发送消息 到 交换机 fanout
    public void sendFanout(Object msg){
        log.info("发送的消息fanout-->"+msg);
        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
    }

    //路由模式
    //发送 到交换机 并且 指定路由
    public void sendDirect1(Object msg){
        log.info("发送的消息Direct-->"+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
    }
    public void sendDirect2(Object msg){
        log.info("发送的消息Direct-->"+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
    }


    //topic模式
    public void sendTopic1(Object msg){
        log.info("发送的消息topic1-->"+msg);
        rabbitTemplate.convertAndSend("topicExchange","queue.red.message",msg);
    }
    public void sendTopic2(Object msg){
        log.info("发送的消息topic2-->"+msg);
        rabbitTemplate.convertAndSend("topicExchange","green.queue.green.message",msg);
    }


    //headers模式
    public void sendHeader1(String msg){
        log.info("发送的消息Header1-->"+msg);
        //创建消息属性
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "fast");
//        创建message对象  包含了发送的消息  和 属性
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange","",message);
    }

    public void sendHeader2(String msg){
        log.info("发送的消息Header2-->"+msg);
        //创建消息属性
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "normal");
//        创建message对象  包含了发送的消息  和 属性
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange","",message);
    }

}
