-- 创建表
CREATE TABLE IF NOT EXISTS `investment_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) DEFAULT NULL COMMENT '题材概念',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `province_code` varchar(64) DEFAULT NULL COMMENT '省份编码',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `company_name` varchar(255) DEFAULT NULL COMMENT '股票名称',
  `industry` varchar(64) DEFAULT NULL COMMENT '股票代码',
  `investment_amount` decimal(20,2) DEFAULT NULL COMMENT '当前价格',
  `investment_type` varchar(64) DEFAULT NULL COMMENT '市盈率',
  `ten_day_change` decimal(10,2) DEFAULT NULL COMMENT '近十日总涨幅',
  `one_day_change` decimal(10,2) DEFAULT NULL COMMENT '当日涨跌幅',
  `volume` bigint(20) DEFAULT NULL COMMENT '成交量',
  `turnover` decimal(20,2) DEFAULT NULL COMMENT '成交额',
  `market_cap` decimal(20,2) DEFAULT NULL COMMENT '市值',
  `description` text COMMENT '公司简介',
  `related_provinces` varchar(512) DEFAULT NULL COMMENT '关联省份',
  `main_concepts` varchar(512) DEFAULT NULL COMMENT '主要概念题材',
  `theme_tags` varchar(512) DEFAULT NULL COMMENT '题材标签',
  `rising_reason` text COMMENT '上涨原因说明',
  `investment_date` date DEFAULT NULL COMMENT '数据日期',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股票信息表';

-- 插入模拟数据
INSERT INTO `investment_info` (`title`, `province`, `province_code`, `city`, `company_name`, `industry`, `investment_amount`, `investment_type`, `ten_day_change`, `one_day_change`, `volume`, `turnover`, `market_cap`, `description`, `related_provinces`, `main_concepts`, `theme_tags`, `rising_reason`, `investment_date`, `update_time`, `status`, `create_time`) VALUES
('新能源', '福建', '350000', '宁德', '宁德时代', '300750', 450.00, '50.5', 15.20, 2.50, 100000, 45000000.00, 1000000000.00, '全球领先的动力电池企业', '江苏,四川', '锂电池,新能源汽车', '行业龙头,高成长', '业绩超预期', CURDATE(), NOW(), 1, NOW()),
('新能源', '广东', '440000', '深圳', '比亚迪', '002594', 280.00, '45.2', 12.50, 1.80, 80000, 22400000.00, 800000000.00, '新能源汽车领导者', '湖南,陕西', '新能源汽车,刀片电池', '全产业链,销量冠军', '销量创新高', CURDATE(), NOW(), 1, NOW()),
('光伏', '陕西', '610000', '西安', '隆基绿能', '601012', 50.00, '20.1', 8.50, 0.50, 120000, 6000000.00, 300000000.00, '单晶硅片龙头', '云南,宁夏', '光伏,单晶硅', '成本优势,技术领先', '行业需求回暖', CURDATE(), NOW(), 1, NOW()),
('白酒', '贵州', '520000', '遵义', '贵州茅台', '600519', 1800.00, '30.5', 5.20, 0.80, 5000, 9000000.00, 2000000000.00, '高端白酒龙头', '北京,上海', '白酒,消费', '品牌护城河,高分红', '提价预期', CURDATE(), NOW(), 1, NOW()),
('互联网', '浙江', '330000', '杭州', '海康威视', '002415', 35.00, '18.5', -2.50, -0.50, 60000, 2100000.00, 300000000.00, '安防监控龙头', '重庆,湖北', '安防,人工智能', '数字化转型,稳健增长', '估值修复', CURDATE(), NOW(), 1, NOW()),
('医药', '江苏', '320000', '连云港', '恒瑞医药', '600276', 45.00, '40.2', 6.80, 1.20, 40000, 1800000.00, 280000000.00, '创新药龙头', '上海,美国', '创新药,抗肿瘤', '研发投入大,产品管线丰富', '新药获批', CURDATE(), NOW(), 1, NOW()),
('家电', '广东', '440000', '佛山', '美的集团', '000333', 60.00, '12.5', 4.50, 0.60, 50000, 3000000.00, 400000000.00, '家电巨头', '安徽,湖北', '家电,机器人', '全球化布局,多元化', '原材料成本下降', CURDATE(), NOW(), 1, NOW()),
('银行', '北京', '110000', '北京', '招商银行', '600036', 32.00, '6.5', 3.20, 0.40, 150000, 4800000.00, 800000000.00, '零售银行之王', '广东,上海', '银行,金融科技', '优质资产,高ROE', '息差改善', CURDATE(), NOW(), 1, NOW()),
('券商', '上海', '310000', '上海', '中信证券', '600030', 22.00, '15.2', 7.50, 1.50, 100000, 2200000.00, 300000000.00, '券商龙头', '北京,深圳', '证券,投行', '综合实力强,头部效应', '市场活跃度提升', CURDATE(), NOW(), 1, NOW()),
('半导体', '上海', '310000', '上海', '中芯国际', '688981', 55.00, '60.5', 9.80, 2.10, 70000, 3850000.00, 400000000.00, '晶圆代工龙头', '北京,天津', '半导体,芯片', '国产替代,先进制程', '产能扩张', CURDATE(), NOW(), 1, NOW());