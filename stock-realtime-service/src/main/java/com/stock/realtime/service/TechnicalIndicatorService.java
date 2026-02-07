package com.stock.realtime.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.stock.realtime.entity.TechnicalIndicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * 技术指标计算服务
 * 使用TA4J库计算各种技术指标
 */
@Slf4j
@Service
public class TechnicalIndicatorService {

    @Autowired
    @Qualifier("technicalIndicatorCache")
    private Cache<String, Object> indicatorCache;

    /**
     * 计算所有技术指标
     */
    @Async("indicatorCalculateExecutor")
    public CompletableFuture<TechnicalIndicator> calculateIndicators(String stockCode, BarSeries series) {
        try {
            // 从缓存获取
            TechnicalIndicator cached = (TechnicalIndicator) indicatorCache.getIfPresent(stockCode);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }

            // 计算各项指标
            TechnicalIndicator.MA ma = calculateMA(series);
            TechnicalIndicator.MACD macd = calculateMACD(series);
            TechnicalIndicator.KDJ kdj = calculateKDJ(series);
            TechnicalIndicator.RSI rsi = calculateRSI(series);
            TechnicalIndicator.BOLL boll = calculateBOLL(series);

            TechnicalIndicator indicator = TechnicalIndicator.builder()
                    .stockCode(stockCode)
                    .ma(ma)
                    .macd(macd)
                    .kdj(kdj)
                    .rsi(rsi)
                    .boll(boll)
                    .build();

            // 缓存结果
            indicatorCache.put(stockCode, indicator);

            return CompletableFuture.completedFuture(indicator);
        } catch (Exception e) {
            log.error("计算技术指标失败: {}", stockCode, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 计算移动平均线
     */
    private TechnicalIndicator.MA calculateMA(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        
        return TechnicalIndicator.MA.builder()
                .ma5(getIndicatorValue(new SMAIndicator(closePrice, 5)))
                .ma10(getIndicatorValue(new SMAIndicator(closePrice, 10)))
                .ma20(getIndicatorValue(new SMAIndicator(closePrice, 20)))
                .ma30(getIndicatorValue(new SMAIndicator(closePrice, 30)))
                .ma60(getIndicatorValue(new SMAIndicator(closePrice, 60)))
                .ma120(getIndicatorValue(new SMAIndicator(closePrice, 120)))
                .ma250(getIndicatorValue(new SMAIndicator(closePrice, 250)))
                .build();
    }

    /**
     * 计算MACD指标
     */
    private TechnicalIndicator.MACD calculateMACD(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        MACDIndicator macdIndicator = new MACDIndicator(closePrice, 12, 26);
        EMAIndicator emaIndicator = new EMAIndicator(macdIndicator, 9);
        
        Num dif = macdIndicator.getValue(series.getEndIndex());
        Num dea = emaIndicator.getValue(series.getEndIndex());
        Num macd = dif.minus(dea).multipliedBy(series.numOf(2));
        
        String signal = "持平";
        if (dif.isGreaterThan(dea)) {
            signal = "金叉";
        } else if (dif.isLessThan(dea)) {
            signal = "死叉";
        }
        
        return TechnicalIndicator.MACD.builder()
                .dif(new BigDecimal(dif.toString()))
                .dea(new BigDecimal(dea.toString()))
                .macd(new BigDecimal(macd.toString()))
                .signal(signal)
                .build();
    }

    /**
     * 计算KDJ指标
     */
    private TechnicalIndicator.KDJ calculateKDJ(BarSeries series) {
        StochasticOscillatorKIndicator kIndicator = new StochasticOscillatorKIndicator(series, 9);
        StochasticOscillatorDIndicator dIndicator = new StochasticOscillatorDIndicator(kIndicator);
        
        Num k = kIndicator.getValue(series.getEndIndex());
        Num d = dIndicator.getValue(series.getEndIndex());
        Num j = k.multipliedBy(series.numOf(3)).minus(d.multipliedBy(series.numOf(2)));
        
        String signal = "正常";
        if (k.doubleValue() > 80) {
            signal = "超买";
        } else if (k.doubleValue() < 20) {
            signal = "超卖";
        }
        
        return TechnicalIndicator.KDJ.builder()
                .k(new BigDecimal(k.toString()))
                .d(new BigDecimal(d.toString()))
                .j(new BigDecimal(j.toString()))
                .signal(signal)
                .build();
    }

    /**
     * 计算RSI指标
     */
    private TechnicalIndicator.RSI calculateRSI(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsi6 = new RSIIndicator(closePrice, 6);
        RSIIndicator rsi12 = new RSIIndicator(closePrice, 12);
        RSIIndicator rsi24 = new RSIIndicator(closePrice, 24);
        
        double rsi6Value = rsi6.getValue(series.getEndIndex()).doubleValue();
        
        String signal = "正常";
        if (rsi6Value > 70) {
            signal = "超买";
        } else if (rsi6Value < 30) {
            signal = "超卖";
        }
        
        return TechnicalIndicator.RSI.builder()
                .rsi6(getIndicatorValue(rsi6))
                .rsi12(getIndicatorValue(rsi12))
                .rsi24(getIndicatorValue(rsi24))
                .signal(signal)
                .build();
    }

    /**
     * 计算布林带
     */
    private TechnicalIndicator.BOLL calculateBOLL(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(new SMAIndicator(closePrice, 20));
        StandardDeviationIndicator std = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsUpperIndicator upper = new BollingerBandsUpperIndicator(middle, std);
        BollingerBandsLowerIndicator lower = new BollingerBandsLowerIndicator(middle, std);
        
        Num currentPrice = closePrice.getValue(series.getEndIndex());
        Num upperValue = upper.getValue(series.getEndIndex());
        Num lowerValue = lower.getValue(series.getEndIndex());
        
        String signal = "正常";
        if (currentPrice.isGreaterThan(upperValue)) {
            signal = "突破上轨";
        } else if (currentPrice.isLessThan(lowerValue)) {
            signal = "跌破下轨";
        }
        
        return TechnicalIndicator.BOLL.builder()
                .upper(getIndicatorValue(upper))
                .middle(getIndicatorValue(middle))
                .lower(getIndicatorValue(lower))
                .signal(signal)
                .build();
    }

    /**
     * 获取指标值
     */
    private BigDecimal getIndicatorValue(Indicator<Num> indicator) {
        try {
            Num value = indicator.getValue(indicator.getBarSeries().getEndIndex());
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}