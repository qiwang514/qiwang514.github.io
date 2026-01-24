package com.wq.seckill.config;


//RabbitMQHeadersConfig 配置队列 交换机

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQHeadersConfig {
    //定义队列
    private static final String QUEUE01 = "queue_header01";
    private static final String QUEUE02 = "queue_header02";
    //定义交换机
    private static final String EXCHANGE = "headersExchange";

    //配置队列
    @Bean
    public Queue queue_header01() {
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue queue_header02() {
        return new Queue(QUEUE02);
    }
    //交换机配置
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(EXCHANGE);
    }


    @Bean
    //绑定
    public Binding binding_header01(){
        //先定义 声明k-v 因为可以有多个kv  所以将其声明放在map中
        Map<String, Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","low");
        return BindingBuilder.bind(queue_header01())
                .to(headersExchange()).whereAny(map).match();

    }
    @Bean
    //绑定
    public Binding binding_header02(){
        //先定义 声明k-v 因为可以有多个kv  所以将其声明放在map中
        Map<String, Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","fast");
        return BindingBuilder.bind(queue_header02())
                .to(headersExchange()).whereAll(map).match();

    }

}
