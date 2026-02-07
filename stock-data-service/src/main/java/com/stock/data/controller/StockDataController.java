package com.stock.data.controller;

import com.stock.common.result.Result;
import com.stock.data.entity.StockQuote;
import com.stock.data.service.StockRealtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 股票数据API控制器
 * 提供股票数据的REST API接口
 */
@RestController
@RequestMapping("/api/stock")
@Tag(name = "股票数据API")
public class StockDataController {

    @Autowired
    private StockRealtimeService stockRealtimeService;

    /**
     * 获取单只股票的实时数据
     *
     * @param tsCode 股票代码（如：000001.SZ）
     * @return 股票行情数据
     */
    @GetMapping("/realtime/{tsCode}")
    @Operation(summary = "获取单只股票的实时数据")
    public Result<StockQuote> getRealtimeData(
            @Parameter(description = "股票代码", required = true, example = "000001.SZ")
            @PathVariable("tsCode") String tsCode) {
        StockQuote stockQuote = stockRealtimeService.getRealtimeData(tsCode);

        if (stockQuote == null) {
            return Result.error("未找到股票数据");
        }

        return Result.success(stockQuote);
    }

    /**
     * 获取所有股票的实时数据
     *
     * @return 股票行情数据列表
     */
    @GetMapping("/realtime/all")
    @Operation(summary = "获取所有股票的实时数据")
    public Result<List<StockQuote>> getAllRealtimeData() {
        List<StockQuote> stockQuotes = stockRealtimeService.getAllRealtimeData();
        return Result.success(stockQuotes);
    }

    /**
     * 手动触发数据更新
     *
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(summary = "手动触发数据更新")
    public Result<String> manualUpdate() {
        String result = stockRealtimeService.manualUpdate();
        return Result.success(result);
    }

    /**
     * 健康检查接口
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Result<String> health() {
        return Result.success("服务正常");
    }
}