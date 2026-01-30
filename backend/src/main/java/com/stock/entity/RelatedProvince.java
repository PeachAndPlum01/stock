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
     * 关联项目数量
     */
    private Integer jointProjectCount;

    /**
     * 源省份投资次数
     */
    private Integer sourceInvestCount;

    /**
     * 目标省份投资次数
     */
    private Integer targetInvestCount;

    /**
     * 关联项目总投资金额（万元）
     */
    private BigDecimal totalInvestmentAmount;

    /**
     * 行业相似度（0-1之间）
     */
    private BigDecimal industrySimilarity;

    /**
     * 投资类型相似度（0-1之间）
     */
    private BigDecimal investmentTypeSimilarity;

    /**
     * 地理距离（千米）
     */
    private Integer geographicDistance;

    /**
     * 关联项目ID列表（JSON数组）
     */
    private String relatedProjectIds;

    /**
     * 共同行业（逗号分隔）
     */
    private String commonIndustries;

    /**
     * 共同投资类型（逗号分隔）
     */
    private String commonInvestmentTypes;

    /**
     * 计算方法标识
     */
    private String calculationMethod;

    /**
     * 权重配置（JSON）
     */
    private String weightConfig;

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

    public Integer getJointProjectCount() {
        return jointProjectCount;
    }

    public void setJointProjectCount(Integer jointProjectCount) {
        this.jointProjectCount = jointProjectCount;
    }

    public Integer getSourceInvestCount() {
        return sourceInvestCount;
    }

    public void setSourceInvestCount(Integer sourceInvestCount) {
        this.sourceInvestCount = sourceInvestCount;
    }

    public Integer getTargetInvestCount() {
        return targetInvestCount;
    }

    public void setTargetInvestCount(Integer targetInvestCount) {
        this.targetInvestCount = targetInvestCount;
    }

    public BigDecimal getTotalInvestmentAmount() {
        return totalInvestmentAmount;
    }

    public void setTotalInvestmentAmount(BigDecimal totalInvestmentAmount) {
        this.totalInvestmentAmount = totalInvestmentAmount;
    }

    public BigDecimal getIndustrySimilarity() {
        return industrySimilarity;
    }

    public void setIndustrySimilarity(BigDecimal industrySimilarity) {
        this.industrySimilarity = industrySimilarity;
    }

    public BigDecimal getInvestmentTypeSimilarity() {
        return investmentTypeSimilarity;
    }

    public void setInvestmentTypeSimilarity(BigDecimal investmentTypeSimilarity) {
        this.investmentTypeSimilarity = investmentTypeSimilarity;
    }

    public Integer getGeographicDistance() {
        return geographicDistance;
    }

    public void setGeographicDistance(Integer geographicDistance) {
        this.geographicDistance = geographicDistance;
    }

    public String getRelatedProjectIds() {
        return relatedProjectIds;
    }

    public void setRelatedProjectIds(String relatedProjectIds) {
        this.relatedProjectIds = relatedProjectIds;
    }

    public String getCommonIndustries() {
        return commonIndustries;
    }

    public void setCommonIndustries(String commonIndustries) {
        this.commonIndustries = commonIndustries;
    }

    public String getCommonInvestmentTypes() {
        return commonInvestmentTypes;
    }

    public void setCommonInvestmentTypes(String commonInvestmentTypes) {
        this.commonInvestmentTypes = commonInvestmentTypes;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public String getWeightConfig() {
        return weightConfig;
    }

    public void setWeightConfig(String weightConfig) {
        this.weightConfig = weightConfig;
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
