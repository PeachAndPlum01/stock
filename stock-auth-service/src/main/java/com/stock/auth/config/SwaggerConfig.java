package com.stock.auth.config;

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
        return OpenApiConfigBuilder.build("认证服务API", "提供用户登录、注册、JWT Token管理等功能");
    }
}
