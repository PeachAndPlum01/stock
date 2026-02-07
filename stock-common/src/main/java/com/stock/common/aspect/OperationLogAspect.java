package com.stock.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.common.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Pointcut("@annotation(com.stock.common.annotation.OperationLog)")
    public void operationLogPointcut() {
    }
    
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String module = operationLog.module();
        String operation = operationLog.operation();
        String uri = request != null ? request.getRequestURI() : "";
        String method_name = request != null ? request.getMethod() : "";
        
        // 记录请求参数
        if (operationLog.saveParams()) {
            Object[] args = joinPoint.getArgs();
            try {
                String params = objectMapper.writeValueAsString(args);
                log.info("操作日志 - 模块:{}, 操作:{}, URI:{}, Method:{}, 参数:{}", 
                        module, operation, uri, method_name, params);
            } catch (Exception e) {
                log.warn("参数序列化失败", e);
            }
        }
        
        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录响应结果
            if (operationLog.saveResult() && result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    log.info("操作日志 - 模块:{}, 操作:{}, 耗时:{}ms, 结果:{}", 
                            module, operation, duration, resultStr);
                } catch (Exception e) {
                    log.info("操作日志 - 模块:{}, 操作:{}, 耗时:{}ms", 
                            module, operation, duration);
                }
            } else {
                log.info("操作日志 - 模块:{}, 操作:{}, 耗时:{}ms", 
                        module, operation, duration);
            }
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.error("操作失败 - 模块:{}, 操作:{}, 耗时:{}ms, 异常:{}", 
                    module, operation, duration, e.getMessage());
            throw e;
        }
    }
}
