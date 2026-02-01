package com.stock.investment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stock.investment.entity.InvestmentInfo;
import com.stock.investment.service.InvestmentInfoService;
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
public class InvestmentInfoController {

    @Autowired
    private InvestmentInfoService investmentInfoService;

    /**
     * 分页查询股票列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "tenDayChange") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
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
    public ResponseEntity<Map<String, Object>> getDetail(@PathVariable Long id) {
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
    public ResponseEntity<Map<String, Object>> getTopGainers(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String province) {
        
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
    public ResponseEntity<Map<String, Object>> getByChangeRange(
            @RequestParam(required = false) BigDecimal minChange,
            @RequestParam(required = false) BigDecimal maxChange) {
        
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
    public ResponseEntity<Map<String, Object>> getByTheme(@PathVariable String theme) {
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
    public ResponseEntity<Map<String, Object>> getByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
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
