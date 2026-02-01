package com.stock.investment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 股票投资系统 - 投资信息服务
 * 
 * @author stock-system
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.stock.investment.mapper")
public class InvestmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("股票投资系统 - 投资信息服务启动成功！");
        System.out.println("服务地址: http://localhost:8083");
        System.out.println("========================================");
    }
}
