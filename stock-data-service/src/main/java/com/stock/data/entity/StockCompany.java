package com.stock.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 股票公司信息实体类
 * 对应Tushare的stock_company接口数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_company", indexes = {
        @Index(name = "idx_ts_code", columnList = "ts_code"),
        @Index(name = "idx_com_name", columnList = "com_name")
})
public class StockCompany {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 股票代码（如：688981.SH）
     */
    @Column(name = "ts_code", nullable = false, length = 20, unique = true)
    private String tsCode;

    /**
     * 公司名称
     */
    @Column(name = "com_name", nullable = false, length = 100)
    private String comName;

    /**
     * 董事长
     */
    @Column(name = "chairman", length = 50)
    private String chairman;

    /**
     * 总经理
     */
    @Column(name = "manager", length = 50)
    private String manager;

    /**
     * 董秘
     */
    @Column(name = "secretary", length = 50)
    private String secretary;

    /**
     * 注册资本（万元）
     */
    @Column(name = "reg_capital", precision = 18, scale = 4)
    private BigDecimal regCapital;

    /**
     * 成立日期
     */
    @Column(name = "setup_date")
    private LocalDate setupDate;

    /**
     * 省份
     */
    @Column(name = "province", length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(name = "city", length = 50)
    private String city;

    /**
     * 办公地址
     */
    @Column(name = "office", length = 500)
    private String office;

    /**
     * 公司简介
     */
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    /**
     * 公司网站
     */
    @Column(name = "website", length = 200)
    private String website;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 经营范围
     */
    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;

    /**
     * 员工人数
     */
    @Column(name = "employees")
    private Integer employees;

    /**
     * 主营业务
     */
    @Column(name = "main_business", columnDefinition = "TEXT")
    private String mainBusiness;

    /**
     * 交易所（SSE上交所, SZSE深交所）
     */
    @Column(name = "exchange", length = 10)
    private String exchange;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 数据来源
     */
    @Column(name = "data_source", length = 20)
    private String dataSource = "tushare";

    @PrePersist
    public void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}