package com.stock.data.service;

import com.stock.data.integration.TushareApiClient;
import com.stock.data.properties.StockCodeMapping;
import com.stock.data.entity.StockCompany;
import com.stock.data.repository.StockCompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 股票公司信息服务
 * 负责获取、更新和查询公司信息数据
 */
@Service
public class StockCompanyService {

    private static final Logger log = LoggerFactory.getLogger(StockCompanyService.class);

    @Autowired
    private TushareApiClient tushareApiClient;

    @Autowired
    private StockCompanyRepository stockCompanyRepository;

    @Autowired
    private StockCodeMapping stockCodeMapping;

    /**
     * 每天晚上9点刷新所有公司信息数据
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 21 * * ?")
    public void refreshAllCompanyData() {
        log.info("开始刷新所有A股公司信息数据...");

        try {
            // 1. 先获取所有A股股票列表
            log.info("第一步：获取所有A股股票列表...");
            List<Map<String, Object>> allStocks = tushareApiClient.getAllStocks();

            if (allStocks == null || allStocks.isEmpty()) {
                log.warn("未获取到A股股票列表");
                return;
            }

            log.info("获取到 {} 只A股股票", allStocks.size());

            // 2. 提取所有股票代码
            List<String> tsCodes = new ArrayList<>();
            for (Map<String, Object> stock : allStocks) {
                String tsCode = (String) stock.get("ts_code");
                if (tsCode != null) {
                    tsCodes.add(tsCode);
                }
            }

            log.info("准备批量获取 {} 只股票的公司信息", tsCodes.size());

            // 3. 批量获取公司信息（每批1000个）
            int batchSize = 1000;
            List<Map<String, Object>> allCompanyData = tushareApiClient.getStockCompaniesBatchOptimized(tsCodes, batchSize);

            log.info("成功获取到 {} 条公司信息", allCompanyData.size());

            // 4. 保存或更新到数据库
            int successCount = 0;
            int updateCount = 0;
            int insertCount = 0;
            int skipCount = 0;
            int failCount = 0;

            for (Map<String, Object> companyData : allCompanyData) {
                String tsCode = null;
                try {
                    tsCode = (String) companyData.get("ts_code");
                    if (tsCode == null || tsCode.trim().isEmpty()) {
                        log.warn("跳过无效数据：ts_code为空");
                        skipCount++;
                        continue;
                    }

                    // 检查数据库中是否已存在
                    Optional<StockCompany> existing = stockCompanyRepository.findByTsCode(tsCode);
                    boolean isUpdate = existing.isPresent();

                    // 转换为StockCompany实体（即使部分字段为空也要保存）
                    StockCompany company = convertToStockCompany(tsCode, companyData);

                    if (company == null) {
                        log.error("转换失败，但仍尝试保存基础数据: tsCode={}", tsCode);
                        // 创建一个最基础的实体，至少保存ts_code
                        company = StockCompany.builder()
                                .tsCode(tsCode)
                                .dataSource("tushare")
                                .build();
                    }

                    // 保存或更新到数据库（每条记录独立事务）
                    boolean saved = saveOrUpdateCompany(company, tsCode);
                    
                    // 统计
                    if (saved) {
                        if (isUpdate) {
                            updateCount++;
                        } else {
                            insertCount++;
                        }
                        successCount++;
                    } else {
                        failCount++;
                    }

                } catch (Exception e) {
                    failCount++;
                    log.error("处理公司信息失败: tsCode={}, 错误: {}", tsCode, e.getMessage(), e);
                }
            }

            log.info("公司信息刷新完成: 获取 {}, 成功 {}, 更新 {}, 新增 {}, 跳过 {}, 失败 {}",
                    allCompanyData.size(), successCount, updateCount, insertCount, skipCount, failCount);

        } catch (Exception e) {
            log.error("刷新公司信息数据异常", e);
        }
    }

    /**
     * 保存或更新公司信息（独立事务）
     * 确保每条数据都能成功保存
     */
    @Transactional
    public boolean saveOrUpdateCompany(StockCompany company, String tsCode) {
        try {
            Optional<StockCompany> existing = stockCompanyRepository.findByTsCode(tsCode);
            
            if (existing.isPresent()) {
                // 更新已有记录
                StockCompany existingCompany = existing.get();
                updateCompanyFields(existingCompany, company);
                stockCompanyRepository.save(existingCompany);
                log.debug("更新公司信息成功: tsCode={}, comName={}", tsCode, company.getComName());
            } else {
                // 新增记录
                stockCompanyRepository.save(company);
                log.debug("新增公司信息成功: tsCode={}, comName={}", tsCode, company.getComName());
            }
            return true;
        } catch (Exception e) {
            log.error("保存公司信息失败: tsCode={}, comName={}, 错误: {}", 
                    tsCode, company.getComName(), e.getMessage(), e);
            return false; // 返回false表示失败，不抛出异常
        }
    }

    /**
     * 手动触发公司信息刷新
     *
     * @return 刷新结果
     */
    public String manualRefresh() {
        log.info("手动触发公司信息刷新...");

        try {
            refreshAllCompanyData();
            return "公司信息刷新完成";
        } catch (Exception e) {
            log.error("手动刷新失败", e);
            return "手动刷新失败: " + e.getMessage();
        }
    }

