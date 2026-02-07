package com.stock.correlation.controller;

import com.stock.correlation.entity.RelatedProvince;
import com.stock.correlation.service.CorrelationService;
import com.stock.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 省份关联控制器
 */
@RestController
@RequestMapping("/correlation")
@Tag(name = "省份关联API", description = "省份之间股票相关度查询和分析接口")
public class CorrelationController {

    @Autowired
    private CorrelationService correlationService;

    /**
     * 获取指定省份的相关省份列表
     */
    @GetMapping("/province/{province}")
    @Operation(summary = "获取指定省份的相关省份列表", description = "查询与指定省份股票走势相关的其他省份")
    public Result<?> getRelatedProvinces(
            @Parameter(description = "省份名称", required = true)
            @PathVariable String province) {
        List<RelatedProvince> list = correlationService.getRelatedProvinces(province);
        return Result.success(list);
    }

    /**
     * 获取两个省份之间的相关度
     */
    @GetMapping("/between")
    @Operation(summary = "获取两个省份之间的相关度", description = "查询两个省份之间股票走势的相关系数")
    public Result<?> getCorrelation(
            @Parameter(description = "源省份", required = true) @RequestParam String source,
            @Parameter(description = "目标省份", required = true) @RequestParam String target) {

        RelatedProvince correlation = correlationService.getCorrelation(source, target);
        if (correlation == null) {
            return Result.error(404, "未找到相关数据");
        }

        return Result.success(correlation);
    }

    /**
     * 获取相关度最高的N对省份
     */
    @GetMapping("/top")
    @Operation(summary = "获取相关度最高的省份对", description = "返回股票走势相关度最高的省份对列表")
    public Result<?> getTopCorrelations(
            @Parameter(description = "返回数量，默认为10") @RequestParam(defaultValue = "10") int limit) {

        List<RelatedProvince> list = correlationService.getTopCorrelations(limit);
        return Result.success(list);
    }

    /**
     * 按相关度范围查询
     */
    @GetMapping("/range")
    @Operation(summary = "按相关度范围查询", description = "查询相关度在指定范围内的省份对")
    public Result<?> getByScoreRange(
            @Parameter(description = "最小相关度，可选") @RequestParam(required = false) BigDecimal minScore,
            @Parameter(description = "最大相关度，可选") @RequestParam(required = false) BigDecimal maxScore) {

        List<RelatedProvince> list = correlationService.getByScoreRange(minScore, maxScore);
        return Result.success(list);
    }

    /**
     * 获取所有省份关联数据
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有省份关联数据", description = "获取所有省份之间的股票相关度数据")
    public Result<?> getAllCorrelations() {
        List<RelatedProvince> list = correlationService.getAllCorrelations();
        return Result.success(list);
    }

    /**
     * 按共同概念查询
     */
    @GetMapping("/concept/{concept}")
    @Operation(summary = "按共同概念查询", description = "查询拥有共同概念的省份之间的相关度")
    public Result<?> getByConcept(
            @Parameter(description = "概念名称", required = true)
            @PathVariable String concept) {
        List<RelatedProvince> list = correlationService.getByConcept(concept);
        return Result.success(list);
    }

    /**
     * 按共同行业查询
     */
    @GetMapping("/industry/{industry}")
    @Operation(summary = "按共同行业查询", description = "查询拥有共同行业的省份之间的相关度")
    public Result<?> getByIndustry(
            @Parameter(description = "行业名称", required = true)
            @PathVariable String industry) {
        List<RelatedProvince> list = correlationService.getByIndustry(industry);
        return Result.success(list);
    }
}