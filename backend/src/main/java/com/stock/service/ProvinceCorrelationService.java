package com.stock.service;

import com.stock.entity.InvestmentInfo;
import com.stock.entity.RelatedProvince;
import com.stock.mapper.InvestmentInfoMapper;
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
    private RelatedProvinceMapper relatedProvinceMapper;

    /**
     * 各维度权重配置（基于涨幅前5股票相似度）
     */
    private static final Map<String, BigDecimal> DIMENSION_WEIGHTS = new HashMap<>();
    static {
        DIMENSION_WEIGHTS.put("CONCEPT", new BigDecimal("0.5"));     // 概念题材相似度权重50%
        DIMENSION_WEIGHTS.put("INDUSTRY", new BigDecimal("0.3"));    // 行业相似度权重30%
        DIMENSION_WEIGHTS.put("COMPANY_TYPE", new BigDecimal("0.2")); // 公司性质相似度权重20%
    }

    /**
     * 定时任务：每天凌晨2点重新计算所有省份相关度
     * 基于涨幅前5股票的概念题材、行业、公司性质相似度
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void recalculateAllCorrelations() {
        logger.info("开始基于涨幅前5股票计算所有省份相关度...");
        
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
                    
                    RelatedProvince correlation = calculateCorrelationByTop5Stocks(provinceA, provinceB);
                    if (correlation != null && correlation.getCorrelationScore().compareTo(new BigDecimal("0.15")) > 0) {
                        // 降低阈值到0.15，让股票数量少的省份也能找到相关省份
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
     * 基于涨幅前N股票计算两个省份之间的相关度
     * 核心逻辑：对比两个省份涨幅前N的股票在概念题材、行业、公司性质等方面的相似度
     * 支持股票数量少的省份，使用实际可用的股票数量进行计算
     * 
     * @param provinceA 省份A简称
     * @param provinceB 省份B简称
     * @return 省份相关度对象
     */
    public RelatedProvince calculateCorrelationByTop5Stocks(String provinceA, String provinceB) {
        try {
            // 获取两个省份涨幅前5的股票
            List<InvestmentInfo> top5StocksA = investmentInfoMapper.selectTop5ByTenDayChange(provinceA);
            List<InvestmentInfo> top5StocksB = investmentInfoMapper.selectTop5ByTenDayChange(provinceB);
            
            // 如果任一省份涨幅数据不足，返回null
            if (top5StocksA == null || top5StocksA.isEmpty() || 
                top5StocksB == null || top5StocksB.isEmpty()) {
                return null;
            }
            
            // 提取概念题材（从title字段，逗号分隔）
            List<String> conceptsA = extractConcepts(top5StocksA);
            List<String> conceptsB = extractConcepts(top5StocksB);
            
            // 提取行业（从industry字段）
            List<String> industriesA = extractIndustries(top5StocksA);
            List<String> industriesB = extractIndustries(top5StocksB);
            
            // 提取公司性质（从investmentType字段，如市盈率高低代表公司性质）
            List<String> companyTypesA = extractCompanyTypes(top5StocksA);
            List<String> companyTypesB = extractCompanyTypes(top5StocksB);
            
            // 计算各维度相似度
            BigDecimal conceptSimilarity = calculateConceptSimilarity(conceptsA, conceptsB);
            BigDecimal industrySimilarity = calculateIndustrySimilarity(top5StocksA, top5StocksB);
            BigDecimal companyTypeSimilarity = calculateCompanyTypeSimilarity(companyTypesA, companyTypesB);
            
            // 加权计算综合相关度
            BigDecimal correlationScore = conceptSimilarity.multiply(DIMENSION_WEIGHTS.get("CONCEPT"))
                    .add(industrySimilarity.multiply(DIMENSION_WEIGHTS.get("INDUSTRY")))
                    .add(companyTypeSimilarity.multiply(DIMENSION_WEIGHTS.get("COMPANY_TYPE")));
            
            // 确保得分在0-1之间
            correlationScore = correlationScore.min(BigDecimal.ONE).max(BigDecimal.ZERO);
            
            // 构建相关度对象
            RelatedProvince correlation = new RelatedProvince();
            correlation.setSourceProvince(provinceA);
            correlation.setSourceProvinceCode(getProvinceCode(provinceA));
            correlation.setTargetProvince(provinceB);
            correlation.setTargetProvinceCode(getProvinceCode(provinceB));
            correlation.setCorrelationScore(correlationScore.setScale(6, RoundingMode.HALF_UP));
            correlation.setConceptSimilarity(conceptSimilarity.setScale(6, RoundingMode.HALF_UP));
            correlation.setIndustrySimilarity(industrySimilarity.setScale(6, RoundingMode.HALF_UP));
            correlation.setCompanyTypeSimilarity(companyTypeSimilarity.setScale(6, RoundingMode.HALF_UP));
            
            // 提取共同概念和行业
            List<String> commonConcepts = findCommonElements(conceptsA, conceptsB);
            List<String> commonIndustries = findCommonElements(industriesA, industriesB);
            correlation.setCommonConcepts(String.join(",", commonConcepts));
            correlation.setCommonIndustries(String.join(",", commonIndustries));
            
            // 生成关联原因说明（JSON格式）
            correlation.setCorrelationReason(generateCorrelationReason(
                correlationScore, conceptSimilarity, industrySimilarity, companyTypeSimilarity,
                commonConcepts, commonIndustries, top5StocksA, top5StocksB
            ));
            
            // 保存涨幅前5股票信息（JSON格式）
            correlation.setSourceTop5Stocks(generateStocksJson(top5StocksA));
            correlation.setTargetTop5Stocks(generateStocksJson(top5StocksB));
            
            correlation.setCalculationMethod("TOP5_SIMILARITY");
            
            return correlation;
        } catch (Exception e) {
            logger.error("计算省份{}和{}相关度失败", provinceA, provinceB, e);
            return null;
        }
    }

    /**
     * 提取概念题材列表
     */
    private List<String> extractConcepts(List<InvestmentInfo> stocks) {
        Set<String> concepts = new HashSet<>();
        for (InvestmentInfo stock : stocks) {
            String title = stock.getTitle();
            if (title != null && !title.isEmpty()) {
                // title字段格式：概念1,概念2,概念3
                String[] parts = title.split(",");
                for (String part : parts) {
                    String concept = part.trim();
                    if (!concept.isEmpty()) {
                        concepts.add(concept);
                    }
                }
            }
        }
        return new ArrayList<>(concepts);
    }

    /**
     * 提取行业列表
     */
    private List<String> extractIndustries(List<InvestmentInfo> stocks) {
        Set<String> industries = new HashSet<>();
        for (InvestmentInfo stock : stocks) {
            String industry = stock.getIndustry();
            if (industry != null && !industry.isEmpty()) {
                industries.add(industry.trim());
            }
        }
        return new ArrayList<>(industries);
    }

    /**
     * 提取公司性质列表（基于市盈率高低判断）
     */
    private List<String> extractCompanyTypes(List<InvestmentInfo> stocks) {
        Set<String> types = new HashSet<>();
        for (InvestmentInfo stock : stocks) {
            String investmentType = stock.getInvestmentType();
            if (investmentType != null && !investmentType.isEmpty()) {
                try {
                    BigDecimal peRatio = new BigDecimal(investmentType);
                    if (peRatio.compareTo(new BigDecimal("0")) <= 0) {
                        types.add("亏损企业");
                    } else if (peRatio.compareTo(new BigDecimal("15")) <= 0) {
                        types.add("价值股");
                    } else if (peRatio.compareTo(new BigDecimal("50")) <= 0) {
                        types.add("成长股");
                    } else {
                        types.add("高估股");
                    }
                } catch (NumberFormatException e) {
                    // 如果市盈率不是数字，直接使用原始值
                    types.add(investmentType.trim());
                }
            }
        }
        return new ArrayList<>(types);
    }

    /**
     * 计算概念题材相似度（使用Jaccard相似度）
     */
    private BigDecimal calculateConceptSimilarity(List<String> conceptsA, List<String> conceptsB) {
        if (conceptsA.isEmpty() || conceptsB.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        Set<String> setA = new HashSet<>(conceptsA);
        Set<String> setB = new HashSet<>(conceptsB);
        
        // Jaccard相似度: |A ∩ B| / |A ∪ B|
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        
        if (union.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return new BigDecimal(intersection.size())
                .divide(new BigDecimal(union.size()), 6, RoundingMode.HALF_UP);
    }

    /**
     * 计算行业相似度（使用余弦相似度）
     */
    private BigDecimal calculateIndustrySimilarity(List<InvestmentInfo> stocksA, List<InvestmentInfo> stocksB) {
        Map<String, Integer> freqA = new HashMap<>();
        Map<String, Integer> freqB = new HashMap<>();
        
        for (InvestmentInfo stock : stocksA) {
            String industry = stock.getIndustry();
            if (industry != null && !industry.isEmpty()) {
                freqA.put(industry, freqA.getOrDefault(industry, 0) + 1);
            }
        }
        
        for (InvestmentInfo stock : stocksB) {
            String industry = stock.getIndustry();
            if (industry != null && !industry.isEmpty()) {
                freqB.put(industry, freqB.getOrDefault(industry, 0) + 1);
            }
        }
        
        if (freqA.isEmpty() || freqB.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 余弦相似度
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        Set<String> allIndustries = new HashSet<>();
        allIndustries.addAll(freqA.keySet());
        allIndustries.addAll(freqB.keySet());
        
        for (String industry : allIndustries) {
            int countA = freqA.getOrDefault(industry, 0);
            int countB = freqB.getOrDefault(industry, 0);
            
            dotProduct += countA * countB;
            normA += countA * countA;
            normB += countB * countB;
        }
        
        if (normA == 0 || normB == 0) {
            return BigDecimal.ZERO;
        }
        
        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return new BigDecimal(similarity).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * 计算公司性质相似度（使用Jaccard相似度）
     */
    private BigDecimal calculateCompanyTypeSimilarity(List<String> typesA, List<String> typesB) {
        if (typesA.isEmpty() || typesB.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        Set<String> setA = new HashSet<>(typesA);
        Set<String> setB = new HashSet<>(typesB);
        
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        
        if (union.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return new BigDecimal(intersection.size())
                .divide(new BigDecimal(union.size()), 6, RoundingMode.HALF_UP);
    }

    /**
     * 查找两个列表的共同元素
     */
    private List<String> findCommonElements(List<String> listA, List<String> listB) {
        Set<String> setA = new HashSet<>(listA);
        Set<String> setB = new HashSet<>(listB);
        
        setA.retainAll(setB);
        return new ArrayList<>(setA);
    }

    /**
     * 生成关联原因说明（自然语言格式）
     * 格式：近期两省排名前几的股票在上涨原因相似，他们分别是新疆的XXX、XXX，青海的XXX、XXX，都因风电、光伏等清洁能源概念上涨
     */
    private String generateCorrelationReason(BigDecimal correlationScore,
                                            BigDecimal conceptSimilarity,
                                            BigDecimal industrySimilarity,
                                            BigDecimal companyTypeSimilarity,
                                            List<String> commonConcepts,
                                            List<String> commonIndustries,
                                            List<InvestmentInfo> top5StocksA,
                                            List<InvestmentInfo> top5StocksB) {
        StringBuilder reason = new StringBuilder();
        reason.append("{");
        
        // 保留技术指标（供后台分析使用）
        reason.append("\"correlationScore\":\"").append(correlationScore.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).append("%\",");
        reason.append("\"conceptSimilarity\":\"").append(conceptSimilarity.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).append("%\",");
        reason.append("\"industrySimilarity\":\"").append(industrySimilarity.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).append("%\",");
        reason.append("\"companyTypeSimilarity\":\"").append(companyTypeSimilarity.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).append("%\",");
        
        // 共同概念说明
        if (!commonConcepts.isEmpty()) {
            reason.append("\"commonConcepts\":[");
            for (int i = 0; i < commonConcepts.size(); i++) {
                reason.append("\"").append(commonConcepts.get(i)).append("\"");
                if (i < commonConcepts.size() - 1) {
                    reason.append(",");
                }
            }
            reason.append("],");
        }
        
        // 共同行业说明
        if (!commonIndustries.isEmpty()) {
            reason.append("\"commonIndustries\":[");
            for (int i = 0; i < commonIndustries.size(); i++) {
                reason.append("\"").append(commonIndustries.get(i)).append("\"");
                if (i < commonIndustries.size() - 1) {
                    reason.append(",");
                }
            }
            reason.append("],");
        }
        
        // 生成自然语言描述
        String naturalLanguage = generateNaturalLanguageDescription(
            top5StocksA, top5StocksB, commonConcepts, commonIndustries
        );
        reason.append("\"description\":\"").append(naturalLanguage).append("\"");
        
        reason.append("}");
        return reason.toString();
    }

    /**
     * 生成自然语言格式的关联原因描述
     * 格式：贵州：朗玛信息<br/>江西：洪都航空<br/>关联原因：行业分布、地域特征
     */
    private String generateNaturalLanguageDescription(List<InvestmentInfo> stocksA,
                                                      List<InvestmentInfo> stocksB,
                                                      List<String> commonConcepts,
                                                      List<String> commonIndustries) {
        StringBuilder desc = new StringBuilder();
        
        // 获取两个省份的代表股票（各取前3个）
        List<InvestmentInfo> top3A = stocksA.stream().limit(3).collect(java.util.stream.Collectors.toList());
        List<InvestmentInfo> top3B = stocksB.stream().limit(3).collect(java.util.stream.Collectors.toList());
        
        // 获取省份名称（从第一只股票获取）
        String provinceA = !top3A.isEmpty() ? top3A.get(0).getProvince() : "A省";
        String provinceB = !top3B.isEmpty() ? top3B.get(0).getProvince() : "B省";
        
        // 转换为完整省份名
        String provinceAFull = convertProvinceToFullName(provinceA);
        String provinceBFull = convertProvinceToFullName(provinceB);
        
        // 第一行：省份A：股票名称
        if (!top3A.isEmpty()) {
            desc.append(provinceAFull).append("：");
            for (int i = 0; i < top3A.size(); i++) {
                desc.append(top3A.get(i).getCompanyName());
                if (i < top3A.size() - 1) {
                    desc.append("、");
                }
            }
            desc.append("<br/>");
        }
        
        // 第二行：省份B：股票名称
        if (!top3B.isEmpty()) {
            desc.append(provinceBFull).append("：");
            for (int i = 0; i < top3B.size(); i++) {
                desc.append(top3B.get(i).getCompanyName());
                if (i < top3B.size() - 1) {
                    desc.append("、");
                }
            }
            desc.append("<br/>");
        }
        
        // 第三行：关联原因
        desc.append("关联原因：");
        
        // 优先使用共同概念题材
        if (!commonConcepts.isEmpty()) {
            int count = Math.min(commonConcepts.size(), 3);
            for (int i = 0; i < count; i++) {
                desc.append(commonConcepts.get(i));
                if (i < count - 1) {
                    desc.append("、");
                }
            }
            if (commonConcepts.size() > 3) {
                desc.append("等");
            }
        }
        // 如果没有共同概念，使用共同行业
        else if (!commonIndustries.isEmpty()) {
            int count = Math.min(commonIndustries.size(), 2);
            for (int i = 0; i < count; i++) {
                desc.append(commonIndustries.get(i));
                if (i < count - 1) {
                    desc.append("、");
                }
            }
            if (commonIndustries.size() > 2) {
                desc.append("等");
            }
        }
        // 如果都没有，使用默认描述
        else {
            desc.append("行业分布、地域特征");
        }
        
        return desc.toString();
    }

    /**
     * 将省份简称转换为完整名称
     */
    private String convertProvinceToFullName(String abbreviation) {
        if (abbreviation == null || abbreviation.isEmpty()) {
            return "未知省份";
        }
        
        // 省份简称映射表
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
     * 生成股票信息的JSON字符串
     */
    private String generateStocksJson(List<InvestmentInfo> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < stocks.size(); i++) {
            InvestmentInfo stock = stocks.get(i);
            json.append("{");
            json.append("\"id\":").append(stock.getId()).append(",");
            json.append("\"companyName\":\"").append(stock.getCompanyName()).append("\",");
            json.append("\"industry\":\"").append(stock.getIndustry()).append("\",");
            json.append("\"tenDayChange\":\"").append(stock.getTenDayChange()).append("%\",");
            json.append("\"concepts\":\"").append(stock.getTitle() != null ? stock.getTitle() : "").append("\"");
            json.append("}");
            if (i < stocks.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
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
