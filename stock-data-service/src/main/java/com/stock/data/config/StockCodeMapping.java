package com.stock.data.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 股票代码映射配置类
 */
@Configuration
@ConfigurationProperties(prefix = "stock-codes")
public class StockCodeMapping {

    private List<StockMapping> mappings = new ArrayList<>();

    public static class StockMapping {
        private String company;
        private String code;

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public List<StockMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<StockMapping> mappings) {
        this.mappings = mappings;
    }

    /**
     * 获取公司名称到股票代码的映射
     */
    public Map<String, String> getCompanyToCodeMap() {
        Map<String, String> map = new HashMap<>();
        for (StockMapping mapping : mappings) {
            map.put(mapping.getCompany(), mapping.getCode());
        }
        return map;
    }

    /**
     * 获取股票代码到公司名称的映射
     */
    public Map<String, String> getCodeToCompanyMap() {
        Map<String, String> map = new HashMap<>();
        for (StockMapping mapping : mappings) {
            map.put(mapping.getCode(), mapping.getCompany());
        }
        return map;
    }
}
