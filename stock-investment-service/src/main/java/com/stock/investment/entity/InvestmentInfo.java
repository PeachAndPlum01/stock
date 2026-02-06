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
}
