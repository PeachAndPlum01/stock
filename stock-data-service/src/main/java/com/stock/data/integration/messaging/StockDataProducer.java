package com.stock.data.integration.messaging;

import com.stock.data.entity.StockQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * 股票数据消息生产者
 * 用于向消息队列发送实时股票数据
 */
@Service
public class StockDataProducer {

    private static final Logger log = LoggerFactory.getLogger(StockDataProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 设置发送确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送成功: correlationId={}", 
                        correlationData != null ? correlationData.getId() : "null");
            } else {
                log.error("消息发送失败: correlationId={}, cause={}", 
                        correlationData != null ? correlationData.getId() : "null", cause);
            }
        });

        // 设置返回确认回调
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息未路由到队列: exchange={}, routingKey={}, message={}", 
                    returned.getExchange(), returned.getRoutingKey(), returned.getMessage());
        });

        log.info("股票数据消息生产者初始化完成");
    }

    /**
     * 发送股票行情数据
     * 
     * @param stockQuote 股票行情数据
     */
    public void sendStockQuote(StockQuote stockQuote) {
        try {
            String correlationId = UUID.randomUUID().toString();
            CorrelationData correlationData = new CorrelationData(correlationId);

            Message message = rabbitTemplate.getMessageConverter().toMessage(stockQuote, null);
            if (message != null) {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setCorrelationId(correlationId);
            }

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.STOCK_QUOTE_EXCHANGE,
                    "",
                    stockQuote,
                    correlationData
            );

            log.debug("发送股票行情数据: tsCode={}, correlationId={}", 
                    stockQuote.getTsCode(), correlationId);

        } catch (Exception e) {
            log.error("发送股票行情数据失败: tsCode={}", stockQuote.getTsCode(), e);
        }
    }

    /**
     * 发送股票实时数据
     * 
     * @param stockQuote 股票行情数据
     */
    public void sendRealtimeData(StockQuote stockQuote) {
        try {
            String correlationId = UUID.randomUUID().toString();
            CorrelationData correlationData = new CorrelationData(correlationId);

            Message message = rabbitTemplate.getMessageConverter().toMessage(stockQuote, null);
            if (message != null) {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setCorrelationId(correlationId);
            }

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.STOCK_REALTIME_EXCHANGE,
                    RabbitMQConfig.STOCK_REALTIME_ROUTING_KEY,
                    stockQuote,
                    correlationData
            );

            log.debug("发送股票实时数据: tsCode={}, correlationId={}", 
                    stockQuote.getTsCode(), correlationId);

        } catch (Exception e) {
            log.error("发送股票实时数据失败: tsCode={}", stockQuote.getTsCode(), e);
        }
    }

    /**
     * 批量发送股票行情数据
     * 
     * @param stockQuotes 股票行情数据列表
     */
    public void sendStockQuotesBatch(java.util.List<StockQuote> stockQuotes) {
        if (stockQuotes == null || stockQuotes.isEmpty()) {
            return;
        }

        for (StockQuote stockQuote : stockQuotes) {
            sendStockQuote(stockQuote);
        }

        log.info("批量发送股票行情数据完成，数量: {}", stockQuotes.size());
    }

    /**
     * 向特定股票发送实时数据
     * 
     * @param tsCode 股票代码
     * @param stockQuote 股票行情数据
     */
    public void sendToStock(String tsCode, StockQuote stockQuote) {
        try {
            String routingKey = RabbitMQConfig.STOCK_REALTIME_ROUTING_KEY + "." + tsCode;
            String correlationId = UUID.randomUUID().toString();
            CorrelationData correlationData = new CorrelationData(correlationId);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.STOCK_REALTIME_EXCHANGE,
                    routingKey,
                    stockQuote,
                    correlationData
            );

            log.debug("发送特定股票实时数据: tsCode={}, routingKey={}, correlationId={}", 
                    tsCode, routingKey, correlationId);

        } catch (Exception e) {
            log.error("发送特定股票实时数据失败: tsCode={}", tsCode, e);
        }
    }
}