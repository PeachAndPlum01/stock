package com.stock.realtime.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时行情数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeQuote {
    
    /**
     * 股票代码
     */
    private String stockCode;
    
    /**
     * 股票名称
     */
    private String stockName;
    
    /**
     * 当前价格
     */
    private BigDecimal currentPrice;
    
    /**
     * 涨跌额
     */
    private BigDecimal change;
    
    /**
     * 涨跌幅(%)
     */
    private BigDecimal changePercent;
    
    /**
     * 今日开盘价
     */
    private BigDecimal openPrice;
    
    /**
     * 今日最高价
     */
    private BigDecimal highPrice;
    
    /**
     * 今日最低价
     */
    private BigDecimal lowPrice;
    
    /**
     * 昨日收盘价
     */
    private BigDecimal preClosePrice;
    
    /**
     * 成交量(手)
     */
    private Long volume;
    
    /**
     * 成交额(元)
     */
    private BigDecimal amount;
    
    /**
     * 换手率(%)
     */
    private BigDecimal turnoverRate;
    
    /**
     * 市盈率
     */
    private BigDecimal pe;
    
    /**
     * 市净率
     */
    private BigDecimal pb;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 买一价
     */
    private BigDecimal bid1;
    
    /**
     * 买一量
     */
    private Long bidVolume1;
    
    /**
     * 卖一价
     */
    private BigDecimal ask1;
    
    /**
     * 卖一量
     */
    private Long askVolume1;
}