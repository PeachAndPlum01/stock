package com.stock.realtime.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxDB时序数据库配置
 * 用于存储K线数据和技术指标
 */
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url:http://localhost:8086}")
    private String url;

    @Value("${influxdb.token:my-super-secret-auth-token}")
    private String token;

    @Value("${influxdb.org:stock-org}")
    private String org;

    @Value("${influxdb.bucket:stock-data}")
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    public String getOrg() {
        return org;
    }

    public String getBucket() {
        return bucket;
    }
}
