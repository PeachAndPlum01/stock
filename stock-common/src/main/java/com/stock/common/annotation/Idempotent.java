package com.stock.common.annotation;

import java.lang.annotation.*;

/**
 * 幂等性注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    
    /**
     * 幂等性key的前缀
     */
    String prefix() default "idempotent";
    
    /**
     * 过期时间（秒）
     */
    int expireTime() default 5;
    
    /**
     * 提示信息
     */
    String message() default "请勿重复提交";
}
