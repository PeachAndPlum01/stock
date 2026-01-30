package com.stock.service;

import com.stock.entity.InvestmentInfo;
import com.stock.mapper.InvestmentInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 投资信息服务类
 */
@Service
public class InvestmentInfoService {

    @Autowired
    private InvestmentInfoMapper investmentInfoMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String INVESTMENT_CACHE_PREFIX = "investment:province:";
    private static final String ALL_INVESTMENT_CACHE_KEY = "investment:all";
    private static final Long CACHE_EXPIRE_TIME = 10L; // 10分钟

    /**
     * 根据省份获取投资信息
     */
    public Map<String, Object> getInvestmentByProvince(String province, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 先从缓存获取
        String cacheKey = INVESTMENT_CACHE_PREFIX + province + ":" + limit;
        Map<String, Object> cachedData = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            return cachedData;
        }

        // 从数据库查询
        List<InvestmentInfo> investmentList = investmentInfoMapper.selectRecentByProvince(province, limit);

        // 获取关联省份并计算关联强度
        Map<String, Integer> provinceRelationCount = new HashMap<>();
        for (InvestmentInfo info : investmentList) {
            if (info.getRelatedProvinces() != null && !info.getRelatedProvinces().isEmpty()) {
                String[] provinces = info.getRelatedProvinces().split(",");
                for (String relatedProvince : provinces) {
                    relatedProvince = relatedProvince.trim();
                    if (!relatedProvince.equals(province)) {
                        provinceRelationCount.put(relatedProvince, 
                            provinceRelationCount.getOrDefault(relatedProvince, 0) + 1);
                    }
                }
            }
        }

        // 按关联强度排序，取前三个关联性最强的省份
        List<String> topRelatedProvinces = provinceRelationCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("province", province);
        result.put("investmentList", investmentList);
        result.put("relatedProvinces", topRelatedProvinces);
        result.put("total", investmentList.size());

        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 获取所有投资信息（用于地图展示）
     */
    public Map<String, Object> getAllInvestmentData() {
        // 先从缓存获取
        Map<String, Object> cachedData = (Map<String, Object>) redisTemplate.opsForValue().get(ALL_INVESTMENT_CACHE_KEY);
        
        if (cachedData != null) {
            return cachedData;
        }

        // 从数据库查询所有有效投资信息
        List<InvestmentInfo> allInvestments = investmentInfoMapper.selectAllActive();

        // 按省份分组统计
        Map<String, Long> provinceCountMap = allInvestments.stream()
                .collect(Collectors.groupingBy(InvestmentInfo::getProvince, Collectors.counting()));

        // 按省份分组获取投资总额
        Map<String, Double> provinceAmountMap = allInvestments.stream()
                .collect(Collectors.groupingBy(
                        InvestmentInfo::getProvince,
                        Collectors.summingDouble(info -> info.getInvestmentAmount().doubleValue())
                ));

        // 构建地图数据
        List<Map<String, Object>> mapData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : provinceCountMap.entrySet()) {
            Map<String, Object> provinceData = new HashMap<>();
            provinceData.put("name", entry.getKey());
            provinceData.put("value", entry.getValue());
            provinceData.put("amount", provinceAmountMap.get(entry.getKey()));
            mapData.add(provinceData);
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("mapData", mapData);
        result.put("totalInvestments", allInvestments.size());
        result.put("totalProvinces", provinceCountMap.size());

        // 缓存结果
        redisTemplate.opsForValue().set(ALL_INVESTMENT_CACHE_KEY, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 添加投资信息（并发送消息到RabbitMQ）
     */
    public void addInvestment(InvestmentInfo investmentInfo) {
        investmentInfoMapper.insert(investmentInfo);

        // 清除相关缓存
        clearCache(investmentInfo.getProvince());

        // 发送消息到RabbitMQ
        try {
            rabbitTemplate.convertAndSend("investment.exchange", "investment.new", investmentInfo);
        } catch (Exception e) {
            // 消息发送失败不影响主流程
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache(String province) {
        // 清除省份相关缓存
        Set<String> keys = redisTemplate.keys(INVESTMENT_CACHE_PREFIX + province + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // 清除全局缓存
        redisTemplate.delete(ALL_INVESTMENT_CACHE_KEY);
    }
}
