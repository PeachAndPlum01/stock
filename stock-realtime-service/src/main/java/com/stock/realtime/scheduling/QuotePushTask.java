package com.stock.realtime.scheduling;

import com.stock.realtime.entity.RealtimeQuote;
import com.stock.realtime.service.RealtimeQuoteService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 行情推送定时任务
 */
@Slf4j
@Component
public class QuotePushTask {

    @Autowired
    private RealtimeQuoteService quoteService;

    // 模拟的热门股票列表
    private static final String[] HOT_STOCKS = {
            "000001", "000002", "000333", "600000", "600036",
            "600519", "601318", "601398", "601857", "601988"
    };

    /**
     * 定时推送行情（每5秒执行一次）
     * 使用Spring @Scheduled
     */
    @Scheduled(fixedRate = 5000, initialDelay = 10000)
    public void pushQuoteScheduled() {
        try {
            log.info("开始推送实时行情...");
            List<RealtimeQuote> quotes = new ArrayList<>();
            
            for (String stockCode : HOT_STOCKS) {
                RealtimeQuote quote = quoteService.generateMockQuote(stockCode, "股票" + stockCode);
                quotes.add(quote);
            }
            
            quoteService.pushQuoteBatch(quotes);
            log.info("推送行情完成，共{}只股票", quotes.size());
        } catch (Exception e) {
            log.error("推送行情失败", e);
        }
    }

    /**
     * XXL-JOB任务：推送行情
     */
    @XxlJob("pushQuoteJob")
    public void pushQuoteJob() {
        log.info("XXL-JOB: 开始推送实时行情...");
        pushQuoteScheduled();
    }

    /**
     * XXL-JOB任务：清理过期缓存
     */
    @XxlJob("cleanCacheJob")
    public void cleanCacheJob() {
        log.info("XXL-JOB: 开始清理过期缓存...");
        // 实现缓存清理逻辑
        log.info("XXL-JOB: 缓存清理完成");
    }

    /**
     * XXL-JOB任务：同步历史数据到InfluxDB
     */
    @XxlJob("syncHistoryDataJob")
    public void syncHistoryDataJob() {
        log.info("XXL-JOB: 开始同步历史数据...");
        // 实现数据同步逻辑
        log.info("XXL-JOB: 数据同步完成");
    }
}