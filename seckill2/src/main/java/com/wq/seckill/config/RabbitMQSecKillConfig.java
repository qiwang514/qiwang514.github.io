package com.wq.seckill.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//秒杀 队列 和 交换机  配置类
@Configuration
public class RabbitMQSecKillConfig {
    //定义消息队列
    private static final String QUEUE = "seckillQueue";
    //定义交换机
    private static final String EXCHANGE = "seckillExchange";


    @Bean
    //创建队列
    public Queue queue_seckill(){
        return new Queue(QUEUE);
    }

    @Bean
    //创建交换机 Topic
    public TopicExchange topicExchange_seckill(){
        return new TopicExchange(EXCHANGE);
    }


    @Bean
    //将队列绑定到交换机 并指定路由
    public Binding binding_seckill(){
        return BindingBuilder.bind(queue_seckill()).to(topicExchange_seckill()).with("seckill.#");

    }

}
