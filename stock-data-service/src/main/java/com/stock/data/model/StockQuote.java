package com.stock.data.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 股票行情数据模型
 */
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

    // Constructors
    public StockQuote() {
    }

    public StockQuote(String tsCode, String name, String tradeDate, BigDecimal open, BigDecimal high,
                      BigDecimal low, BigDecimal close, BigDecimal preClose, BigDecimal change,
                      BigDecimal pctChg, BigDecimal vol, BigDecimal amount, LocalDateTime updateTime, String source) {
        this.tsCode = tsCode;
        this.name = name;
        this.tradeDate = tradeDate;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.preClose = preClose;
        this.change = change;
        this.pctChg = pctChg;
        this.vol = vol;
        this.amount = amount;
        this.updateTime = updateTime;
        this.source = source;
    }

    // Builder pattern
    public static StockQuoteBuilder builder() {
        return new StockQuoteBuilder();
    }

    // Getters and Setters
    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getPreClose() {
        return preClose;
    }

    public void setPreClose(BigDecimal preClose) {
        this.preClose = preClose;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public BigDecimal getPctChg() {
        return pctChg;
    }

    public void setPctChg(BigDecimal pctChg) {
        this.pctChg = pctChg;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Builder class
    public static class StockQuoteBuilder {
        private String tsCode;
        private String name;
        private String tradeDate;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private BigDecimal preClose;
        private BigDecimal change;
        private BigDecimal pctChg;
        private BigDecimal vol;
        private BigDecimal amount;
        private LocalDateTime updateTime;
        private String source = "tushare";

        public StockQuoteBuilder tsCode(String tsCode) {
            this.tsCode = tsCode;
            return this;
        }

        public StockQuoteBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StockQuoteBuilder tradeDate(String tradeDate) {
            this.tradeDate = tradeDate;
            return this;
        }

        public StockQuoteBuilder open(BigDecimal open) {
            this.open = open;
            return this;
        }

        public StockQuoteBuilder high(BigDecimal high) {
            this.high = high;
            return this;
        }

        public StockQuoteBuilder low(BigDecimal low) {
            this.low = low;
            return this;
        }

        public StockQuoteBuilder close(BigDecimal close) {
            this.close = close;
            return this;
        }

        public StockQuoteBuilder preClose(BigDecimal preClose) {
            this.preClose = preClose;
            return this;
        }

        public StockQuoteBuilder change(BigDecimal change) {
            this.change = change;
            return this;
        }

        public StockQuoteBuilder pctChg(BigDecimal pctChg) {
            this.pctChg = pctChg;
            return this;
        }

        public StockQuoteBuilder vol(BigDecimal vol) {
            this.vol = vol;
            return this;
        }

        public StockQuoteBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public StockQuoteBuilder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public StockQuoteBuilder source(String source) {
            this.source = source;
            return this;
        }

        public StockQuote build() {
            return new StockQuote(tsCode, name, tradeDate, open, high, low, close, preClose,
                    change, pctChg, vol, amount, updateTime, source);
        }
    }
}
