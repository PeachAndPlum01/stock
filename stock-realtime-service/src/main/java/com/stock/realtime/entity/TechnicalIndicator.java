package com.stock.realtime.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 技术指标数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalIndicator {
    
    /**
     * 股票代码
     */
    private String stockCode;
    
    /**
     * 移动平均线
     */
    private MA ma;
    
    /**
     * MACD指标
     */
    private MACD macd;
    
    /**
     * KDJ指标
     */
    private KDJ kdj;
    
    /**
     * RSI指标
     */
    private RSI rsi;
    
    /**
     * 布林带
     */
    private BOLL boll;
    
    /**
     * 移动平均线
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MA {
        private BigDecimal ma5;   // 5日均线
        private BigDecimal ma10;  // 10日均线
        private BigDecimal ma20;  // 20日均线
        private BigDecimal ma30;  // 30日均线
        private BigDecimal ma60;  // 60日均线
        private BigDecimal ma120; // 120日均线
        private BigDecimal ma250; // 250日均线
    }
    
    /**
     * MACD指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MACD {
        private BigDecimal dif;    // 差离值
        private BigDecimal dea;    // 讯号线
        private BigDecimal macd;   // MACD柱
        private String signal;     // 信号：金叉/死叉/持平
    }
    
    /**
     * KDJ指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KDJ {
        private BigDecimal k;      // K值
        private BigDecimal d;      // D值
        private BigDecimal j;      // J值
        private String signal;     // 信号：超买/超卖/正常
    }
    
    /**
     * RSI指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RSI {
        private BigDecimal rsi6;   // 6日RSI
        private BigDecimal rsi12;  // 12日RSI
        private BigDecimal rsi24;  // 24日RSI
        private String signal;     // 信号：超买/超卖/正常
    }
    
    /**
     * 布林带
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BOLL {
        private BigDecimal upper;  // 上轨
        private BigDecimal middle; // 中轨
        private BigDecimal lower;  // 下轨
        private String signal;     // 信号：突破上轨/跌破下轨/正常
    }
}