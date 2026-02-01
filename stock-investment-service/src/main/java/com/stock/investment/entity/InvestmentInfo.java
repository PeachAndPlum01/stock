package com.stock.investment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 股票信息实体类
 */
@Data
@TableName("investment_info")
public class InvestmentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题材概念（如：人工智能、新能源等，逗号分隔）
     */
    private String title;

    /**
     * 省份（上市公司注册地，简称）
     */
    private String province;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市
     */
    private String city;

    /**
     * 股票名称
     */
    private String companyName;

    /**
     * 股票代码
     */
    private String industry;

    /**
     * 当前价格（元）
     */
    private BigDecimal investmentAmount;

    /**
     * 市盈率
     */
    private String investmentType;

    /**
     * 近十日总涨幅（%）
     */
    private BigDecimal tenDayChange;

    /**
     * 当日涨跌幅（%）
     */
    private BigDecimal oneDayChange;

    /**
     * 成交量（手）
     */
    private Long volume;

    /**
     * 成交额（元）
     */
    private BigDecimal turnover;

    /**
     * 市值（元）
     */
    private BigDecimal marketCap;

    /**
     * 公司简介
     */
    private String description;

    /**
     * 关联省份（逗号分隔）
     */
    private String relatedProvinces;

    /**
     * 主要概念题材（逗号分隔，前3个）
     */
    private String mainConcepts;

    /**
     * 题材标签（逗号分隔）
     */
    private String themeTags;

    /**
     * 上涨原因说明
     */
    private String risingReason;

    /**
     * 数据日期
     */
    private LocalDate investmentDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态：0-无效，1-有效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public BigDecimal getTenDayChange() {
        return tenDayChange;
    }

    public void setTenDayChange(BigDecimal tenDayChange) {
        this.tenDayChange = tenDayChange;
    }

    public BigDecimal getOneDayChange() {
        return oneDayChange;
    }

    public void setOneDayChange(BigDecimal oneDayChange) {
        this.oneDayChange = oneDayChange;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelatedProvinces() {
        return relatedProvinces;
    }

    public void setRelatedProvinces(String relatedProvinces) {
        this.relatedProvinces = relatedProvinces;
    }

    public String getMainConcepts() {
        return mainConcepts;
    }

    public void setMainConcepts(String mainConcepts) {
        this.mainConcepts = mainConcepts;
    }

    public String getThemeTags() {
        return themeTags;
    }

    public void setThemeTags(String themeTags) {
        this.themeTags = themeTags;
    }

    public String getRisingReason() {
        return risingReason;
    }

    public void setRisingReason(String risingReason) {
        this.risingReason = risingReason;
    }

    public LocalDate getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDate investmentDate) {
        this.investmentDate = investmentDate;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
