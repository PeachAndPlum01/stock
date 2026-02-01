package com.stock.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 股票数据微服务启动类
 * 负责实时获取和推送股票数据
 */
@SpringBootApplication
@EnableScheduling
public class StockDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDataServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("股票数据微服务启动成功！");
        System.out.println("服务端口: 8082");
        System.out.println("功能: 实时获取股票数据并通过消息队列推送");
        System.out.println("========================================");
    }
}
