package com.stock.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Boot Admin 监控服务
 * 提供微服务监控管理界面
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
        System.out.println("========================================");
        System.out.println("Spring Boot Admin Server 启动成功！");
        System.out.println("访问地址: http://localhost:8090");
        System.out.println("========================================");
    }
}
