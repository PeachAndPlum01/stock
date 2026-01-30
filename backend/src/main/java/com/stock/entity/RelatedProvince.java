package com.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 省份相关度实体类
 */
@TableName("related_provinces")
public class RelatedProvince implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 源省份（简称）
     */
    private String sourceProvince;

    /**
     * 源省份编码
     */
    private String sourceProvinceCode;

    /**
     * 目标省份（简称）
     */
    private String targetProvince;

    /**
     * 目标省份编码
     */
    private String targetProvinceCode;

    /**
     * 相关度得分（0-1之间）
     */
    private BigDecimal correlationScore;

    /**
     * 行业相似度（0-1之间）
     */
    private BigDecimal industrySimilarity;

    /**
     * 概念题材相似度（0-1之间）
     */
    private BigDecimal conceptSimilarity;

    /**
     * 公司性质相似度（0-1之间）
     */
    private BigDecimal companyTypeSimilarity;

    /**
     * 共同概念题材（逗号分隔）
     */
    private String commonConcepts;

    /**
     * 共同行业（逗号分隔）
     */
    private String commonIndustries;

    /**
     * 关联原因说明（JSON格式）
     */
    private String correlationReason;

    /**
     * 源省份涨幅前5股票信息（JSON）
     */
    private String sourceTop5Stocks;

    /**
     * 目标省份涨幅前5股票信息（JSON）
     */
    private String targetTop5Stocks;

    /**
     * 计算方法标识
     */
    private String calculationMethod;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceProvince() {
        return sourceProvince;
    }

    public void setSourceProvince(String sourceProvince) {
        this.sourceProvince = sourceProvince;
    }

    public String getSourceProvinceCode() {
        return sourceProvinceCode;
    }

    public void setSourceProvinceCode(String sourceProvinceCode) {
        this.sourceProvinceCode = sourceProvinceCode;
    }

    public String getTargetProvince() {
        return targetProvince;
    }

    public void setTargetProvince(String targetProvince) {
        this.targetProvince = targetProvince;
    }

    public String getTargetProvinceCode() {
        return targetProvinceCode;
    }

    public void setTargetProvinceCode(String targetProvinceCode) {
        this.targetProvinceCode = targetProvinceCode;
    }

    public BigDecimal getCorrelationScore() {
        return correlationScore;
    }

    public void setCorrelationScore(BigDecimal correlationScore) {
        this.correlationScore = correlationScore;
    }

    public BigDecimal getIndustrySimilarity() {
        return industrySimilarity;
    }

    public void setIndustrySimilarity(BigDecimal industrySimilarity) {
        this.industrySimilarity = industrySimilarity;
    }

    public BigDecimal getConceptSimilarity() {
        return conceptSimilarity;
    }

    public void setConceptSimilarity(BigDecimal conceptSimilarity) {
        this.conceptSimilarity = conceptSimilarity;
    }

    public BigDecimal getCompanyTypeSimilarity() {
        return companyTypeSimilarity;
    }

    public void setCompanyTypeSimilarity(BigDecimal companyTypeSimilarity) {
        this.companyTypeSimilarity = companyTypeSimilarity;
    }

    public String getCommonConcepts() {
        return commonConcepts;
    }

    public void setCommonConcepts(String commonConcepts) {
        this.commonConcepts = commonConcepts;
    }

    public String getCommonIndustries() {
        return commonIndustries;
    }

    public void setCommonIndustries(String commonIndustries) {
        this.commonIndustries = commonIndustries;
    }

    public String getCorrelationReason() {
        return correlationReason;
    }

    public void setCorrelationReason(String correlationReason) {
        this.correlationReason = correlationReason;
    }

    public String getSourceTop5Stocks() {
        return sourceTop5Stocks;
    }

    public void setSourceTop5Stocks(String sourceTop5Stocks) {
        this.sourceTop5Stocks = sourceTop5Stocks;
    }

    public String getTargetTop5Stocks() {
        return targetTop5Stocks;
    }

    public void setTargetTop5Stocks(String targetTop5Stocks) {
        this.targetTop5Stocks = targetTop5Stocks;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
