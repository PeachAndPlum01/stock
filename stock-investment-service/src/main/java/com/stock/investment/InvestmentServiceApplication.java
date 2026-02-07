package com.stock.investment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 天问 - 投资信息服务
 * 
 * @author stock-system
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.stock.investment.repository")
public class InvestmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("天问 - 投资信息服务启动成功！");
        System.out.println("服务地址: http://localhost:8083");
        System.out.println("========================================");
    }
}