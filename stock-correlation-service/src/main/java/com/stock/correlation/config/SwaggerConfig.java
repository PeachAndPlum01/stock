package com.stock.correlation.config;

import com.stock.common.config.OpenApiConfigBuilder;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置类
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建OpenAPI文档
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return OpenApiConfigBuilder.build("股票关联服务API", "提供股票相关性分析、行业分类等功能");
    }
}
