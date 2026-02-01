package com.stock.correlation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 天问 - 省份关联服务
 * 
 * @author stock-system
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.stock.correlation.mapper")
public class CorrelationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorrelationServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("天问 - 省份关联服务启动成功！");
        System.out.println("服务地址: http://localhost:8084");
        System.out.println("========================================");
    }
}
