package com.stock.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Tushare API配置类
 */
@Data
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
}
