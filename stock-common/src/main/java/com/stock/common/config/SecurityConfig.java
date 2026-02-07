package com.stock.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 安全配置类
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${security.gateway-auth.enabled:true}")
    private boolean gatewayAuthEnabled;

    @Value("${security.gateway-auth.exclude-paths:}")
    private String excludePaths;

    /**
     * 密码加密器（使用BCrypt）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!gatewayAuthEnabled) {
            return;
        }
        List<String> excludes = resolveExcludePaths();
        registry.addInterceptor(new GatewayAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludes);
    }

    private List<String> resolveExcludePaths() {
        if (!StringUtils.hasText(excludePaths)) {
            return Collections.emptyList();
        }
        return Arrays.stream(StringUtils.commaDelimitedListToStringArray(excludePaths))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private static class GatewayAuthInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String userId = request.getHeader("X-User-Id");
            if (StringUtils.hasText(userId)) {
                return true;
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }
}
