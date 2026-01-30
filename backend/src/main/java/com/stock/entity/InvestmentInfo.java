package com.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 投资信息实体类
 */
@TableName("investment_info")
public class InvestmentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 投资标题
     */
    private String title;

    /**
     * 省份
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
     * 公司名称
     */
    private String companyName;

    /**
     * 行业
     */
    private String industry;

    /**
     * 投资金额（万元）
     */
    private BigDecimal investmentAmount;

    /**
     * 投资类型
     */
    private String investmentType;

    /**
     * 近十日总涨幅（%）
     */
    private BigDecimal tenDayChange;

    /**
     * 投资描述
     */
    private String description;

    /**
     * 关联省份（逗号分隔）
     */
    private String relatedProvinces;

    /**
     * 投资日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate investmentDate;

    /**
     * 状态：0-无效，1-有效
     */
    private Integer status;

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

    public LocalDate getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(LocalDate investmentDate) {
        this.investmentDate = investmentDate;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