    /**
     * 根据股票代码获取公司信息
     *
     * @param tsCode 股票代码
     * @return 公司信息
     */
    public StockCompany getCompanyByTsCode(String tsCode) {
        try {
            // 先从数据库查询
            Optional<StockCompany> existing = stockCompanyRepository.findByTsCode(tsCode);

            if (existing.isPresent()) {
                log.debug("从数据库获取公司信息: {}", tsCode);
                return existing.get();
            }

            // 数据库中没有，从API获取并保存
            log.debug("从API获取公司信息: {}", tsCode);
            Map<String, Object> companyData = tushareApiClient.getStockCompany(tsCode);

            if (companyData == null) {
                log.warn("未获取到股票 {} 的公司信息", tsCode);
                return null;
            }

            StockCompany company = convertToStockCompany(tsCode, companyData);
            if (company != null) {
                stockCompanyRepository.save(company);
            }

            return company;

        } catch (Exception e) {
            log.error("获取公司信息失败: tsCode={}", tsCode, e);
            return null;
        }
    }

    /**
     * 根据公司名称模糊查询
     *
     * @param comName 公司名称
     * @return 公司信息列表
     */
    public List<StockCompany> getCompanyByName(String comName) {
        return stockCompanyRepository.findByComNameContaining(comName);
    }

    /**
     * 根据城市查询公司信息
     *
     * @param city 城市
     * @return 公司信息列表
     */
    public List<StockCompany> getCompanyByCity(String city) {
        return stockCompanyRepository.findByCity(city);
    }

    /**
     * 获取所有公司信息
     *
     * @return 公司信息列表
     */
    public List<StockCompany> getAllCompanies() {
        return stockCompanyRepository.findAll();
    }

    /**
     * 根据交易所查询公司信息
     *
     * @param exchange 交易所
     * @return 公司信息列表
     */
    public List<StockCompany> getCompanyByExchange(String exchange) {
        return stockCompanyRepository.findByExchange(exchange);
    }

    /**
     * 获取公司统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getCompanyStatistics() {
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("total", stockCompanyRepository.countTotal());
        statistics.put("updateTime", LocalDateTime.now());
        return statistics;
    }

    /**
     * 将Tushare返回的数据转换为StockCompany实体
     * 即使部分字段转换失败，也会保存基础数据
     *
     * @param tsCode 股票代码
     * @param rawData 原始数据
     * @return StockCompany实体（不会返回null）
     */
    private StockCompany convertToStockCompany(String tsCode, Map<String, Object> rawData) {
        try {
            // 调试：输出原始数据的所有字段名（仅输出第一条记录）
            if (log.isDebugEnabled() && rawData != null) {
                log.debug("股票 {} 的原始数据字段: {}", tsCode, rawData.keySet());
                log.debug("股票 {} 的com_name值: {}", tsCode, rawData.get("com_name"));
            }
            
            // 获取公司名称（允许为空）
            String comName = getString(rawData, "com_name");

            StockCompany.StockCompanyBuilder builder = StockCompany.builder()
                    .tsCode(tsCode)
                    .comName(comName)
                    .chairman(getString(rawData, "chairman"))
                    .manager(getString(rawData, "manager"))
                    .secretary(getString(rawData, "secretary"))
                    .regCapital(getBigDecimal(rawData, "reg_capital"))
                    .setupDate(getLocalDate(rawData, "setup_date"))
                    .province(getString(rawData, "province"))
                    .city(getString(rawData, "city"))
                    .office(getString(rawData, "office"))
                    .introduction(getString(rawData, "introduction"))
                    .website(getString(rawData, "website"))
                    .email(getString(rawData, "email"))
                    .businessScope(getString(rawData, "business_scope"))
                    .employees(getInteger(rawData, "employees"))
                    .mainBusiness(getString(rawData, "main_business"))
                    .exchange(getString(rawData, "exchange"))
                    .dataSource("tushare");

            return builder.build();

        } catch (Exception e) {
            log.error("转换公司信息时发生异常: tsCode={}, 将返回基础实体", tsCode, e);
            // 即使转换失败，也返回一个包含ts_code的基础实体
            return StockCompany.builder()
                    .tsCode(tsCode)
                    .dataSource("tushare")
                    .build();
        }
    }

    /**
     * 更新已有公司信息的字段
     *
     * @param existing 已有记录
     * @param newData 新数据
     */
    private void updateCompanyFields(StockCompany existing, StockCompany newData) {
        existing.setComName(newData.getComName());
        existing.setChairman(newData.getChairman());
        existing.setManager(newData.getManager());
        existing.setSecretary(newData.getSecretary());
        existing.setRegCapital(newData.getRegCapital());
        existing.setSetupDate(newData.getSetupDate());
        existing.setProvince(newData.getProvince());
        existing.setCity(newData.getCity());
        existing.setOffice(newData.getOffice());
        existing.setIntroduction(newData.getIntroduction());
        existing.setWebsite(newData.getWebsite());
        existing.setEmail(newData.getEmail());
        existing.setBusinessScope(newData.getBusinessScope());
        existing.setEmployees(newData.getEmployees());
        existing.setMainBusiness(newData.getMainBusiness());
        existing.setExchange(newData.getExchange());
        // updateTime会由@PreUpdate自动更新
    }

    /**
     * 从Map中获取字符串值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 从Map中获取BigDecimal值
     */
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从Map中获取LocalDate值
     */
    private LocalDate getLocalDate(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return null;
            }
            try {
                // Tushare返回的日期格式为 YYYYMMDD
                if (str.length() == 8) {
                    return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));
                }
                return LocalDate.parse(str);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从Map中获取Integer值
     */
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}