package com.stock.correlation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 省份相关度实体类
 */
@Data
@TableName("related_provinces")
public class RelatedProvince implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 概念题材相似度（0-1之间）
     */
    private BigDecimal conceptSimilarity;

    /**
     * 行业相似度（0-1之间）
     */
    private BigDecimal industrySimilarity;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
