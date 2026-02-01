package com.stock.correlation.controller;

import com.stock.correlation.entity.RelatedProvince;
import com.stock.correlation.service.CorrelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 省份关联控制器
 */
@RestController
@RequestMapping("/correlation")
public class CorrelationController {

    @Autowired
    private CorrelationService correlationService;

    /**
     * 获取指定省份的相关省份列表
     */
    @GetMapping("/province/{province}")
    public ResponseEntity<Map<String, Object>> getRelatedProvinces(@PathVariable String province) {
        try {
            List<RelatedProvince> list = correlationService.getRelatedProvinces(province);

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
     * 获取两个省份之间的相关度
     */
    @GetMapping("/between")
    public ResponseEntity<Map<String, Object>> getCorrelation(
            @RequestParam String source,
            @RequestParam String target) {
        
        try {
            RelatedProvince correlation = correlationService.getCorrelation(source, target);

            Map<String, Object> response = new HashMap<>();
            if (correlation != null) {
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", correlation);
            } else {
                response.put("code", 404);
                response.put("message", "未找到相关数据");
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
     * 获取相关度最高的N对省份
     */
    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopCorrelations(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<RelatedProvince> list = correlationService.getTopCorrelations(limit);

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
     * 按相关度范围查询
     */
    @GetMapping("/range")
    public ResponseEntity<Map<String, Object>> getByScoreRange(
            @RequestParam(required = false) BigDecimal minScore,
            @RequestParam(required = false) BigDecimal maxScore) {
        
        try {
            List<RelatedProvince> list = correlationService.getByScoreRange(minScore, maxScore);

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
     * 获取所有省份关联数据
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCorrelations() {
        try {
            List<RelatedProvince> list = correlationService.getAllCorrelations();

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
     * 按共同概念查询
     */
    @GetMapping("/concept/{concept}")
    public ResponseEntity<Map<String, Object>> getByConcept(@PathVariable String concept) {
        try {
            List<RelatedProvince> list = correlationService.getByConcept(concept);

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
     * 按共同行业查询
     */
    @GetMapping("/industry/{industry}")
    public ResponseEntity<Map<String, Object>> getByIndustry(@PathVariable String industry) {
        try {
            List<RelatedProvince> list = correlationService.getByIndustry(industry);

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
}
