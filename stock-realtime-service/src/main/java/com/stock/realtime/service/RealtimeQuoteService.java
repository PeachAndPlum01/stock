package com.stock.realtime.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.stock.realtime.entity.RealtimeQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 实时行情推送服务
 */
@Slf4j
@Service
public class RealtimeQuoteService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier("stockPriceCache")
    private Cache<String, Object> stockPriceCache;

    private static final String REDIS_QUOTE_KEY = "stock:quote:";
    private static final Random random = new Random();

    /**
     * 推送实时行情到WebSocket
     */
    @Async("quotePushExecutor")
    public void pushQuote(RealtimeQuote quote) {
        try {
            // 1. 更新本地缓存
            stockPriceCache.put(quote.getStockCode(), quote);

            // 2. 更新Redis缓存
            String redisKey = REDIS_QUOTE_KEY + quote.getStockCode();
            redisTemplate.opsForValue().set(redisKey, quote, 60, TimeUnit.SECONDS);

            // 3. 推送到WebSocket订阅者
            messagingTemplate.convertAndSend("/topic/quote/" + quote.getStockCode(), quote);

            log.debug("推送行情: {} - {}", quote.getStockCode(), quote.getCurrentPrice());
        } catch (Exception e) {
            log.error("推送行情失败: {}", quote.getStockCode(), e);
        }
    }

    /**
     * 批量推送行情
     */
    @Async("quotePushExecutor")
    public void pushQuoteBatch(List<RealtimeQuote> quotes) {
        quotes.forEach(this::pushQuote);
    }

    /**
     * 获取实时行情（多级缓存）
     */
    public RealtimeQuote getQuote(String stockCode) {
        // 1. 先查本地缓存
        RealtimeQuote quote = (RealtimeQuote) stockPriceCache.getIfPresent(stockCode);
        if (quote != null) {
            log.debug("从本地缓存获取行情: {}", stockCode);
            return quote;
        }

        // 2. 再查Redis
        String redisKey = REDIS_QUOTE_KEY + stockCode;
        quote = (RealtimeQuote) redisTemplate.opsForValue().get(redisKey);
        if (quote != null) {
            log.debug("从Redis获取行情: {}", stockCode);
            // 回写本地缓存
            stockPriceCache.put(stockCode, quote);
            return quote;
        }

        // 3. 缓存未命中，返回null或从数据源获取
        log.debug("缓存未命中: {}", stockCode);
        return null;
    }

    /**
     * 模拟生成实时行情数据（用于测试）
     */
    public RealtimeQuote generateMockQuote(String stockCode, String stockName) {
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal change = new BigDecimal(random.nextDouble() * 10 - 5).setScale(2, RoundingMode.HALF_UP);
        BigDecimal currentPrice = basePrice.add(change);
        BigDecimal changePercent = change.divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return RealtimeQuote.builder()
                .stockCode(stockCode)
                .stockName(stockName)
                .currentPrice(currentPrice)
                .change(change)
                .changePercent(changePercent)
                .openPrice(basePrice)
                .highPrice(currentPrice.add(new BigDecimal("2")))
                .lowPrice(currentPrice.subtract(new BigDecimal("2")))
                .preClosePrice(basePrice)
                .volume(random.nextLong(1000000, 10000000))
                .amount(new BigDecimal(random.nextLong(100000000, 1000000000)))
                .turnoverRate(new BigDecimal(random.nextDouble() * 10).setScale(2, RoundingMode.HALF_UP))
                .pe(new BigDecimal(random.nextDouble() * 50).setScale(2, RoundingMode.HALF_UP))
                .pb(new BigDecimal(random.nextDouble() * 10).setScale(2, RoundingMode.HALF_UP))
                .bid1(currentPrice.subtract(new BigDecimal("0.01")))
                .bidVolume1(random.nextLong(1000, 10000))
                .ask1(currentPrice.add(new BigDecimal("0.01")))
                .askVolume1(random.nextLong(1000, 10000))
                .updateTime(LocalDateTime.now())
                .build();
    }
}