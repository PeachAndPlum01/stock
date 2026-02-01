package com.stock.data.service;

import com.stock.data.client.TushareApiClient;
import com.stock.data.config.StockCodeMapping;
import com.stock.data.config.TushareProperties;
import com.stock.data.model.StockQuote;
import com.stock.data.rabbitmq.StockDataProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 股票实时数据服务
 * 负责实时获取股票数据并推送到消息队列
 */
@Slf4j
@Service
public class StockRealtimeService {

    @Autowired
    private TushareApiClient tushareApiClient;

    @Autowired
    private TushareProperties tushareProperties;

    @Autowired
    private StockCodeMapping stockCodeMapping;

    @Autowired
    private StockDataProducer stockDataProducer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "stock:realtime:";
    private static final String CACHE_KEY_ALL = CACHE_PREFIX + "all";

    /**
     * 实时获取所有配置股票的最新数据
     * 定时执行（每10秒）
     */
    @Scheduled(fixedRate = 10000)
    public void fetchAllRealtimeData() {
        if (!isTradingTime()) {
            log.debug("当前非交易时间，跳过实时数据获取");
            return;
        }

        try {
            log.debug("开始获取实时股票数据...");

            Map<String, String> companyToCodeMap = stockCodeMapping.getCompanyToCodeMap();
            List<String> tsCodes = new ArrayList<>(companyToCodeMap.values());

            if (tsCodes.isEmpty()) {
                log.warn("没有配置股票代码");
                return;
            }

            // 批量获取股票行情数据
            Map<String, Map<String, Object>> quotesMap = tushareApiClient.getLatestQuotesBatch(tsCodes);

            if (quotesMap.isEmpty()) {
                log.warn("未获取到任何股票数据");
                return;
            }

            // 转换为StockQuote对象并发送到消息队列
            List<StockQuote> stockQuotes = new ArrayList<>();
            int successCount = 0;

            for (Map.Entry<String, Map<String, Object>> entry : quotesMap.entrySet()) {
                try {
                    StockQuote stockQuote = convertToStockQuote(entry.getKey(), entry.getValue());
                    if (stockQuote != null) {
                        stockQuotes.add(stockQuote);
                        
                        // 发送到消息队列
                        stockDataProducer.sendStockQuote(stockQuote);
                        stockDataProducer.sendRealtimeData(stockQuote);
                        
                        // 缓存到Redis
                        cacheStockQuote(stockQuote);
                        
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("处理股票数据失败: tsCode={}", entry.getKey(), e);
                }
            }

            // 批量缓存所有股票数据
            cacheAllStockQuotes(stockQuotes);

            log.info("实时股票数据获取完成: 成功 {}, 总数 {}", successCount, tsCodes.size());

        } catch (Exception e) {
            log.error("获取实时股票数据异常", e);
        }
    }

    /**
     * 批量更新股票数据（每60秒）
     * 用于更新较长周期的数据，如涨跌幅等
     */
    @Scheduled(fixedRate = 60000)
    public void batchUpdateStockData() {
        if (!isTradingTime()) {
            return;
        }

        try {
            log.debug("开始批量更新股票数据...");

            Map<String, String> companyToCodeMap = stockCodeMapping.getCompanyToCodeMap();

            for (Map.Entry<String, String> entry : companyToCodeMap.entrySet()) {
                String company = entry.getKey();
                String tsCode = entry.getValue();

                try {
                    // 计算N日涨跌幅
                    BigDecimal fiveDayChange = tushareApiClient.calculateNDayChange(tsCode, 5);
                    BigDecimal tenDayChange = tushareApiClient.calculateNDayChange(tsCode, 10);
                    BigDecimal twentyDayChange = tushareApiClient.calculateNDayChange(tsCode, 20);

                    // 更新缓存
                    String cacheKey = CACHE_PREFIX + tsCode;
                    Object cached = redisTemplate.opsForValue().get(cacheKey);
                    
                    if (cached instanceof StockQuote) {
                        StockQuote stockQuote = (StockQuote) cached;
                        // 这里可以扩展字段来存储涨跌幅数据
                        redisTemplate.opsForValue().set(cacheKey, stockQuote, 1, TimeUnit.HOURS);
                    }

                } catch (Exception e) {
                    log.error("更新股票数据失败: tsCode={}", tsCode, e);
                }
            }

        } catch (Exception e) {
            log.error("批量更新股票数据异常", e);
        }
    }

    /**
     * 手动触发数据更新
     * 
     * @return 更新结果
     */
    public String manualUpdate() {
        log.info("手动触发股票数据更新...");
        
        try {
            fetchAllRealtimeData();
            batchUpdateStockData();
            
            return "手动更新完成";
        } catch (Exception e) {
            log.error("手动更新失败", e);
            return "手动更新失败: " + e.getMessage();
        }
    }

    /**
     * 获取单只股票的实时数据
     * 
     * @param tsCode 股票代码
     * @return 股票行情数据
     */
    public StockQuote getRealtimeData(String tsCode) {
        // 先从缓存获取
        String cacheKey = CACHE_PREFIX + tsCode;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached instanceof StockQuote) {
            return (StockQuote) cached;
        }

        // 缓存未命中，从API获取
        Map<String, Object> quote = tushareApiClient.getLatestQuote(tsCode);
        if (quote != null) {
            StockQuote stockQuote = convertToStockQuote(tsCode, quote);
            if (stockQuote != null) {
                cacheStockQuote(stockQuote);
                return stockQuote;
            }
        }

        return null;
    }

