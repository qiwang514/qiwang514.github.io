package com.wq.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//rabbirMq的配置类 可以创建队列 交换机
@Configuration
public class RabbitMQConfig {

    //普通模式
    //定义队列名
    public static final String QUEUE = "queue";

    //fanout 广播模式
    public static final String QUEUE1 = "queue_fanout01";
    public static final String QUEUE2 = "queue_fanout02";
    //fnaout 交换机
    public static final String EXCHANGE = "fanoutExchange";



    //direct 路由模式
    public static final String QUEUE_DIRECT1 = "queue_direct01";
    public static final String QUEUE_DIRECT2 = "queue_direct02";
    //direct交换机
    public static final String EXCHANGE_DIRECT = "directExchange";
    //定义路由
    public static final String ROUTING_KEY01 = "queue.red";
    public static final String ROUTING_KEY02 = "queue.green";


//=================================================
    //普通模式
    //创建队列
    //配置队列 队列名QUEUE 队列值queue  true表示持久化
    @Bean
    public Queue queue() {
        return new Queue(QUEUE,true);
    }

//    ===================================================
    //广播模式
    //fanout  广播模式
    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1,true);
    }
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE2,true);

    }

    //创建交换机
    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange(EXCHANGE);
    }


    //交换机和队列进行绑定
    @Bean
    public Binding binding1(){
        return BindingBuilder.bind(queue1()).to(exchange());
    }
    @Bean
    public Binding binding2(){
        return BindingBuilder.bind(queue2()).to(exchange());
    }

//    =====================================================
    //路由模式
    //配置队列
    @Bean
    public Queue direct1() {
        return new Queue(QUEUE_DIRECT1,true);
    }
    @Bean
    public Queue direct2() {
        return new Queue(QUEUE_DIRECT2,true);
    }
    //创建交换机
    @Bean
    public DirectExchange exchange_direct(){
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    //绑定  把队列direct1()  绑定到  exchange_direct()交换机 同时声明了路由ROUTING_KEY01
    public Binding binding_direct1(){
        return BindingBuilder.bind(direct1()).to(exchange_direct()).with(ROUTING_KEY01);
    }

    @Bean
    //绑定
    public Binding binding_direct2(){
        return BindingBuilder.bind(direct2()).to(exchange_direct()).with(ROUTING_KEY02);
    }



}
