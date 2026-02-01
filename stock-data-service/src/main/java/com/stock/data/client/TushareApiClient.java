package com.stock.data.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.stock.data.config.TushareProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Tushare API客户端
 * 用于调用Tushare API获取股票数据
 */
@Slf4j
@Component
public class TushareApiClient {

    @Autowired
    private TushareProperties tushareProperties;

    private OkHttpClient httpClient;

    private static final String DAILY_API = "daily";
    private static final String REALTIME_API = "realtime";
    private static final String TRADE_CALENDAR_API = "trade_cal";
    private static final String STOCK_BASIC_API = "stock_basic";

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(tushareProperties.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(tushareProperties.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(tushareProperties.getTimeout(), TimeUnit.SECONDS)
                .build();
        
        log.info("Tushare API客户端初始化完成");
    }

    /**
     * 调用Tushare API
     * 
     * @param apiName API名称
     * @param params 请求参数
     * @return API响应数据
     */
    public Map<String, Object> callApi(String apiName, Map<String, Object> params) {
        try {
            String token = tushareProperties.getToken();
            
            if (token == null || token.isEmpty() || token.equals("your_tushare_token_here")) {
                log.error("Tushare Token未配置，请在application.yml中设置tushare.token");
                return null;
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("api_name", apiName);
            requestBody.put("token", token);
            requestBody.put("params", params);
            requestBody.put("fields", "");

            String jsonBody = JSON.toJSONString(requestBody);

            Request request = new Request.Builder()
                    .url(tushareProperties.getApiUrl())
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                    .build();

            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Tushare API调用失败: {}", response.code());
                return null;
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);

            // 检查响应状态
            Integer code = jsonResponse.getInteger("code");
            if (code != null && code != 0) {
                log.error("Tushare API返回错误: code={}, msg={}", 
                        code, jsonResponse.getString("msg"));
                return null;
            }

            JSONObject data = jsonResponse.getJSONObject("data");
            if (data != null) {
                return data.toJavaObject(Map.class);
            }

            return null;

        } catch (IOException e) {
            log.error("Tushare API调用异常", e);
            return null;
        }
    }

    /**
     * 获取股票日线行情数据
     * 
     * @param tsCode 股票代码（如：000001.SZ）
     * @param startDate 开始日期（如：20240101）
     * @param endDate 结束日期（如：20240131）
     * @return 行情数据列表
     */
    public List<Map<String, Object>> getDailyData(String tsCode, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("ts_code", tsCode);
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        Map<String, Object> response = callApi(DAILY_API, params);
        if (response == null) {
            return Collections.emptyList();
        }

        return (List<Map<String, Object>>) response.get("items");
    }

    /**
     * 获取单只股票的最新行情数据
     * 
     * @param tsCode 股票代码
     * @return 最新行情数据
     */
    public Map<String, Object> getLatestQuote(String tsCode) {
        String today = getCurrentDate();
        String startDate = getStartDate(30); // 获取最近30天的数据

        List<Map<String, Object>> dataList = getDailyData(tsCode, startDate, today);
        
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }

        // 返回最新的一条数据
        return dataList.get(0);
    }

    /**
     * 批量获取多只股票的最新行情数据
     * 
     * @param tsCodes 股票代码列表
     * @return 股票代码到行情数据的映射
     */
    public Map<String, Map<String, Object>> getLatestQuotesBatch(List<String> tsCodes) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        for (String tsCode : tsCodes) {
            try {
                Map<String, Object> quote = getLatestQuote(tsCode);
                if (quote != null) {
                    resultMap.put(tsCode, quote);
                }
            } catch (Exception e) {
                log.error("获取股票行情失败: {}", tsCode, e);
            }
        }

        return resultMap;
    }

    /**
     * 计算N日涨跌幅
     * 
     * @param tsCode 股票代码
     * @param nDays 天数
     * @return N日涨跌幅
     */
    public java.math.BigDecimal calculateNDayChange(String tsCode, int nDays) {
        try {
            String today = getCurrentDate();
            String startDate = getStartDate(nDays + 5); // 多获取几天，确保有足够的数据

            List<Map<String, Object>> dataList = getDailyData(tsCode, startDate, today);
            
            if (dataList == null || dataList.size() < nDays) {
                log.warn("数据不足，无法计算{}日涨跌幅: {}", nDays, tsCode);
                return null;
            }

            // 获取N天前的收盘价和最新的收盘价
            java.math.BigDecimal oldPrice = getClosePrice(dataList, nDays);
            java.math.BigDecimal newPrice = getClosePrice(dataList, 0);

            if (oldPrice == null || newPrice == null || oldPrice.compareTo(java.math.BigDecimal.ZERO) == 0) {
                return null;
            }

            // 计算涨跌幅
            return newPrice.subtract(oldPrice)
                    .divide(oldPrice, 4, java.math.BigDecimal.ROUND_HALF_UP)
                    .multiply(new java.math.BigDecimal(100));

        } catch (Exception e) {
            log.error("计算N日涨跌幅失败: {}", tsCode, e);
            return null;
        }
    }

    /**
     * 获取交易日期
     * 
     * @param exchange 交易所代码（SSE上交所, SZSE深交所）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易日期列表
     */
    public List<String> getTradeDates(String exchange, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("exchange", exchange);
        params.put("start_date", startDate);
        params.put("end_date", endDate);
        params.put("is_open", "1"); // 只返回开市的日期

        Map<String, Object> response = callApi(TRADE_CALENDAR_API, params);
        if (response == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<String> dates = new ArrayList<>();

        if (items != null) {
            for (Map<String, Object> item : items) {
                dates.add((String) item.get("cal_date"));
            }
        }

        return dates;
    }

    /**
     * 获取股票基本信息
     * 
     * @param tsCode 股票代码
     * @return 股票基本信息
     */
    public Map<String, Object> getStockBasic(String tsCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("ts_code", tsCode);

        Map<String, Object> response = callApi(STOCK_BASIC_API, params);
        if (response == null) {
            return null;
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        }

        return null;
    }

    /**
     * 获取当前日期（YYYYMMDD格式）
     */
    private String getCurrentDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 获取N天前的日期（YYYYMMDD格式）
     */
    private String getStartDate(int nDays) {
        java.time.LocalDate date = java.time.LocalDate.now().minusDays(nDays);
        return date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 从数据列表中获取第N天的收盘价
     * 
     * @param dataList 数据列表
     * @param index 索引（0表示最新）
     * @return 收盘价
     */
    private java.math.BigDecimal getClosePrice(List<Map<String, Object>> dataList, int index) {
        if (index >= dataList.size()) {
            return null;
        }

        Map<String, Object> data = dataList.get(index);
        Object close = data.get("close");

        if (close == null) {
            return null;
        }

        if (close instanceof Number) {
            return new java.math.BigDecimal(close.toString());
        }

        if (close instanceof String) {
            return new java.math.BigDecimal((String) close);
        }

        return null;
    }
}
