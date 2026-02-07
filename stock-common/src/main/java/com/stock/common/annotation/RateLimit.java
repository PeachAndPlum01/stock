package com.stock.common.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 每秒允许的请求数
     */
    int value() default 100;
    
    /**
     * 超时时间（毫秒）
     */
    int timeout() default 1000;
    
    /**
     * 限流key的前缀
     */
    String prefix() default "ratelimit";
    
    /**
     * 提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
}
