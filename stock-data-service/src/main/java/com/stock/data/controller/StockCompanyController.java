package com.stock.data.controller;

import com.stock.data.common.Result;
import com.stock.data.model.StockCompany;
import com.stock.data.service.StockCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 股票公司信息控制器
 * 提供公司信息的查询和刷新接口
 */
@RestController
@RequestMapping("/api/v1/company")
public class StockCompanyController {

    private static final Logger log = LoggerFactory.getLogger(StockCompanyController.class);

    @Autowired
    private StockCompanyService stockCompanyService;

    /**
     * 根据股票代码获取公司信息
     *
     * @param tsCode 股票代码
     * @return 公司信息
     */
    @GetMapping("/ts-code/{tsCode}")
    public Result<StockCompany> getCompanyByTsCode(@PathVariable String tsCode) {
        log.info("获取公司信息: tsCode={}", tsCode);

        StockCompany company = stockCompanyService.getCompanyByTsCode(tsCode);

        if (company == null) {
            return Result.error("未找到该公司信息");
        }

        return Result.success(company);
    }

    /**
     * 根据公司名称模糊查询
     *
     * @param name 公司名称（支持模糊查询）
     * @return 公司信息列表
     */
    @GetMapping("/search")
    public Result<List<StockCompany>> searchCompanyByName(@RequestParam String name) {
        log.info("查询公司信息: name={}", name);

        List<StockCompany> companies = stockCompanyService.getCompanyByName(name);

        return Result.success(companies);
    }

    /**
     * 根据城市查询公司信息
     *
     * @param city 城市
     * @return 公司信息列表
     */
    @GetMapping("/city/{city}")
    public Result<List<StockCompany>> getCompanyByCity(@PathVariable String city) {
        log.info("查询公司信息: city={}", city);

        List<StockCompany> companies = stockCompanyService.getCompanyByCity(city);

        return Result.success(companies);
    }

    /**
     * 根据交易所查询公司信息
     *
     * @param exchange 交易所（SSE上交所, SZSE深交所）
     * @return 公司信息列表
     */
    @GetMapping("/exchange/{exchange}")
    public Result<List<StockCompany>> getCompanyByExchange(@PathVariable String exchange) {
        log.info("查询公司信息: exchange={}", exchange);

        List<StockCompany> companies = stockCompanyService.getCompanyByExchange(exchange);

        return Result.success(companies);
    }

    /**
     * 获取所有公司信息
     *
     * @return 公司信息列表
     */
    @GetMapping("/all")
    public Result<List<StockCompany>> getAllCompanies() {
        log.info("获取所有公司信息");

        List<StockCompany> companies = stockCompanyService.getAllCompanies();

        return Result.success(companies);
    }

    /**
     * 获取公司统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCompanyStatistics() {
        log.info("获取公司统计信息");

        Map<String, Object> statistics = stockCompanyService.getCompanyStatistics();

        return Result.success(statistics);
    }

    /**
     * 手动刷新所有公司信息
     *
     * @return 刷新结果
     */
    @PostMapping("/refresh")
    public Result<String> refreshCompanyData() {
        log.info("手动刷新公司信息");

        String result = stockCompanyService.manualRefresh();

        return Result.success(result);
    }

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("StockCompanyService is running");
    }
}
