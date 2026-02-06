package com.stock.investment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stock.investment.entity.InvestmentInfo;
import com.stock.investment.service.InvestmentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投资信息控制器
 */
@RestController
@RequestMapping("/investment")
@Tag(name = "投资信息API", description = "股票投资信息查询、统计和分析接口")
public class InvestmentInfoController {

    @Autowired
    private InvestmentInfoService investmentInfoService;

    /**
     * 分页查询股票列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询股票列表", description = "支持按省份、关键词过滤，支持排序")
    public ResponseEntity<Map<String, Object>> getList(
            @Parameter(description = "页码，从1开始，默认为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认为20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "省份过滤，可选") @RequestParam(required = false) String province,
            @Parameter(description = "关键词搜索，可选") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段，默认为tenDayChange（十日涨跌幅）") @RequestParam(defaultValue = "tenDayChange") String sortBy,
            @Parameter(description = "排序方向，asc升序/desc降序，默认为desc") @RequestParam(defaultValue = "desc") String sortOrder) {
        
        try {
            IPage<InvestmentInfo> result = investmentInfoService.getInvestmentList(
                    page, size, province, keyword, sortBy, sortOrder);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result.getRecords());
            response.put("total", result.getTotal());
            response.put("page", result.getCurrent());
            response.put("size", result.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取股票详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取股票详情", description = "根据ID获取单条股票的详细信息")
    public ResponseEntity<Map<String, Object>> getDetail(
            @Parameter(description = "股票ID", required = true)
            @PathVariable Long id) {
        try {
            InvestmentInfo info = investmentInfoService.getInvestmentById(id);

            Map<String, Object> response = new HashMap<>();
            if (info != null) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", info);
            } else {
                response.put("code", 404);
                response.put("message", "股票信息不存在");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取地图数据
     */
    @GetMapping("/map/data")
    @Operation(summary = "获取地图数据", description = "按省份统计股票数据，用于地图展示")
    public ResponseEntity<Map<String, Object>> getMapData() {
        try {
            List<Map<String, Object>> stats = investmentInfoService.countByProvince();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("mapData", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按省份统计
     */
    @GetMapping("/stats/province")
    @Operation(summary = "按省份统计", description = "统计各省份的股票数量等信息")
    public ResponseEntity<Map<String, Object>> countByProvince() {
        try {
            List<Map<String, Object>> stats = investmentInfoService.countByProvince();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "统计成功");
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "统计失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取涨幅榜
     */
    @GetMapping("/top/gainers")
    @Operation(summary = "获取涨幅榜", description = "获取涨幅最高的股票列表，可按省份过滤")
    public ResponseEntity<Map<String, Object>> getTopGainers(
            @Parameter(description = "返回数量，默认为10") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "省份过滤，可选") @RequestParam(required = false) String province) {
        
        try {
            List<InvestmentInfo> list;
            if (province != null && !province.isEmpty()) {
                list = investmentInfoService.getTopGainersByProvince(province, limit);
            } else {
                list = investmentInfoService.getTopGainers(limit);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", list);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按涨幅范围查询
     */
    @GetMapping("/range")
    @Operation(summary = "按涨幅范围查询", description = "查询涨幅在指定范围内的股票")
    public ResponseEntity<Map<String, Object>> getByChangeRange(
            @Parameter(description = "最小涨幅，可选") @RequestParam(required = false) BigDecimal minChange,
            @Parameter(description = "最大涨幅，可选") @RequestParam(required = false) BigDecimal maxChange) {
        
        try {
            List<InvestmentInfo> list = investmentInfoService.getByChangeRange(minChange, maxChange);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", list);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按题材查询
     */
    @GetMapping("/theme/{theme}")
    @Operation(summary = "按题材查询", description = "查询指定题材的股票")
    public ResponseEntity<Map<String, Object>> getByTheme(
            @Parameter(description = "题材名称", required = true)
            @PathVariable String theme) {
        try {
            List<InvestmentInfo> list = investmentInfoService.getByTheme(theme);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", list);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 按日期范围查询
     */
    @GetMapping("/date")
    @Operation(summary = "按日期范围查询", description = "查询指定日期范围内的股票")
    public ResponseEntity<Map<String, Object>> getByDate(
            @Parameter(description = "开始日期，格式YYYY-MM-DD") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期，格式YYYY-MM-DD") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            List<InvestmentInfo> list = investmentInfoService.getByDate(startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", list);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取所有省份列表
     */
    @GetMapping("/provinces")
    @Operation(summary = "获取所有省份列表", description = "获取所有有股票数据的省份名称列表")
    public ResponseEntity<Map<String, Object>> getAllProvinces() {
        try {
            List<String> provinces = investmentInfoService.getAllProvinces();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", provinces);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
