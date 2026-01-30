package com.stock.service;

import com.stock.entity.InvestmentInfo;
import com.stock.entity.ProvinceDistance;
import com.stock.entity.RelatedProvince;
import com.stock.mapper.InvestmentInfoMapper;
import com.stock.mapper.ProvinceDistanceMapper;
import com.stock.mapper.RelatedProvinceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 省份相关度计算服务类
 */
@Service
public class ProvinceCorrelationService {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceCorrelationService.class);

    @Autowired
    private InvestmentInfoMapper investmentInfoMapper;

    @Autowired
    private ProvinceDistanceMapper provinceDistanceMapper;

    @Autowired
    private RelatedProvinceMapper relatedProvinceMapper;

    /**
     * 各维度权重配置
     */
    private static final Map<String, BigDecimal> DIMENSION_WEIGHTS = new HashMap<>();
    static {
        DIMENSION_WEIGHTS.put("JOINT_PROJECT", new BigDecimal("0.4"));
        DIMENSION_WEIGHTS.put("INDUSTRY", new BigDecimal("0.2"));
        DIMENSION_WEIGHTS.put("INVESTMENT_TYPE", new BigDecimal("0.15"));
        DIMENSION_WEIGHTS.put("GEOGRAPHY", new BigDecimal("0.1"));
        DIMENSION_WEIGHTS.put("INVESTMENT_AMOUNT", new BigDecimal("0.15"));
    }

    /**
     * 定时任务：每天凌晨2点重新计算所有省份相关度
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void recalculateAllCorrelations() {
        logger.info("开始计算所有省份相关度...");
        
        try {
            // 获取所有省份
            Set<String> provinces = getAllProvinces();
            logger.info("共发现{}个省份", provinces.size());
            
            // 清空现有的相关度数据
            relatedProvinceMapper.delete(null);
            
            // 计算每对省份的相关度
            int count = 0;
            List<String> provinceList = new ArrayList<>(provinces);
            for (int i = 0; i < provinceList.size(); i++) {
                for (int j = i + 1; j < provinceList.size(); j++) {
                    String provinceA = provinceList.get(i);
                    String provinceB = provinceList.get(j);
                    
                    RelatedProvince correlation = calculateCorrelation(provinceA, provinceB);
                    if (correlation != null && correlation.getCorrelationScore().compareTo(BigDecimal.ZERO) > 0) {
                        relatedProvinceMapper.insert(correlation);
                        count++;
                    }
                }
                
                // 每处理10个省份输出一次日志
                if (i % 10 == 0) {
                    logger.info("已处理{}/{}个省份", i + 1, provinceList.size());
                }
            }
            
            logger.info("省份相关度计算完成，共计算{}对省份", count);

            // 计算完成后，自动更新投资信息的related_provinces字段
            logger.info("开始更新投资信息的related_provinces字段...");
            updateRelatedProvincesBasedOnCorrelation(5); // 保留前5个最相关的省份
            logger.info("投资信息related_provinces字段更新完成");

        } catch (Exception e) {
            logger.error("计算省份相关度失败", e);
            throw e;
        }
    }

    /**
     * 计算两个省份之间的相关度
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 省份相关度对象
     */
    public RelatedProvince calculateCorrelation(String provinceA, String provinceB) {
        try {
            // 获取两个省份的关联项目
            List<InvestmentInfo> jointProjects = investmentInfoMapper.selectJointByProvincePair(provinceA, provinceB);
            
            // 如果没有共同项目，返回null或低相关度
            if (jointProjects == null || jointProjects.isEmpty()) {
                // 计算地理相关度
                BigDecimal geoScore = calculateGeographicScore(provinceA, provinceB);
                if (geoScore.compareTo(BigDecimal.ZERO) > 0) {
                    RelatedProvince correlation = new RelatedProvince();
                    correlation.setSourceProvince(provinceA);
                    correlation.setSourceProvinceCode(getProvinceCode(provinceA));
                    correlation.setTargetProvince(provinceB);
                    correlation.setTargetProvinceCode(getProvinceCode(provinceB));
                    correlation.setCorrelationScore(geoScore.multiply(DIMENSION_WEIGHTS.get("GEOGRAPHY")));
                    correlation.setGeographicDistance(getGeographicDistance(provinceA, provinceB));
                    correlation.setJointProjectCount(0);
                    correlation.setCalculationMethod("GEOGRAPHY_ONLY");
                    return correlation;
                }
                return null;
            }
            
            // 获取各自省份的投资项目
            List<InvestmentInfo> investA = investmentInfoMapper.selectByRelatedProvince(provinceA);
            List<InvestmentInfo> investB = investmentInfoMapper.selectByRelatedProvince(provinceB);
            
            // 计算各个维度的相关度
            BigDecimal jointProjectScore = calculateJointProjectScore(jointProjects, investA, investB);
            BigDecimal industryScore = calculateIndustrySimilarity(investA, investB, jointProjects);
            BigDecimal investmentTypeScore = calculateInvestmentTypeSimilarity(investA, investB, jointProjects);
            BigDecimal geographicScore = calculateGeographicScore(provinceA, provinceB);
            BigDecimal amountScore = calculateInvestmentAmountScore(jointProjects);
            
            // 计算综合相关度得分
            BigDecimal correlationScore = jointProjectScore.multiply(DIMENSION_WEIGHTS.get("JOINT_PROJECT"))
                    .add(industryScore.multiply(DIMENSION_WEIGHTS.get("INDUSTRY")))
                    .add(investmentTypeScore.multiply(DIMENSION_WEIGHTS.get("INVESTMENT_TYPE")))
                    .add(geographicScore.multiply(DIMENSION_WEIGHTS.get("GEOGRAPHY")))
                    .add(amountScore.multiply(DIMENSION_WEIGHTS.get("INVESTMENT_AMOUNT")));
            
            // 确保得分在0-1之间
            correlationScore = correlationScore.min(BigDecimal.ONE).max(BigDecimal.ZERO);
            
            // 构建相关度对象
            RelatedProvince correlation = new RelatedProvince();
            correlation.setSourceProvince(provinceA);
            correlation.setSourceProvinceCode(getProvinceCode(provinceA));
            correlation.setTargetProvince(provinceB);
            correlation.setTargetProvinceCode(getProvinceCode(provinceB));
            correlation.setCorrelationScore(correlationScore.setScale(6, RoundingMode.HALF_UP));
            correlation.setJointProjectCount(jointProjects.size());
            correlation.setSourceInvestCount(investA != null ? investA.size() : 0);
            correlation.setTargetInvestCount(investB != null ? investB.size() : 0);
            correlation.setTotalInvestmentAmount(calculateTotalAmount(jointProjects));
            correlation.setIndustrySimilarity(industryScore.setScale(6, RoundingMode.HALF_UP));
            correlation.setInvestmentTypeSimilarity(investmentTypeScore.setScale(6, RoundingMode.HALF_UP));
            correlation.setGeographicDistance(getGeographicDistance(provinceA, provinceB));
            correlation.setRelatedProjectIds(getProjectIdsJson(jointProjects));
            correlation.setCommonIndustries(getCommonIndustries(investA, investB, jointProjects));
            correlation.setCommonInvestmentTypes(getCommonInvestmentTypes(investA, investB, jointProjects));
            correlation.setCalculationMethod("COMPREHENSIVE");
            
            return correlation;
        } catch (Exception e) {
            logger.error("计算省份{}和{}相关度失败", provinceA, provinceB, e);
            return null;
        }
    }

    /**
     * 计算共同项目相关度（使用Jaccard相似度）
     */
    private BigDecimal calculateJointProjectScore(List<InvestmentInfo> jointProjects, 
                                                  List<InvestmentInfo> investA, 
                                                  List<InvestmentInfo> investB) {
        int jointCount = jointProjects != null ? jointProjects.size() : 0;
        int countA = investA != null ? investA.size() : 0;
        int countB = investB != null ? investB.size() : 0;
        
        if (countA == 0 || countB == 0) {
            return BigDecimal.ZERO;
        }
        
        // Jaccard相似度: |A ∩ B| / |A ∪ B|
        int unionCount = countA + countB - jointCount;
        if (unionCount == 0) {
            return BigDecimal.ZERO;
        }
        
        return new BigDecimal(jointCount).divide(new BigDecimal(unionCount), 6, RoundingMode.HALF_UP);
    }

    /**
     * 计算行业相似度（使用余弦相似度）
     */
    private BigDecimal calculateIndustrySimilarity(List<InvestmentInfo> investA, 
                                                   List<InvestmentInfo> investB,
                                                   List<InvestmentInfo> jointProjects) {
        // 统计行业频率
        Map<String, Integer> industryFreqA = getIndustryFrequency(investA);
        Map<String, Integer> industryFreqB = getIndustryFrequency(investB);
        
        if (industryFreqA.isEmpty() || industryFreqB.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 计算余弦相似度
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        Set<String> allIndustries = new HashSet<>();
        allIndustries.addAll(industryFreqA.keySet());
        allIndustries.addAll(industryFreqB.keySet());
        
        for (String industry : allIndustries) {
            int freqA = industryFreqA.getOrDefault(industry, 0);
            int freqB = industryFreqB.getOrDefault(industry, 0);
            
            dotProduct += freqA * freqB;
            normA += freqA * freqA;
            normB += freqB * freqB;
        }
        
        if (normA == 0 || normB == 0) {
            return BigDecimal.ZERO;
        }
        
        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return new BigDecimal(similarity).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * 计算投资类型相似度（使用Jaccard相似度）
     */
    private BigDecimal calculateInvestmentTypeSimilarity(List<InvestmentInfo> investA, 
                                                         List<InvestmentInfo> investB,
                                                         List<InvestmentInfo> jointProjects) {
        Set<String> typesA = getInvestmentTypes(investA);
        Set<String> typesB = getInvestmentTypes(investB);
        
        if (typesA.isEmpty() || typesB.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Jaccard相似度
        Set<String> intersection = new HashSet<>(typesA);
        intersection.retainAll(typesB);
        
        Set<String> union = new HashSet<>(typesA);
        union.addAll(typesB);
        
        if (union.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return new BigDecimal(intersection.size())
                .divide(new BigDecimal(union.size()), 6, RoundingMode.HALF_UP);
    }

    /**
     * 计算地理距离相关度（使用距离衰减函数）
     */
    private BigDecimal calculateGeographicScore(String provinceA, String provinceB) {
        Integer distance = getGeographicDistance(provinceA, provinceB);
        if (distance == null || distance == 0) {
            return BigDecimal.ONE;
        }
        
        // 距离衰减函数: Score = 1 / (1 + distance / scale)
        // scale = 2000km
        double scale = 2000;
        double score = 1.0 / (1.0 + distance / scale);
        
        return new BigDecimal(score).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * 计算投资金额相关度
     */
    private BigDecimal calculateInvestmentAmountScore(List<InvestmentInfo> jointProjects) {
        if (jointProjects == null || jointProjects.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 归一化：使用log函数压缩金额范围
        BigDecimal totalAmount = calculateTotalAmount(jointProjects);
        double logAmount = Math.log10(totalAmount.doubleValue() + 1);
        
        // 假设最大金额为100亿元(1000000万元)
        double maxLogAmount = Math.log10(1000001);
        
        double score = Math.min(logAmount / maxLogAmount, 1.0);
        return new BigDecimal(score).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * 获取行业频率统计
     */
    private Map<String, Integer> getIndustryFrequency(List<InvestmentInfo> investments) {
        Map<String, Integer> freqMap = new HashMap<>();
        if (investments == null) {
            return freqMap;
        }
        
        for (InvestmentInfo inv : investments) {
            String industry = inv.getIndustry();
            if (industry != null && !industry.isEmpty()) {
                freqMap.put(industry, freqMap.getOrDefault(industry, 0) + 1);
            }
        }
        return freqMap;
    }

    /**
     * 获取投资类型集合
     */
    private Set<String> getInvestmentTypes(List<InvestmentInfo> investments) {
        Set<String> types = new HashSet<>();
        if (investments == null) {
            return types;
        }
        
        for (InvestmentInfo inv : investments) {
            String type = inv.getInvestmentType();
            if (type != null && !type.isEmpty()) {
                types.add(type);
            }
        }
        return types;
    }

    /**
     * 计算总投资金额
     */
    private BigDecimal calculateTotalAmount(List<InvestmentInfo> investments) {
        BigDecimal total = BigDecimal.ZERO;
        if (investments == null) {
            return total;
        }
        
        for (InvestmentInfo inv : investments) {
            if (inv.getInvestmentAmount() != null) {
                total = total.add(inv.getInvestmentAmount());
            }
        }
        return total;
    }

    /**
     * 获取项目ID的JSON字符串
     */
    private String getProjectIdsJson(List<InvestmentInfo> investments) {
        if (investments == null || investments.isEmpty()) {
            return null;
        }
        
        List<Long> ids = investments.stream()
                .map(InvestmentInfo::getId)
                .collect(Collectors.toList());
        
        return ids.toString();
    }

    /**
     * 获取共同行业
     */
    private String getCommonIndustries(List<InvestmentInfo> investA, 
                                       List<InvestmentInfo> investB,
                                       List<InvestmentInfo> jointProjects) {
        Map<String, Integer> freqA = getIndustryFrequency(investA);
        Map<String, Integer> freqB = getIndustryFrequency(investB);
        
        List<String> commonIndustries = new ArrayList<>();
        for (String industry : freqA.keySet()) {
            if (freqB.containsKey(industry)) {
                commonIndustries.add(industry);
            }
        }
        
        return String.join(",", commonIndustries);
    }

    /**
     * 获取共同投资类型
     */
    private String getCommonInvestmentTypes(List<InvestmentInfo> investA, 
                                            List<InvestmentInfo> investB,
                                            List<InvestmentInfo> jointProjects) {
        Set<String> typesA = getInvestmentTypes(investA);
        Set<String> typesB = getInvestmentTypes(investB);
        
        typesA.retainAll(typesB);
        
        return String.join(",", typesA);
    }

    /**
     * 获取地理距离
     */
    private Integer getGeographicDistance(String provinceA, String provinceB) {
        ProvinceDistance distance = provinceDistanceMapper.selectByProvincePair(provinceA, provinceB);
        return distance != null ? distance.getDistanceKm() : null;
    }

    /**
     * 获取省份编码
     */
    private String getProvinceCode(String province) {
        // 简化版：返回固定编码，实际应该从配置表或数据库查询
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("京", "110000");
        codeMap.put("津", "120000");
        codeMap.put("冀", "130000");
        codeMap.put("晋", "140000");
        codeMap.put("蒙", "150000");
        codeMap.put("辽", "210000");
        codeMap.put("吉", "220000");
        codeMap.put("黑", "230000");
        codeMap.put("沪", "310000");
        codeMap.put("苏", "320000");
        codeMap.put("浙", "330000");
        codeMap.put("皖", "340000");
        codeMap.put("闽", "350000");
        codeMap.put("赣", "360000");
        codeMap.put("鲁", "370000");
        codeMap.put("豫", "410000");
        codeMap.put("鄂", "420000");
        codeMap.put("湘", "430000");
        codeMap.put("粤", "440000");
        codeMap.put("桂", "450000");
        codeMap.put("琼", "460000");
        codeMap.put("渝", "500000");
        codeMap.put("川", "510000");
        codeMap.put("贵", "520000");
        codeMap.put("云", "530000");
        codeMap.put("藏", "540000");
        codeMap.put("陕", "610000");
        codeMap.put("甘", "620000");
        codeMap.put("青", "630000");
        codeMap.put("宁", "640000");
        codeMap.put("新", "650000");
        codeMap.put("台", "710000");
        codeMap.put("港", "810000");
        codeMap.put("澳", "820000");
        
        return codeMap.getOrDefault(province, "");
    }

    /**
     * 获取所有省份
     */
    private Set<String> getAllProvinces() {
        Set<String> provinces = new HashSet<>();
        List<InvestmentInfo> allInvestments = investmentInfoMapper.selectAllActive();
        
        for (InvestmentInfo inv : allInvestments) {
            if (inv.getProvince() != null) {
                provinces.add(inv.getProvince());
            }
            if (inv.getRelatedProvinces() != null) {
                String[] related = inv.getRelatedProvinces().split(",");
                for (String r : related) {
                    if (!r.trim().isEmpty()) {
                        provinces.add(r.trim());
                    }
                }
            }
        }
        
        return provinces;
    }

    /**
     * 查询某省份最相关的省份列表
     * @param province 省份简称
     * @param limit 查询数量限制
     * @return 相关省份列表
     */
    public List<RelatedProvince> getTopRelatedProvinces(String province, Integer limit) {
        return relatedProvinceMapper.selectTopRelatedByProvince(province, limit);
    }

    /**
     * 查询两个省份之间的相关度
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 相关度记录
     */
    public RelatedProvince getCorrelationByProvincePair(String provinceA, String provinceB) {
        return relatedProvinceMapper.selectByProvincePair(provinceA, provinceB);
    }

    /**
     * 基于相关度数据更新所有投资信息的related_provinces字段
     * 只保留与项目所在省份相关度最高的前N个省份
     * @param topN 保留的省份数量，默认为5
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRelatedProvincesBasedOnCorrelation(Integer topN) {
        if (topN == null || topN <= 0) {
            topN = 5; // 默认保留前5个最相关的省份
        }

        logger.info("开始基于相关度数据更新投资信息的related_provinces字段，保留前{}个最相关的省份", topN);

        try {
            // 获取所有有效的投资信息
            List<InvestmentInfo> allInvestments = investmentInfoMapper.selectAllActive();
            logger.info("共处理{}条投资信息", allInvestments.size());

            int updateCount = 0;
            for (InvestmentInfo investment : allInvestments) {
                String province = investment.getProvince();
                if (province == null || province.isEmpty()) {
                    continue;
                }

                // 查询与该省份相关度最高的前N个省份
                List<RelatedProvince> relatedList = relatedProvinceMapper
                        .selectTopRelatedByProvince(province, topN);

                if (relatedList == null || relatedList.isEmpty()) {
                    // 如果没有相关省份数据，清空related_provinces字段
                    if (investment.getRelatedProvinces() != null && !investment.getRelatedProvinces().isEmpty()) {
                        investment.setRelatedProvinces(null);
                        investmentInfoMapper.updateById(investment);
                        updateCount++;
                    }
                    continue;
                }

                // 构建新的related_provinces字符串（逗号分隔）
                String newRelatedProvinces = relatedList.stream()
                        .map(related -> {
                            // 确定目标省份：如果source是当前省份，则取target，否则取source
                            if (related.getSourceProvince().equals(province)) {
                                return related.getTargetProvince();
                            } else {
                                return related.getSourceProvince();
                            }
                        })
                        .collect(Collectors.joining(","));

                // 只有当相关度数据发生变化时才更新
                String oldRelatedProvinces = investment.getRelatedProvinces();
                if (!newRelatedProvinces.equals(oldRelatedProvinces)) {
                    investment.setRelatedProvinces(newRelatedProvinces);
                    investmentInfoMapper.updateById(investment);
                    updateCount++;

                    // 每100条更新一次日志
                    if (updateCount % 100 == 0) {
                        logger.info("已更新{}条投资信息", updateCount);
                    }
                }
            }

            logger.info("投资信息related_provinces字段更新完成，共更新{}条记录", updateCount);
        } catch (Exception e) {
            logger.error("更新投资信息related_provinces字段失败", e);
            throw e;
        }
    }
}
