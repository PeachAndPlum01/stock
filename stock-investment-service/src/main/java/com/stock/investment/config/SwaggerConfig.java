package com.stock.investment.config;

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
        return OpenApiConfigBuilder.build("投资组合服务API", "提供投资组合管理、交易记录等功能");
    }
}
