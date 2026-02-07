package com.stock.realtime.controller;

import com.stock.realtime.entity.RealtimeQuote;
import com.stock.realtime.entity.TechnicalIndicator;
import com.stock.realtime.service.RealtimeQuoteService;
import com.stock.realtime.service.TechnicalIndicatorService;
import com.stock.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 实时行情API控制器
 */
@RestController
@RequestMapping("/api/realtime")
public class RealtimeController {

    @Autowired
    private RealtimeQuoteService quoteService;

    @Autowired
    private TechnicalIndicatorService indicatorService;

    /**
     * 获取实时行情
     */
    @GetMapping("/quote/{stockCode}")
    public Result<RealtimeQuote> getQuote(@PathVariable String stockCode) {
        RealtimeQuote quote = quoteService.getQuote(stockCode);
        if (quote == null) {
            // 如果缓存中没有，生成模拟数据
            quote = quoteService.generateMockQuote(stockCode, "测试股票");
            quoteService.pushQuote(quote);
        }
        return Result.success(quote);
    }

    /**
     * 手动推送行情（测试用）
     */
    @PostMapping("/quote/push")
    public Result<String> pushQuote(@RequestBody RealtimeQuote quote) {
        quoteService.pushQuote(quote);
        return Result.success("推送成功");
    }

    /**
     * 生成模拟行情（测试用）
     */
    @PostMapping("/quote/mock/{stockCode}")
    public Result<RealtimeQuote> generateMockQuote(
            @PathVariable String stockCode,
            @RequestParam(defaultValue = "测试股票") String stockName) {
        RealtimeQuote quote = quoteService.generateMockQuote(stockCode, stockName);
        quoteService.pushQuote(quote);
        return Result.success(quote);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "stock-realtime-service");
        return Result.success(result);
    }
}