package com.stock.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String INVESTMENT_EXCHANGE = "investment.exchange";

    // 队列名称
    public static final String INVESTMENT_QUEUE = "investment.queue";

    // 路由键
    public static final String INVESTMENT_ROUTING_KEY = "investment.new";

    /**
     * 创建交换机
     */
    @Bean
    public DirectExchange investmentExchange() {
        return new DirectExchange(INVESTMENT_EXCHANGE, true, false);
    }

    /**
     * 创建队列
     */
    @Bean
    public Queue investmentQueue() {
        return new Queue(INVESTMENT_QUEUE, true);
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding investmentBinding() {
        return BindingBuilder
                .bind(investmentQueue())
                .to(investmentExchange())
                .with(INVESTMENT_ROUTING_KEY);
    }
}
