package com.stock.controller;

import com.stock.common.Result;
import com.stock.entity.RelatedProvince;
import com.stock.service.ProvinceCorrelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 省份相关度控制器
 */
@RestController
@RequestMapping("/correlation")
public class ProvinceCorrelationController {

    @Autowired
    private ProvinceCorrelationService provinceCorrelationService;

    /**
     * 查询某省份最相关的省份列表
     * @param province 省份简称
     * @param limit 返回数量限制，默认5
     * @return 相关省份列表
     */
    @GetMapping("/top/{province}")
    public Result<List<RelatedProvince>> getTopRelated(
            @PathVariable String province,
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<RelatedProvince> data = provinceCorrelationService.getTopRelatedProvinces(province, limit);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询两个省份之间的相关度详情
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 相关度详情
     */
    @GetMapping("/detail")
    public Result<RelatedProvince> getCorrelationDetail(
            @RequestParam String provinceA,
            @RequestParam String provinceB) {
        try {
            RelatedProvince data = provinceCorrelationService.getCorrelationByProvincePair(provinceA, provinceB);
            if (data == null) {
                return Result.error("未找到相关度数据");
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 手动触发重新计算所有省份相关度
     * @return 操作结果
     */
    @PostMapping("/recalculate")
    public Result<String> recalculateCorrelations() {
        try {
            provinceCorrelationService.recalculateAllCorrelations();
            return Result.success("重新计算完成", "省份相关度已更新");
        } catch (Exception e) {
            return Result.error("计算失败：" + e.getMessage());
        }
    }

    /**
     * 基于相关度数据更新投资信息的related_provinces字段
     * 只保留与项目所在省份相关度最高的前N个省份
     * @param params 包含topN参数的Map
     * @return 操作结果
     */
    @PostMapping("/update-related-provinces")
    public Result<String> updateRelatedProvinces(@RequestBody(required = false) Map<String, Integer> params) {
        try {
            Integer topN = params != null ? params.get("topN") : 5;
            provinceCorrelationService.updateRelatedProvincesBasedOnCorrelation(topN);
            return Result.success("更新完成", "投资信息的related_provinces字段已基于相关度数据更新");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
