package com.stock.service;

import com.stock.entity.InvestmentInfo;
import com.stock.entity.RelatedProvince;
import com.stock.mapper.InvestmentInfoMapper;
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
    private ProvinceCorrelationService provinceCorrelationService;

    private static final String INVESTMENT_CACHE_PREFIX = "investment:province:";
    private static final String ALL_INVESTMENT_CACHE_KEY = "investment:all";
    private static final Long CACHE_EXPIRE_TIME = 10L; // 10分钟

    /**
     * 根据省份获取投资信息（返回近十日涨幅最高的8个股票）
     * 以及相关度最高的前3个省份的企业和关联原因
     */
    public Map<String, Object> getInvestmentByProvince(String province, Integer limit) {
        // 将省份名称转换为简称格式，确保与数据库格式一致
        String normalizedProvince = convertProvinceToAbbreviation(province);
        
        // 先从缓存获取
        String cacheKey = INVESTMENT_CACHE_PREFIX + normalizedProvince + ":top8";
        Map<String, Object> cachedData = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            return cachedData;
        }
        
        // 从数据库查询近十日涨幅最高的8个股票
        List<InvestmentInfo> investmentList = investmentInfoMapper.selectTop8ByTenDayChange(normalizedProvince);

        // 获取相关度最高的前3个省份及其关联原因
        List<Map<String, Object>> relatedProvinceDetails = getRelatedProvinceDetails(normalizedProvince, investmentList);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("province", normalizedProvince);
        result.put("topStocks", investmentList); // 当前省份涨幅最高的企业
        result.put("relatedProvinces", relatedProvinceDetails); // 相关省份及其企业、原因
        result.put("total", investmentList.size());

        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return result;
    }
    
    /**
     * 获取相关省份及其企业和关联原因
     * @param province 当前省份
     * @param currentTopStocks 当前省份涨幅最高的企业
     * @return 相关省份详情列表
     */
    private List<Map<String, Object>> getRelatedProvinceDetails(String province, List<InvestmentInfo> currentTopStocks) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            // 从相关度表获取相关度最高的前3个省份
            List<RelatedProvince> relatedProvinceList = provinceCorrelationService.getTopRelatedProvinces(province, 3);
            
            if (relatedProvinceList == null || relatedProvinceList.isEmpty()) {
                return result;
            }
            
            // 获取当前省份的排名前3的企业（用于生成关联原因）
            List<InvestmentInfo> currentTop3 = currentTopStocks.stream()
                    .limit(3)
                    .collect(Collectors.toList());
            
            for (RelatedProvince rp : relatedProvinceList) {
                String relatedProvince = rp.getTargetProvince().equals(province) 
                    ? rp.getSourceProvince() 
                    : rp.getTargetProvince();
                
                // 获取关联省份涨幅最高的前3个企业
                List<InvestmentInfo> relatedTopStocks = investmentInfoMapper.selectTop3ByTenDayChange(relatedProvince);
                
                // 生成关联原因
                String correlationReason = generateDetailedCorrelationReason(
                    province, currentTop3, 
                    relatedProvince, relatedTopStocks, 
                    rp.getCorrelationReason()
                );
                
                Map<String, Object> provinceDetail = new HashMap<>();
                provinceDetail.put("province", relatedProvince);
                provinceDetail.put("provinceFullName", convertProvinceToFullName(relatedProvince));
                provinceDetail.put("topStocks", relatedTopStocks);
                provinceDetail.put("correlationReason", correlationReason);
                provinceDetail.put("correlationScore", rp.getCorrelationScore());
                
                result.add(provinceDetail);
            }
        } catch (Exception e) {
            System.err.println("获取相关省份详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 生成详细的关联原因说明
     * 格式：a省的[企业1,企业2,企业3]与b省的[企业1,企业2,企业3]因[概念1,概念2]原因相似度高，均因[共同概念]原因上涨
     */
    private String generateDetailedCorrelationReason(String provinceA, List<InvestmentInfo> stocksA,
                                                    String provinceB, List<InvestmentInfo> stocksB,
                                                    String correlationReasonJson) {
        // 转换为完整省份名
        String provinceAFull = convertProvinceToFullName(provinceA);
        String provinceBFull = convertProvinceToFullName(provinceB);
        
        // 提取A省的企业名称
        List<String> companyNamesA = stocksA.stream()
                .map(InvestmentInfo::getCompanyName)
                .filter(Objects::nonNull)
                .limit(3)
                .collect(Collectors.toList());
        
        // 提取B省的企业名称
        List<String> companyNamesB = stocksB.stream()
                .map(InvestmentInfo::getCompanyName)
                .filter(Objects::nonNull)
                .limit(3)
                .collect(Collectors.toList());
        
        // 解析相关度原因JSON，提取共同概念
        Set<String> commonConcepts = new HashSet<>();
        Set<String> commonIndustries = new HashSet<>();
        try {
            if (correlationReasonJson != null && !correlationReasonJson.isEmpty()) {
                com.alibaba.fastjson2.JSONObject reasonData = com.alibaba.fastjson2.JSONObject.parseObject(correlationReasonJson);
                if (reasonData.containsKey("commonConcepts")) {
                    Object concepts = reasonData.get("commonConcepts");
                    if (concepts instanceof com.alibaba.fastjson2.JSONArray) {
                        com.alibaba.fastjson2.JSONArray conceptsArray = (com.alibaba.fastjson2.JSONArray) concepts;
                        for (int i = 0; i < conceptsArray.size(); i++) {
                            commonConcepts.add(conceptsArray.getString(i));
                        }
                    } else if (concepts instanceof String) {
                        String conceptsStr = (String) concepts;
                        if (!conceptsStr.isEmpty()) {
                            String[] parts = conceptsStr.split(",");
                            for (String part : parts) {
                                commonConcepts.add(part.trim());
                            }
                        }
                    }
                }
                if (reasonData.containsKey("commonIndustries")) {
                    Object industries = reasonData.get("commonIndustries");
                    if (industries instanceof com.alibaba.fastjson2.JSONArray) {
                        com.alibaba.fastjson2.JSONArray industriesArray = (com.alibaba.fastjson2.JSONArray) industries;
                        for (int i = 0; i < industriesArray.size(); i++) {
                            commonIndustries.add(industriesArray.getString(i));
                        }
                    } else if (industries instanceof String) {
                        String industriesStr = (String) industries;
                        if (!industriesStr.isEmpty()) {
                            String[] parts = industriesStr.split(",");
                            for (String part : parts) {
                                commonIndustries.add(part.trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 如果解析失败，从股票的title字段中提取共同概念
            commonConcepts.addAll(extractCommonConceptsFromStocks(stocksA, stocksB));
        }
        
        // 如果没有共同概念，从股票中提取
        if (commonConcepts.isEmpty()) {
            commonConcepts.addAll(extractCommonConceptsFromStocks(stocksA, stocksB));
        }
        
        // 构建新格式的JSON
        com.alibaba.fastjson2.JSONObject result = new com.alibaba.fastjson2.JSONObject();
        
        // 添加共同概念（如果有）
        if (!commonConcepts.isEmpty()) {
            result.put("commonConcepts", new ArrayList<>(commonConcepts));
        }
        
        // 添加共同行业（如果有）
        if (!commonIndustries.isEmpty()) {
            result.put("commonIndustries", new ArrayList<>(commonIndustries));
        }
        
        // 构建格式化的description
        StringBuilder description = new StringBuilder();
        
        // 第一行：省份A：股票名称
        if (!companyNamesA.isEmpty()) {
            description.append(provinceAFull).append("：");
            description.append(String.join("、", companyNamesA));
            description.append("<br/>");
        }
        
        // 第二行：省份B：股票名称
        if (!companyNamesB.isEmpty()) {
            description.append(provinceBFull).append("：");
            description.append(String.join("、", companyNamesB));
            description.append("<br/>");
        }
        
        // 第三行：关联原因
        description.append("关联原因：");
        
        // 优先使用共同概念
        if (!commonConcepts.isEmpty()) {
            List<String> conceptList = new ArrayList<>(commonConcepts);
            int count = Math.min(conceptList.size(), 3);
            description.append(String.join("、", conceptList.subList(0, count)));
            if (conceptList.size() > 3) {
                description.append("等");
            }
        }
        // 如果没有共同概念，使用共同行业
        else if (!commonIndustries.isEmpty()) {
            List<String> industryList = new ArrayList<>(commonIndustries);
            int count = Math.min(industryList.size(), 2);
            description.append(String.join("、", industryList.subList(0, count)));
            if (industryList.size() > 2) {
                description.append("等");
            }
        }
        // 如果都没有，使用默认描述
        else {
            description.append("行业分布、地域特征");
        }
        
        result.put("description", description.toString());
        
        return result.toJSONString();
    }
    
    /**
     * 当没有明显共同概念时，生成替代的关联原因
     * 基于行业、地域或其他特征
     */
    private String generateAlternativeReason(List<InvestmentInfo> stocksA, List<InvestmentInfo> stocksB) {
        // 收集A省和B省的所有概念
        Set<String> conceptsA = new HashSet<>();
        Set<String> conceptsB = new HashSet<>();
        
        for (InvestmentInfo stock : stocksA) {
            if (stock.getTitle() != null && !stock.getTitle().isEmpty()) {
                String[] parts = stock.getTitle().split(",");
                for (String part : parts) {
                    String concept = part.trim();
                    if (!concept.isEmpty()) {
                        conceptsA.add(concept);
                    }
                }
            }
        }
        
        for (InvestmentInfo stock : stocksB) {
            if (stock.getTitle() != null && !stock.getTitle().isEmpty()) {
                String[] parts = stock.getTitle().split(",");
                for (String part : parts) {
                    String concept = part.trim();
                    if (!concept.isEmpty()) {
                        conceptsB.add(concept);
                    }
                }
            }
        }
        
        // 检查是否有相似的概念（模糊匹配）
        List<String> similarConcepts = findSimilarConcepts(conceptsA, conceptsB);
        
        if (!similarConcepts.isEmpty()) {
            // 如果有相似概念，使用相似概念
            return String.join("、", similarConcepts) + "等概念题材相关原因相似度高，均因相似题材原因上涨";
        }
        
        // 如果完全没有相似概念，检查行业或市场表现
        double avgChangeA = stocksA.stream()
                .map(InvestmentInfo::getTenDayChange)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d.doubleValue())
                .average()
                .orElse(0.0);
        
        double avgChangeB = stocksB.stream()
                .map(InvestmentInfo::getTenDayChange)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d.doubleValue())
                .average()
                .orElse(0.0);
        
        // 如果涨幅接近，说明市场表现相似
        if (Math.abs(avgChangeA - avgChangeB) < 2.0) {
            return String.format("市场表现相似（涨幅相近）原因相似度高，均因市场行情原因上涨");
        }
        
        // 默认原因
        return "行业分布和地域特征相关原因相似度高，均因市场行情原因上涨";
    }
    
    /**
     * 查找相似的概念（模糊匹配）
     */
    private List<String> findSimilarConcepts(Set<String> conceptsA, Set<String> conceptsB) {
        List<String> similarConcepts = new ArrayList<>();
        
        // 定义概念映射表，将相似的概念归类
        Map<String, List<String>> conceptMapping = new HashMap<>();
        conceptMapping.put("新材料", Arrays.asList("新材料", "石墨烯", "复合材料", "磁材", "高分子"));
        conceptMapping.put("电子科技", Arrays.asList("电子信息", "集成电路", "光电子", "半导体", "激光", "显示技术"));
        conceptMapping.put("高端制造", Arrays.asList("高端制造", "智能制造", "工业4.0", "机器人", "数字化"));
        conceptMapping.put("生物医药", Arrays.asList("生物医药", "创新药", "疫苗", "医疗"));
        
        // 检查每个概念类别
        for (Map.Entry<String, List<String>> entry : conceptMapping.entrySet()) {
            String category = entry.getKey();
            List<String> categoryConcepts = entry.getValue();
            
            boolean hasInA = conceptsA.stream().anyMatch(c -> categoryConcepts.contains(c));
            boolean hasInB = conceptsB.stream().anyMatch(c -> categoryConcepts.contains(c));
            
            if (hasInA && hasInB) {
                similarConcepts.add(category);
            }
        }
        
        return similarConcepts;
    }
    
    /**
     * 从两组股票中提取共同概念
     */
    private Set<String> extractCommonConceptsFromStocks(List<InvestmentInfo> stocksA, List<InvestmentInfo> stocksB) {
        Set<String> conceptsA = new HashSet<>();
        Set<String> conceptsB = new HashSet<>();
        
        // 提取A省股票的概念
        for (InvestmentInfo stock : stocksA) {
            if (stock.getTitle() != null && !stock.getTitle().isEmpty()) {
                String[] parts = stock.getTitle().split(",");
                for (String part : parts) {
                    String concept = part.trim();
                    if (!concept.isEmpty()) {
                        conceptsA.add(concept);
                    }
                }
            }
        }
        
        // 提取B省股票的概念
        for (InvestmentInfo stock : stocksB) {
            if (stock.getTitle() != null && !stock.getTitle().isEmpty()) {
                String[] parts = stock.getTitle().split(",");
                for (String part : parts) {
                    String concept = part.trim();
                    if (!concept.isEmpty()) {
                        conceptsB.add(concept);
                    }
                }
            }
        }
        
        // 返回交集
        conceptsA.retainAll(conceptsB);
        return conceptsA;
    }
    
    /**
     * 将省份简称转换为完整名称
     */
    private String convertProvinceToFullName(String abbreviation) {
        if (abbreviation == null || abbreviation.isEmpty()) {
            return "未知省份";
        }
        
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put("新", "新疆");
        provinceMap.put("藏", "西藏");
        provinceMap.put("蒙", "内蒙古");
        provinceMap.put("黑", "黑龙江");
        provinceMap.put("吉", "吉林");
        provinceMap.put("辽", "辽宁");
        provinceMap.put("冀", "河北");
        provinceMap.put("晋", "山西");
        provinceMap.put("陕", "陕西");
        provinceMap.put("甘", "甘肃");
        provinceMap.put("宁", "宁夏");
        provinceMap.put("青", "青海");
        provinceMap.put("鲁", "山东");
        provinceMap.put("豫", "河南");
        provinceMap.put("苏", "江苏");
        provinceMap.put("浙", "浙江");
        provinceMap.put("皖", "安徽");
        provinceMap.put("闽", "福建");
        provinceMap.put("赣", "江西");
        provinceMap.put("鄂", "湖北");
        provinceMap.put("湘", "湖南");
        provinceMap.put("粤", "广东");
        provinceMap.put("桂", "广西");
        provinceMap.put("琼", "海南");
        provinceMap.put("川", "四川");
        provinceMap.put("贵", "贵州");
        provinceMap.put("云", "云南");
        provinceMap.put("渝", "重庆");
        provinceMap.put("京", "北京");
        provinceMap.put("津", "天津");
        provinceMap.put("沪", "上海");
        
        return provinceMap.getOrDefault(abbreviation.trim(), abbreviation);
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
     * 添加投资信息
     */
    public void addInvestment(InvestmentInfo investmentInfo) {
        investmentInfoMapper.insert(investmentInfo);

        // 清除相关缓存
        clearCache(investmentInfo.getProvince());
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