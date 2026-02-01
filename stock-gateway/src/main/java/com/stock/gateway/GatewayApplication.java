package com.stock.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 股票投资系统 - API网关
 * 
 * @author stock-system
 * @version 1.0.0
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("股票投资系统 - API网关启动成功！");
        System.out.println("网关地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