    /**
     * 获取所有股票的实时数据
     * 
     * @return 股票行情数据列表
     */
    public List<StockQuote> getAllRealtimeData() {
        // 先从缓存获取
        Object cached = redisTemplate.opsForValue().get(CACHE_KEY_ALL);
        
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<StockQuote> list = (List<StockQuote>) cached;
            return list;
        }

        // 缓存未命中，从各个股票的缓存汇总
        List<StockQuote> allQuotes = new ArrayList<>();
        Map<String, String> codeToCompanyMap = stockCodeMapping.getCodeToCompanyMap();
        
        for (String tsCode : codeToCompanyMap.keySet()) {
            StockQuote quote = getRealtimeData(tsCode);
            if (quote != null) {
                allQuotes.add(quote);
            }
        }

        return allQuotes;
    }

    /**
     * 将Tushare返回的数据转换为StockQuote对象
     * 
     * @param tsCode 股票代码
     * @param rawData 原始数据
     * @return StockQuote对象
     */
    private StockQuote convertToStockQuote(String tsCode, Map<String, Object> rawData) {
        try {
            Map<String, String> codeToCompanyMap = stockCodeMapping.getCodeToCompanyMap();
            String name = codeToCompanyMap.getOrDefault(tsCode, tsCode);

            StockQuote quote = StockQuote.builder()
                    .tsCode(tsCode)
                    .name(name)
                    .tradeDate(getString(rawData, "trade_date"))
                    .open(getBigDecimal(rawData, "open"))
                    .high(getBigDecimal(rawData, "high"))
                    .low(getBigDecimal(rawData, "low"))
                    .close(getBigDecimal(rawData, "close"))
                    .preClose(getBigDecimal(rawData, "pre_close"))
                    .change(getBigDecimal(rawData, "change"))
                    .pctChg(getBigDecimal(rawData, "pct_chg"))
                    .vol(getBigDecimal(rawData, "vol"))
                    .amount(getBigDecimal(rawData, "amount"))
                    .updateTime(LocalDateTime.now())
                    .build();

            return quote;

        } catch (Exception e) {
            log.error("转换股票数据失败: tsCode={}", tsCode, e);
            return null;
        }
    }

    /**
     * 缓存单只股票数据
     */
    private void cacheStockQuote(StockQuote stockQuote) {
        String cacheKey = CACHE_PREFIX + stockQuote.getTsCode();
        redisTemplate.opsForValue().set(cacheKey, stockQuote, 1, TimeUnit.HOURS);
    }

    /**
     * 批量缓存所有股票数据
     */
    private void cacheAllStockQuotes(List<StockQuote> stockQuotes) {
        redisTemplate.opsForValue().set(CACHE_KEY_ALL, stockQuotes, 10, TimeUnit.MINUTES);
    }

    /**
     * 判断当前是否为交易时间
     */
    private boolean isTradingTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int dayOfWeek = now.getDayOfWeek().getValue();

        // 只在周一到周五执行
        if (dayOfWeek > 5) {
            return false;
        }

        // 上午交易时间：9:30-11:30
        if (hour == 9 && minute >= 30) {
            return true;
        }
        if (hour >= 10 && hour < 12) {
            return true;
        }
        if (hour == 12 && minute == 0) {
            return true;
        }

        // 下午交易时间：13:00-15:00
        if (hour >= 13 && hour < 15) {
            return true;
        }
        if (hour == 15 && minute == 0) {
            return true;
        }

        return false;
    }

    /**
     * 从Map中获取字符串值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 从Map中获取BigDecimal值
     */
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        if (value instanceof String) {
            return new BigDecimal((String) value);
        }
        return null;
    }
}
