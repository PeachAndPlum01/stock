package com.stock.data.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Tushare API配置类
 */
@Configuration
@ConfigurationProperties(prefix = "tushare")
public class TushareProperties {

    /**
     * Tushare API Token
     * 需要在 https://tushare.pro 注册后获取
     */
    private String token;

    /**
     * Tushare API地址
     */
    private String apiUrl = "https://api.tushare.pro";

    /**
     * API调用超时时间（秒）
     */
    private Integer timeout = 30;

    /**
     * 实时数据更新间隔（秒）
     */
    private Integer realTimeInterval = 10;

    /**
     * 批量数据更新间隔（秒）
     */
    private Integer batchUpdateInterval = 60;

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRealTimeInterval() {
        return realTimeInterval;
    }

    public void setRealTimeInterval(Integer realTimeInterval) {
        this.realTimeInterval = realTimeInterval;
    }

    public Integer getBatchUpdateInterval() {
        return batchUpdateInterval;
    }

    public void setBatchUpdateInterval(Integer batchUpdateInterval) {
        this.batchUpdateInterval = batchUpdateInterval;
    }
}