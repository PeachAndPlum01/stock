package com.stock.realtime.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 多级缓存配置
 * L1: Caffeine本地缓存（快速访问）
 * L2: Redis分布式缓存（共享数据）
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Caffeine本地缓存配置
     * 用于缓存热点股票数据
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000) // 最大缓存10000条
                .expireAfterWrite(60, TimeUnit.SECONDS) // 写入后60秒过期
                .recordStats()); // 记录统计信息
        return cacheManager;
    }

    /**
     * 股票实时价格缓存
     * 更短的过期时间，保证数据实时性
     */
    @Bean
    public Cache<String, Object> stockPriceCache() {
        return Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(5, TimeUnit.SECONDS) // 5秒过期
                .recordStats()
                .build();
    }

    /**
     * 技术指标缓存
     * 较长的过期时间，减少计算压力
     */
    @Bean
    public Cache<String, Object> technicalIndicatorCache() {
        return Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(300, TimeUnit.SECONDS) // 5分钟过期
                .recordStats()
                .build();
    }
}
