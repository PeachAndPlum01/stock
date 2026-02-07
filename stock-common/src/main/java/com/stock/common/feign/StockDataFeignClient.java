package com.stock.common.feign;

import com.stock.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 股票数据服务Feign客户端
 * 用于其他服务调用股票数据服务的接口
 */
@FeignClient(name = "stock-data-service", path = "/stock")
public interface StockDataFeignClient {

    /**
     * 获取股票基本信息
     *
     * @param stockCode 股票代码
     * @return 股票信息
     */
    @GetMapping("/info/{stockCode}")
    Result<Object> getStockInfo(@PathVariable("stockCode") String stockCode);

    /**
     * 获取股票实时行情
     *
     * @param stockCode 股票代码
     * @return 实时行情
     */
    @GetMapping("/realtime/{stockCode}")
    Result<Object> getRealtimeQuote(@PathVariable("stockCode") String stockCode);
}
