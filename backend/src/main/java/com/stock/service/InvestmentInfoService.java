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
            limit = 100;
        }

        // 将省份名称转换为简称格式，确保与数据库格式一致
        String normalizedProvince = convertProvinceToAbbreviation(province);
        
        // 先从缓存获取
        String cacheKey = INVESTMENT_CACHE_PREFIX + normalizedProvince + ":" + limit;
        Map<String, Object> cachedData = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            return cachedData;
        }
        
        // 直接从数据库查询（使用简称）
        List<InvestmentInfo> investmentList = investmentInfoMapper.selectRecentByProvince(normalizedProvince, limit);

        // 获取关联省份并计算关联强度
        Map<String, Integer> provinceRelationCount = new HashMap<>();
        Map<String, List<InvestmentInfo>> provinceRelatedProjects = new HashMap<>(); // 记录每个关联省份对应的项目
        for (InvestmentInfo info : investmentList) {
            if (info.getRelatedProvinces() != null && !info.getRelatedProvinces().isEmpty()) {
                String[] provinces = info.getRelatedProvinces().split(",");
                for (String relatedProvince : provinces) {
                    relatedProvince = relatedProvince.trim();
                    if (!relatedProvince.equals(province)) {
                        provinceRelationCount.put(relatedProvince, 
                            provinceRelationCount.getOrDefault(relatedProvince, 0) + 1);
                        // 将关联省份名称转换为简称后存储，确保后续查询时key一致
                        String abbreviatedProvince = convertProvinceToAbbreviation(relatedProvince);
                        provinceRelatedProjects.computeIfAbsent(abbreviatedProvince, k -> new ArrayList<>()).add(info);
                    }
                }
            }
        }

        // 按关联强度排序，取前三个关联性最强的省份，并转换为简称格式
        List<String> topRelatedProvinces = provinceRelationCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(entry -> convertProvinceToAbbreviation(entry.getKey()))
                .collect(Collectors.toList());

        // 构建关联原因说明
        Map<String, Object> relatedReasons = new HashMap<>();
        for (String relatedProvince : topRelatedProvinces) {
            List<InvestmentInfo> relatedProjects = provinceRelatedProjects.get(relatedProvince);
            if (relatedProjects != null && !relatedProjects.isEmpty()) {
                // 统计共同行业
                Set<String> commonIndustries = relatedProjects.stream()
                        .map(InvestmentInfo::getIndustry)
                        .collect(Collectors.toSet());
                
                // 统计共同投资金额
                double totalAmount = relatedProjects.stream()
                        .mapToDouble(info -> info.getInvestmentAmount() != null ? info.getInvestmentAmount().doubleValue() : 0)
                        .sum();
                
                // 提取项目名称
                List<String> projectNames = relatedProjects.stream()
                        .map(InvestmentInfo::getTitle)
                        .collect(Collectors.toList());
                
                // 构建详细的项目信息
                List<Map<String, Object>> projectDetails = relatedProjects.stream()
                        .map(info -> {
                            Map<String, Object> detail = new HashMap<>();
                            detail.put("title", info.getTitle());
                            detail.put("industry", info.getIndustry());
                            detail.put("amount", info.getInvestmentAmount());
                            return detail;
                        })
                        .collect(Collectors.toList());
                
                // 生成说明文字
                StringBuilder reason = new StringBuilder();
                reason.append("共同投资了").append(relatedProjects.size()).append("个项目");
                if (commonIndustries.size() > 0) {
                    reason.append("，涉及").append(String.join("、", commonIndustries)).append("行业");
                }
                if (totalAmount > 0) {
                    reason.append("，总投资金额").append(String.format("%.0f", totalAmount)).append("万元");
                }
                
                // 构建返回对象，包含说明文字和项目详情
                Map<String, Object> reasonInfo = new HashMap<>();
                reasonInfo.put("description", reason.toString());
                reasonInfo.put("projects", projectDetails);
                
                relatedReasons.put(relatedProvince, reasonInfo);
            }
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("province", normalizedProvince);
        result.put("investmentList", investmentList);
        result.put("relatedProvinces", topRelatedProvinces);
        result.put("relatedReasons", relatedReasons);
        result.put("total", investmentList.size());

        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 将省份名称转换为简称
     */
    private String convertProvinceToAbbreviation(String provinceName) {
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put("北京", "京");
        provinceMap.put("北京市", "京");
        provinceMap.put("天津", "津");
        provinceMap.put("天津市", "津");
        provinceMap.put("河北", "冀");
        provinceMap.put("河北省", "冀");
        provinceMap.put("山西", "晋");
        provinceMap.put("山西省", "晋");
        provinceMap.put("内蒙古", "蒙");
        provinceMap.put("内蒙古自治区", "蒙");
        provinceMap.put("辽宁", "辽");
        provinceMap.put("辽宁省", "辽");
        provinceMap.put("吉林", "吉");
        provinceMap.put("吉林省", "吉");
        provinceMap.put("黑龙江", "黑");
        provinceMap.put("黑龙江省", "黑");
        provinceMap.put("上海", "沪");
        provinceMap.put("上海市", "沪");
        provinceMap.put("江苏", "苏");
        provinceMap.put("江苏省", "苏");
        provinceMap.put("浙江", "浙");
        provinceMap.put("浙江省", "浙");
        provinceMap.put("安徽", "皖");
        provinceMap.put("安徽省", "皖");
        provinceMap.put("福建", "闽");
        provinceMap.put("福建省", "闽");
        provinceMap.put("江西", "赣");
        provinceMap.put("江西省", "赣");
        provinceMap.put("山东", "鲁");
        provinceMap.put("山东省", "鲁");
        provinceMap.put("河南", "豫");
        provinceMap.put("河南省", "豫");
        provinceMap.put("湖北", "鄂");
        provinceMap.put("湖北省", "鄂");
        provinceMap.put("湖南", "湘");
        provinceMap.put("湖南省", "湘");
        provinceMap.put("广东", "粤");
        provinceMap.put("广东省", "粤");
        provinceMap.put("广西", "桂");
        provinceMap.put("广西壮族自治区", "桂");
        provinceMap.put("海南", "琼");
        provinceMap.put("海南省", "琼");
        provinceMap.put("重庆", "渝");
        provinceMap.put("重庆市", "渝");
        provinceMap.put("四川", "川");
        provinceMap.put("四川省", "川");
        provinceMap.put("贵州", "贵");
        provinceMap.put("贵州省", "贵");
        provinceMap.put("云南", "云");
        provinceMap.put("云南省", "云");
        provinceMap.put("西藏", "藏");
        provinceMap.put("西藏自治区", "藏");
        provinceMap.put("陕西", "陕");
        provinceMap.put("陕西省", "陕");
        provinceMap.put("甘肃", "甘");
        provinceMap.put("甘肃省", "甘");
        provinceMap.put("青海", "青");
        provinceMap.put("青海省", "青");
        provinceMap.put("宁夏", "宁");
        provinceMap.put("宁夏回族自治区", "宁");
        provinceMap.put("新疆", "新");
        provinceMap.put("新疆维吾尔自治区", "新");
        provinceMap.put("台湾", "台");
        provinceMap.put("台湾省", "台");
        provinceMap.put("香港", "港");
        provinceMap.put("香港特别行政区", "港");
        provinceMap.put("澳门", "澳");
        provinceMap.put("澳门特别行政区", "澳");
        
        return provinceMap.getOrDefault(provinceName, provinceName);
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
            // 将省份全称转换为简称
            String provinceName = entry.getKey();
            String provinceAbbr = convertProvinceToAbbreviation(provinceName);
            provinceData.put("name", provinceAbbr);
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