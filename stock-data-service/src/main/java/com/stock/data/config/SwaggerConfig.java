package com.stock.data.config;

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
        return OpenApiConfigBuilder.build("股票数据服务API", "提供股票数据查询、公司信息等功能");
    }
}
