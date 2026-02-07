package com.stock.common.aspect;

import com.stock.common.annotation.Idempotent;
import com.stock.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性切面
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Pointcut("@annotation(com.stock.common.annotation.Idempotent)")
    public void idempotentPointcut() {
    }
    
    @Around("idempotentPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        
        // 生成幂等性key
        String key = generateKey(joinPoint, idempotent);
        
        // 尝试获取锁
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                key, "1", idempotent.expireTime(), TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(success)) {
            log.warn("重复提交: key={}", key);
            throw new BusinessException(400, idempotent.message());
        }
        
        try {
            // 执行方法
            return joinPoint.proceed();
        } catch (Exception e) {
            // 如果执行失败，删除key，允许重试
            redisTemplate.delete(key);
            throw e;
        }
    }
    
    /**
     * 生成幂等性key
     */
    private String generateKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String uri = request != null ? request.getRequestURI() : "";
        String userId = request != null ? request.getHeader("userId") : "anonymous";
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        // 生成key: prefix:userId:uri:methodName
        return String.format("%s:%s:%s:%s", 
                idempotent.prefix(), userId, uri, methodName);
    }
}
