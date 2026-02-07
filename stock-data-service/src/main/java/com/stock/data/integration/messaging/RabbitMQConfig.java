package com.stock.data.integration.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 用于配置股票数据实时推送的消息队列
 */
@Configuration
public class RabbitMQConfig {

    // 股票行情队列
    public static final String STOCK_QUOTE_QUEUE = "stock.quote.queue";

    // 股票行情交换机
    public static final String STOCK_QUOTE_EXCHANGE = "stock.quote.exchange";

    // 股票行情路由键
    public static final String STOCK_QUOTE_ROUTING_KEY = "stock.quote";

    // 股票实时数据队列
    public static final String STOCK_REALTIME_QUEUE = "stock.realtime.queue";

    // 股票实时数据交换机
    public static final String STOCK_REALTIME_EXCHANGE = "stock.realtime.exchange";

    // 股票实时数据路由键
    public static final String STOCK_REALTIME_ROUTING_KEY = "stock.realtime";

    // 死信队列
    public static final String STOCK_DEAD_QUEUE = "stock.dead.queue";

    // 死信交换机
    public static final String STOCK_DEAD_EXCHANGE = "stock.dead.exchange";

    // 死信路由键
    public static final String STOCK_DEAD_ROUTING_KEY = "stock.dead";

    /**
     * JSON消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        // 开启发送端确认
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 股票行情队列
     */
    @Bean
    public Queue stockQuoteQueue() {
        return QueueBuilder.durable(STOCK_QUOTE_QUEUE)
                .withArgument("x-dead-letter-exchange", STOCK_DEAD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", STOCK_DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 股票行情交换机（Fanout类型，广播到所有队列）
     */
    @Bean
    public FanoutExchange stockQuoteExchange() {
        return new FanoutExchange(STOCK_QUOTE_EXCHANGE, true, false);
    }

    /**
     * 绑定股票行情队列到交换机
     */
    @Bean
    public Binding stockQuoteBinding() {
        return BindingBuilder.bind(stockQuoteQueue())
                .to(stockQuoteExchange());
    }

    /**
     * 股票实时数据队列
     */
    @Bean
    public Queue stockRealtimeQueue() {
        return QueueBuilder.durable(STOCK_REALTIME_QUEUE)
                .withArgument("x-dead-letter-exchange", STOCK_DEAD_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", STOCK_DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 股票实时数据交换机（Topic类型，支持路由模式）
     */
    @Bean
    public TopicExchange stockRealtimeExchange() {
        return new TopicExchange(STOCK_REALTIME_EXCHANGE, true, false);
    }

    /**
     * 绑定股票实时数据队列到交换机
     */
    @Bean
    public Binding stockRealtimeBinding() {
        return BindingBuilder.bind(stockRealtimeQueue())
                .to(stockRealtimeExchange())
                .with(STOCK_REALTIME_ROUTING_KEY);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue stockDeadQueue() {
        return QueueBuilder.durable(STOCK_DEAD_QUEUE).build();
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange stockDeadExchange() {
        return new DirectExchange(STOCK_DEAD_EXCHANGE, true, false);
    }

    /**
     * 绑定死信队列到死信交换机
     */
    @Bean
    public Binding stockDeadBinding() {
        return BindingBuilder.bind(stockDeadQueue())
                .to(stockDeadExchange())
                .with(STOCK_DEAD_ROUTING_KEY);
    }
}