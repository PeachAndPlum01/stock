-- 股票公司信息表
-- 用于存储Tushare stock_company接口获取的公司数据
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS stock_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stock_db;

-- 删除已存在的表（谨慎使用，仅在开发环境使用）
-- DROP TABLE IF EXISTS stock_company;

-- 创建股票公司信息表
CREATE TABLE IF NOT EXISTS stock_company (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    ts_code VARCHAR(20) NOT NULL UNIQUE COMMENT '股票代码（如：688981.SH）',
    com_name VARCHAR(100) COMMENT '公司名称',
    chairman VARCHAR(50) COMMENT '董事长',
    manager VARCHAR(50) COMMENT '总经理',
    secretary VARCHAR(50) COMMENT '董秘',
    reg_capital DECIMAL(18, 4) COMMENT '注册资本（万元）',
    setup_date DATE COMMENT '成立日期',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    office VARCHAR(500) COMMENT '办公地址',
    introduction TEXT COMMENT '公司简介',
    website VARCHAR(200) COMMENT '公司网站',
    email VARCHAR(100) COMMENT '邮箱',
    business_scope TEXT COMMENT '经营范围',
    employees INT COMMENT '员工人数',
    main_business TEXT COMMENT '主营业务',
    exchange VARCHAR(10) COMMENT '交易所（SSE上交所, SZSE深交所）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    data_source VARCHAR(20) DEFAULT 'tushare' COMMENT '数据来源',
    PRIMARY KEY (id),
    INDEX idx_ts_code (ts_code),
    INDEX idx_com_name (com_name),
    INDEX idx_city (city),
    INDEX idx_province (province),
    INDEX idx_exchange (exchange),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='股票公司信息表';

-- 插入示例数据（可选）
-- INSERT INTO stock_company (
--     ts_code, com_name, chairman, secretary, reg_capital, setup_date,
--     city, office, introduction, website, email, employees, exchange
-- ) VALUES (
--     '688981.SH',
--     '中芯国际集成电路制造有限公司',
--     '刘训峰',
--     '郭光莉',
--     800028.4472,
--     '2004-02-26',
--     'Grand Cayman',
--     '上海市浦东新区张江路18号',
--     '世界领先的集成电路晶圆代工企业',
--     'www.smics.com',
--     'ir@smics.com',
--     19186,
--     'SSE'
-- );

-- 查询示例
-- SELECT ts_code, com_name, city, office, website FROM stock_company WHERE city = '上海';
-- SELECT * FROM stock_company WHERE exchange = 'SSE' LIMIT 10;
-- SELECT * FROM stock_company WHERE com_name LIKE '%芯片%';
