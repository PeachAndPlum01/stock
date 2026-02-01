package com.stock.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 股票行情数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQuote {

    /**
     * 股票代码（Tushare格式：如 000001.SZ）
     */
    private String tsCode;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 开盘价
     */
    private BigDecimal open;

    /**
     * 最高价
     */
    private BigDecimal high;

    /**
     * 最低价
     */
    private BigDecimal low;

    /**
     * 收盘价
     */
    private BigDecimal close;

    /**
     * 昨收价
     */
    private BigDecimal preClose;

    /**
     * 涨跌额
     */
    private BigDecimal change;

    /**
     * 涨跌幅 (%)
     */
    private BigDecimal pctChg;

    /**
     * 成交量（手）
     */
    private BigDecimal vol;

    /**
     * 成交额（千元）
     */
    private BigDecimal amount;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 数据来源
     */
    private String source = "tushare";
}
