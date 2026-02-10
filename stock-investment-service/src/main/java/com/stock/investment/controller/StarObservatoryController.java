package com.stock.investment.controller;

import com.stock.common.result.Result;
import com.stock.investment.entity.StarGraphData;
import com.stock.investment.service.StarObservatoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/investment/star")
@Tag(name = "观星台接口")
@CrossOrigin(origins = "*")
public class StarObservatoryController {

    @Autowired
    private StarObservatoryService starObservatoryService;

    @GetMapping("/graph")
    @Operation(summary = "获取股票关系图谱数据")
    public Result<StarGraphData> getGraphData() {
        return Result.success(starObservatoryService.getGraphData());
    }

    @GetMapping("/analyze")
    @Operation(summary = "获取初始分析文本")
    public Result<Map<String, String>> getAnalysis() {
        Map<String, String> result = new HashMap<>();
        result.put("content", starObservatoryService.analyzeRelations());
        return Result.success(result);
    }

    @PostMapping("/chat")
    @Operation(summary = "提问")
    public Result<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = starObservatoryService.chat(question);
        Map<String, String> result = new HashMap<>();
        result.put("answer", answer);
        return Result.success(result);
    }
}
