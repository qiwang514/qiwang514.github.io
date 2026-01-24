package com.wq.seckill.controller;


import com.wq.seckill.rabbitmq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

//
@Controller
public class RabbitMQHandler {


    @Resource
    //装配MQSender
    private MQSender mqSender;


    @RequestMapping("/mq")
    @ResponseBody
    //方法 调用消息生产者 发送消息
    public void mq(){
        mqSender.send("hello wq");
    }

//===================================
    //广播模式
    @RequestMapping("/mq/fanout")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void fanout(){
        mqSender.sendFanout("hello wq fanout");
    }

    //===================================
    //路由模式
    @RequestMapping("/mq/direct01")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void direct01(){
        mqSender.sendDirect1("hello wq direct01");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void direct02(){
        mqSender.sendDirect2("hello wq direct02");
    }



    //===================================
    //topic模式
    @RequestMapping("/mq/topic01")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void topic01(){
        mqSender.sendTopic1("hello wq topic01");
    }

    @RequestMapping("/mq/topic02")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void topic02(){
        mqSender.sendTopic2("hello wq topic02");
    }


    //===================================
    //header模式
    @RequestMapping("/mq/header01")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void header01(){
        mqSender.sendHeader1("hello wq header01");
    }

    @RequestMapping("/mq/header02")
    @ResponseBody
    //方法 调用消息生产者 发送消息 fanout 到交换机
    public void header02(){
        mqSender.sendHeader2("hello wq header02");
    }

}
