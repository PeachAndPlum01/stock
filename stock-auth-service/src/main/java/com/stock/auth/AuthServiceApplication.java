package com.stock.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 天问 - 认证服务
 * 
 * @author stock-system
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.stock.auth.mapper")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("天问 - 认证服务启动成功！");
        System.out.println("服务地址: http://localhost:8081");
        System.out.println("========================================");
    }
}
