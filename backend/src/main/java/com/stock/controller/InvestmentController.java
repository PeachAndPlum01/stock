package com.stock.controller;

import com.stock.common.Result;
import com.stock.entity.InvestmentInfo;
import com.stock.service.InvestmentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 投资信息控制器
 */
@RestController
@RequestMapping("/investment")
public class InvestmentController {

    @Autowired
    private InvestmentInfoService investmentInfoService;

    /**
     * 根据省份获取投资信息
     */
    @GetMapping("/province/{province}")
    public Result<Map<String, Object>> getByProvince(
            @PathVariable String province,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            Map<String, Object> data = investmentInfoService.getInvestmentByProvince(province, limit);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有投资数据（用于地图展示）
     */
    @GetMapping("/map/data")
    public Result<Map<String, Object>> getMapData() {
        try {
            Map<String, Object> data = investmentInfoService.getAllInvestmentData();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加投资信息
     */
    @PostMapping("/add")
    public Result<Void> addInvestment(@RequestBody InvestmentInfo investmentInfo) {
        try {
            investmentInfoService.addInvestment(investmentInfo);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }
}
