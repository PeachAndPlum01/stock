package com.stock.data.controller;

import com.stock.data.common.Result;
import com.stock.data.model.StockQuote;
import com.stock.data.service.StockRealtimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 股票数据API控制器
 * 提供股票数据的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/stock")
@Api(tags = "股票数据API")
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
    @ApiOperation("获取单只股票的实时数据")
    public Result<StockQuote> getRealtimeData(
            @ApiParam(value = "股票代码", required = true, example = "000001.SZ")
            @PathVariable("tsCode") String tsCode) {
        try {
            StockQuote stockQuote = stockRealtimeService.getRealtimeData(tsCode);
            
            if (stockQuote == null) {
                return Result.error("未找到股票数据");
            }
            
            return Result.success(stockQuote);
            
        } catch (Exception e) {
            log.error("获取股票实时数据失败: tsCode={}", tsCode, e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有股票的实时数据
     * 
     * @return 股票行情数据列表
     */
    @GetMapping("/realtime/all")
    @ApiOperation("获取所有股票的实时数据")
    public Result<List<StockQuote>> getAllRealtimeData() {
        try {
            List<StockQuote> stockQuotes = stockRealtimeService.getAllRealtimeData();
            return Result.success(stockQuotes);
            
        } catch (Exception e) {
            log.error("获取所有股票实时数据失败", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发数据更新
     * 
     * @return 更新结果
     */
    @PostMapping("/update")
    @ApiOperation("手动触发数据更新")
    public Result<String> manualUpdate() {
        try {
            String result = stockRealtimeService.manualUpdate();
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("手动更新失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public Result<String> health() {
        return Result.success("服务正常");
    }
}
