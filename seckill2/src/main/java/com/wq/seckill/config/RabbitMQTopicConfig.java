package com.wq.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.ExchangeBuilder.topicExchange;

//rabbirMq的配置类 可以创建队列 交换机
@Configuration
public class RabbitMQTopicConfig {

    //定义队列名称
    private static final String QUEUE01 = "queue_topic01";
    private static final String QUEUE02 = "queue_topic02";
    //交换机
    private static final String EXCHANGE = "topicExchange";
    //路由
    private static final String ROUTING_KEY01 = "#.queue.#";
    private static final String ROUTING_KEY02 = "*.queue.#";

    @Bean
    //配置队列
    public Queue queue_topic01() {
        return new Queue(QUEUE01);

    }
    @Bean
    //配置队列
    public Queue queue_topic02() {
        return new Queue(QUEUE02);

    }

    @Bean
    public TopicExchange exchangeTopic() {
        return new TopicExchange(EXCHANGE);
    }

    //绑定
    @Bean
    public Binding bingding_topic01(){
        return BindingBuilder.bind(queue_topic01()).to(exchangeTopic()).with(ROUTING_KEY01);
    }
    @Bean
    public Binding bingding_topic02(){
        return BindingBuilder.bind(queue_topic02()).to(exchangeTopic()).with(ROUTING_KEY02);
    }


}
