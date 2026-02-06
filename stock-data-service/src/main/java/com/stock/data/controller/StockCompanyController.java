package com.stock.data.controller;

import com.stock.data.common.Result;
import com.stock.data.model.StockCompany;
import com.stock.data.service.StockCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "公司信息API", description = "股票公司信息查询和刷新接口")
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
    @Operation(summary = "根据股票代码获取公司信息", description = "通过股票代码查询公司的详细信息")
    public Result<StockCompany> getCompanyByTsCode(
            @Parameter(description = "股票代码，如 000001.SZ", required = true)
            @PathVariable String tsCode) {
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
    @Operation(summary = "模糊查询公司信息", description = "根据公司名称进行模糊查询，返回匹配的公司列表")
    public Result<List<StockCompany>> searchCompanyByName(
            @Parameter(description = "公司名称，支持模糊查询", required = true)
            @RequestParam String name) {
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
    @Operation(summary = "根据城市查询公司信息", description = "查询指定城市的所有公司信息")
    public Result<List<StockCompany>> getCompanyByCity(
            @Parameter(description = "城市名称", required = true)
            @PathVariable String city) {
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
    @Operation(summary = "根据交易所查询公司信息", description = "查询指定交易所的所有公司信息")
    public Result<List<StockCompany>> getCompanyByExchange(
            @Parameter(description = "交易所代码，SSE表示上交所，SZSE表示深交所", required = true)
            @PathVariable String exchange) {
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
    @Operation(summary = "获取所有公司信息", description = "查询数据库中所有公司的完整信息")
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
    @Operation(summary = "获取公司统计信息", description = "获取公司数量、按交易所分布等统计数据")
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
    @Operation(summary = "手动刷新公司信息", description = "从数据源重新获取并更新所有公司信息")
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
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Result<String> health() {
        return Result.success("StockCompanyService is running");
    }
}
