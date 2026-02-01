package com.stock.data.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 股票公司信息实体类
 * 对应Tushare的stock_company接口数据
 */
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

    // Constructors
    public StockCompany() {
    }

    public StockCompany(Long id, String tsCode, String comName, String chairman, String manager,
                        String secretary, BigDecimal regCapital, LocalDate setupDate, String province,
                        String city, String office, String introduction, String website, String email,
                        String businessScope, Integer employees, String mainBusiness, String exchange,
                        LocalDateTime createTime, LocalDateTime updateTime, String dataSource) {
        this.id = id;
        this.tsCode = tsCode;
        this.comName = comName;
        this.chairman = chairman;
        this.manager = manager;
        this.secretary = secretary;
        this.regCapital = regCapital;
        this.setupDate = setupDate;
        this.province = province;
        this.city = city;
        this.office = office;
        this.introduction = introduction;
        this.website = website;
        this.email = email;
        this.businessScope = businessScope;
        this.employees = employees;
        this.mainBusiness = mainBusiness;
        this.exchange = exchange;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.dataSource = dataSource;
    }

    // Builder
    public static StockCompanyBuilder builder() {
        return new StockCompanyBuilder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getChairman() {
        return chairman;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public BigDecimal getRegCapital() {
        return regCapital;
    }

    public void setRegCapital(BigDecimal regCapital) {
        this.regCapital = regCapital;
    }

    public LocalDate getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(LocalDate setupDate) {
        this.setupDate = setupDate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public Integer getEmployees() {
        return employees;
    }

    public void setEmployees(Integer employees) {
        this.employees = employees;
    }

    public String getMainBusiness() {
        return mainBusiness;
    }

    public void setMainBusiness(String mainBusiness) {
        this.mainBusiness = mainBusiness;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
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

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    // Builder class
    public static class StockCompanyBuilder {
        private Long id;
        private String tsCode;
        private String comName;
        private String chairman;
        private String manager;
        private String secretary;
        private BigDecimal regCapital;
        private LocalDate setupDate;
        private String province;
        private String city;
        private String office;
        private String introduction;
        private String website;
        private String email;
        private String businessScope;
        private Integer employees;
        private String mainBusiness;
        private String exchange;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private String dataSource = "tushare";

        public StockCompanyBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StockCompanyBuilder tsCode(String tsCode) {
            this.tsCode = tsCode;
            return this;
        }

        public StockCompanyBuilder comName(String comName) {
            this.comName = comName;
            return this;
        }

        public StockCompanyBuilder chairman(String chairman) {
            this.chairman = chairman;
            return this;
        }

        public StockCompanyBuilder manager(String manager) {
            this.manager = manager;
            return this;
        }

        public StockCompanyBuilder secretary(String secretary) {
            this.secretary = secretary;
            return this;
        }

        public StockCompanyBuilder regCapital(BigDecimal regCapital) {
            this.regCapital = regCapital;
            return this;
        }

        public StockCompanyBuilder setupDate(LocalDate setupDate) {
            this.setupDate = setupDate;
            return this;
        }

        public StockCompanyBuilder province(String province) {
            this.province = province;
            return this;
        }

        public StockCompanyBuilder city(String city) {
            this.city = city;
            return this;
        }

        public StockCompanyBuilder office(String office) {
            this.office = office;
            return this;
        }

        public StockCompanyBuilder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public StockCompanyBuilder website(String website) {
            this.website = website;
            return this;
        }

        public StockCompanyBuilder email(String email) {
            this.email = email;
            return this;
        }

        public StockCompanyBuilder businessScope(String businessScope) {
            this.businessScope = businessScope;
            return this;
        }

        public StockCompanyBuilder employees(Integer employees) {
            this.employees = employees;
            return this;
        }

        public StockCompanyBuilder mainBusiness(String mainBusiness) {
            this.mainBusiness = mainBusiness;
            return this;
        }

        public StockCompanyBuilder exchange(String exchange) {
            this.exchange = exchange;
            return this;
        }

        public StockCompanyBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public StockCompanyBuilder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public StockCompanyBuilder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public StockCompany build() {
            return new StockCompany(id, tsCode, comName, chairman, manager, secretary,
                    regCapital, setupDate, province, city, office, introduction,
                    website, email, businessScope, employees, mainBusiness,
                    exchange, createTime, updateTime, dataSource);
        }
    }
}
