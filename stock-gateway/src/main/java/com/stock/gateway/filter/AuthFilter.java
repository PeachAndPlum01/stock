package com.stock.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 白名单路径（不需要认证）
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/captcha",
            "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println("🔍 AuthFilter - 请求路径: " + path);

        // 检查是否在白名单中
        if (isWhiteList(path)) {
            System.out.println("✅ AuthFilter - 白名单路径，直接放行: " + path);
            return chain.filter(exchange);
        }

        // 获取token
        String token = getToken(request);
        System.out.println("🔑 AuthFilter - Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        
        if (!StringUtils.hasText(token)) {
            System.out.println("❌ AuthFilter - Token为空，返回401");
            return unauthorized(exchange.getResponse());
        }

        // 验证token
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 将用户信息添加到请求头
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);

            System.out.println("✅ AuthFilter - Token验证成功，用户: " + username + " (ID: " + userId + ")");

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            System.out.println("❌ AuthFilter - Token验证失败: " + e.getMessage());
            e.printStackTrace();
            return unauthorized(exchange.getResponse());
        }
    }

    /**
     * 检查是否在白名单中
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 从请求头获取token
     */
    private String getToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
