DROP DATABASE IF EXISTS stock_investment;
CREATE DATABASE stock_investment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stock_investment;

-- 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 投资信息表
CREATE TABLE `investment_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '投资标题',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `province_code` VARCHAR(10) NOT NULL COMMENT '省份编码',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `company_name` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `industry` VARCHAR(100) DEFAULT NULL COMMENT '行业',
  `investment_amount` DECIMAL(15,2) DEFAULT NULL COMMENT '投资金额（万元）',
  `investment_type` VARCHAR(50) DEFAULT NULL COMMENT '投资类型',
  `description` TEXT COMMENT '投资描述',
  `related_provinces` VARCHAR(500) DEFAULT NULL COMMENT '关联省份（逗号分隔）',
  `investment_date` DATE DEFAULT NULL COMMENT '投资日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-无效，1-有效',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_province` (`province`),
  KEY `idx_province_code` (`province_code`),
  KEY `idx_investment_date` (`investment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投资信息表';

-- 初始化用户数据（密码为123456的MD5加密）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@example.com', 1),
('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'test@example.com', 1);

-- 初始化投资信息数据
INSERT INTO `investment_info` (`title`, `province`, `province_code`, `city`, `company_name`, `industry`, `investment_amount`, `investment_type`, `description`, `related_provinces`, `investment_date`) VALUES
('北京科技园区AI项目', '京', '110000', '北京市', '智能科技有限公司', '人工智能', 50000.00, '股权投资', '投资AI芯片研发项目，预计3年内实现量产', '沪,粤,浙', '2026-01-15'),
('上海金融科技创新基地', '沪', '310000', '上海市', '金融科技集团', '金融科技', 80000.00, '战略投资', '打造金融科技创新中心，服务长三角地区', '浙,苏,京', '2026-01-20'),
('广东新能源汽车产业园', '粤', '440000', '深圳市', '新能源汽车股份', '新能源', 120000.00, '产业投资', '建设新能源汽车生产基地，年产能50万辆', '湘,赣,闽', '2026-01-18'),
('浙江数字经济示范区', '浙', '330000', '杭州市', '数字经济发展公司', '数字经济', 60000.00, '政府引导', '推动数字经济发展，打造智慧城市样板', '沪,苏,皖', '2026-01-22'),
('江苏先进制造业基地', '苏', '320000', '苏州市', '智能制造集团', '智能制造', 90000.00, '股权投资', '建设智能制造产业基地，引进高端装备制造', '沪,浙,皖', '2026-01-25'),
('四川半导体产业园', '川', '510000', '成都市', '半导体科技公司', '半导体', 70000.00, '产业投资', '打造西部半导体产业高地，填补产业空白', '渝,陕,贵', '2026-01-12'),
('湖北生物医药创新中心', '鄂', '420000', '武汉市', '生物医药研究院', '生物医药', 55000.00, '研发投资', '建设生物医药研发中心，推动创新药物研发', '湘,豫,皖', '2026-01-28'),
('山东海洋经济示范区', '鲁', '370000', '青岛市', '海洋产业集团', '海洋经济', 65000.00, '战略投资', '发展海洋经济，打造蓝色经济示范区', '苏,冀,辽', '2026-01-10'),
('河南现代物流枢纽', '豫', '410000', '郑州市', '现代物流公司', '物流', 45000.00, '基础设施', '建设中部地区物流枢纽，提升物流效率', '鄂,鲁,陕', '2026-01-16'),
('陕西航空航天产业基地', '陕', '610000', '西安市', '航空航天集团', '航空航天', 100000.00, '国家战略', '发展航空航天产业，提升国防科技实力', '川,甘,豫', '2026-01-14'),
('福建海峡两岸科技园', '闽', '350000', '厦门市', '两岸科技公司', '高新技术', 40000.00, '区域合作', '促进两岸科技交流，打造创新创业平台', '粤,浙,赣', '2026-01-26'),
('湖南先进轨道交通装备', '湘', '430000', '长沙市', '轨道交通集团', '轨道交通', 75000.00, '产业投资', '发展先进轨道交通装备，引领行业发展', '粤,鄂,赣', '2026-01-19'),
('安徽新型显示产业集群', '皖', '340000', '合肥市', '显示技术公司', '显示技术', 58000.00, '股权投资', '打造新型显示产业集群，提升产业竞争力', '苏,浙,赣', '2026-01-21'),
('重庆智能网联汽车基地', '渝', '500000', '重庆市', '智能汽车科技', '智能汽车', 68000.00, '产业投资', '建设智能网联汽车测试基地，推动产业发展', '川,贵,鄂', '2026-01-17'),
('辽宁装备制造业升级', '辽', '210000', '沈阳市', '装备制造集团', '装备制造', 52000.00, '产业升级', '推动装备制造业转型升级，振兴东北经济', '吉,黑,冀', '2026-01-23'),
('北京量子通信研发中心', '京', '110000', '北京市', '量子科技研究院', '量子科技', 85000.00, '研发投资', '建设量子通信研发中心，抢占科技制高点', '沪,皖,粤', '2026-01-11'),
('上海集成电路产业园', '沪', '310000', '上海市', '集成电路公司', '集成电路', 95000.00, '产业投资', '打造集成电路产业高地，实现自主可控', '苏,浙,京', '2026-01-13'),
('广东生物科技创新园', '粤', '440000', '广州市', '生物科技集团', '生物科技', 62000.00, '创新投资', '建设生物科技创新园，推动生物产业发展', '闽,湘,赣', '2026-01-24'),
('浙江智能家居产业基地', '浙', '330000', '宁波市', '智能家居公司', '智能家居', 48000.00, '产业投资', '打造智能家居产业基地，引领行业标准', '沪,苏,闽', '2026-01-27'),
('江苏新材料产业园', '苏', '320000', '南京市', '新材料科技', '新材料', 72000.00, '股权投资', '发展新材料产业，支撑高端制造业发展', '沪,浙,皖', '2026-01-29'),
('天津智慧港口建设项目', '津', '120000', '天津市', '智慧港口集团', '智慧物流', 88000.00, '基础设施', '建设智能化港口，提升物流效率和服务水平', '冀,京,鲁', '2025-12-15'),
('河北绿色能源示范基地', '冀', '130000', '石家庄市', '绿色能源公司', '清洁能源', 66000.00, '产业投资', '发展风能、太阳能等清洁能源，助力碳中和目标', '津,晋,蒙', '2025-12-20'),
('山西煤炭清洁利用项目', '晋', '140000', '太原市', '清洁煤炭集团', '能源转型', 78000.00, '技术改造', '推动煤炭清洁高效利用，实现能源转型升级', '陕,冀,蒙', '2025-12-18'),
('内蒙古稀土产业园', '蒙', '150000', '包头市', '稀土科技公司', '稀土产业', 92000.00, '战略投资', '打造稀土产业链，提升资源利用效率', '晋,宁,甘', '2025-12-22'),
('吉林生物育种研发中心', '吉', '220000', '长春市', '生物育种研究院', '现代农业', 42000.00, '研发投资', '开展生物育种技术研发，保障粮食安全', '黑,辽,蒙', '2025-12-25'),
('黑龙江现代农业示范区', '黑', '230000', '哈尔滨市', '现代农业集团', '智慧农业', 56000.00, '产业投资', '建设智慧农业示范区，推动农业现代化', '吉,蒙,辽', '2025-12-12'),
('江西航空产业基地', '赣', '360000', '南昌市', '航空制造公司', '航空制造', 64000.00, '产业投资', '发展航空制造产业，打造中部航空产业高地', '湘,闽,皖', '2025-12-28'),
('贵州大数据产业园', '贵', '520000', '贵阳市', '大数据科技公司', '大数据', 54000.00, '数字经济', '建设大数据产业园，打造数据中心集群', '川,云,渝', '2025-12-10'),
('云南生物多样性保护项目', '云', '530000', '昆明市', '生态保护基金', '生态环保', 38000.00, '公益投资', '保护生物多样性，推动生态文明建设', '贵,川,藏', '2025-12-16'),
('西藏清洁能源开发', '藏', '540000', '拉萨市', '清洁能源开发公司', '水电能源', 82000.00, '能源开发', '开发水电、光伏等清洁能源，服务全国电网', '青,川,云', '2025-12-19'),
('甘肃新能源装备制造', '甘', '620000', '兰州市', '新能源装备公司', '装备制造', 46000.00, '产业投资', '生产风电、光伏装备，支撑新能源发展', '青,宁,陕', '2025-12-21'),
('青海盐湖资源综合利用', '青', '630000', '西宁市', '盐湖资源公司', '资源开发', 74000.00, '资源利用', '综合开发盐湖资源，提取锂、钾等战略资源', '甘,藏,新', '2025-12-23'),
('宁夏葡萄酒产业升级', '宁', '640000', '银川市', '葡萄酒产业集团', '特色农业', 32000.00, '产业升级', '打造高端葡萄酒品牌，发展特色农业', '甘,陕,蒙', '2025-12-26'),
('新疆棉纺织产业园', '新', '650000', '乌鲁木齐市', '棉纺织集团', '纺织产业', 58000.00, '产业投资', '建设现代化棉纺织产业园，延伸产业链', '青,甘', '2025-12-11'),
('海南自贸港数字贸易中心', '琼', '460000', '海口市', '数字贸易公司', '数字贸易', 76000.00, '自贸港建设', '打造数字贸易中心，服务自贸港建设', '粤,桂,闽', '2025-12-13'),
('广西东盟合作产业园', '桂', '450000', '南宁市', '东盟合作集团', '国际贸易', 62000.00, '区域合作', '建设面向东盟的产业合作园区，促进贸易往来', '粤,云,贵', '2025-12-17'),
('北京生命科学园区', '京', '110000', '北京市', '生命科学研究院', '生命科学', 96000.00, '研发投资', '建设世界级生命科学研究中心，推动医疗创新', '津,冀,沪', '2025-12-24'),
('上海国际航运中心', '沪', '310000', '上海市', '国际航运集团', '航运物流', 110000.00, '战略投资', '打造国际航运中心，提升全球航运枢纽地位', '浙,苏,闽', '2025-12-14'),
('广东粤港澳大湾区科创中心', '粤', '440000', '深圳市', '大湾区科创公司', '科技创新', 128000.00, '区域协同', '建设粤港澳大湾区科技创新中心，引领区域发展', '港,澳,闽', '2025-12-27'),
('浙江跨境电商产业园', '浙', '330000', '杭州市', '跨境电商集团', '电子商务', 52000.00, '数字贸易', '打造跨境电商产业园，推动外贸转型升级', '沪,苏,闽', '2025-12-29'),
('江苏工业互联网平台', '苏', '320000', '无锡市', '工业互联网公司', '工业互联网', 68000.00, '数字化转型', '建设工业互联网平台，赋能制造业数字化转型', '沪,浙,鲁', '2026-01-05'),
('四川氢能产业基地', '川', '510000', '成都市', '氢能科技公司', '氢能源', 84000.00, '新能源', '发展氢能产业，打造氢能全产业链', '渝,云,贵', '2026-01-08'),
('湖北光电子产业园', '鄂', '420000', '武汉市', '光电子科技公司', '光电子', '72000.00', '产业投资', '建设光电子产业园，打造光谷升级版', '湘,豫,赣', '2026-01-06'),
('山东海洋牧场示范区', '鲁', '370000', '烟台市', '海洋牧场公司', '海洋渔业', 44000.00, '生态渔业', '建设现代化海洋牧场，发展生态渔业', '苏,辽,冀', '2026-01-09'),
('河南智能农机产业园', '豫', '410000', '洛阳市', '智能农机集团', '农业装备', 50000.00, '产业投资', '生产智能农机装备，推动农业机械化', '鲁,皖,陕', '2026-01-03'),
('陕西文化旅游产业基地', '陕', '610000', '西安市', '文旅产业集团', '文化旅游', 36000.00, '文旅融合', '打造文化旅游产业基地，传承历史文化', '甘,豫,川', '2026-01-07'),
('福建海洋新材料产业园', '闽', '350000', '福州市', '海洋新材料公司', '海洋材料', '48000.00', '产业投资', '开发海洋新材料，拓展海洋经济新领域', '浙,粤,赣', '2026-01-04'),
('湖南工程机械智能制造', '湘', '430000', '长沙市', '工程机械集团', '工程机械', 86000.00, '智能制造', '推动工程机械智能化升级，保持行业领先', '粤,赣,鄂', '2026-01-02'),
('安徽量子信息产业园', '皖', '340000', '合肥市', '量子信息公司', '量子信息', 94000.00, '前沿科技', '建设量子信息产业园，抢占量子科技制高点', '苏,浙,沪', '2026-01-01');

-- 省份相关度表
CREATE TABLE `related_provinces` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_province` VARCHAR(10) NOT NULL COMMENT '源省份（简称）',
  `source_province_code` VARCHAR(10) NOT NULL COMMENT '源省份编码',
  `target_province` VARCHAR(10) NOT NULL COMMENT '目标省份（简称）',
  `target_province_code` VARCHAR(10) NOT NULL COMMENT '目标省份编码',
  `correlation_score` DECIMAL(10,6) DEFAULT 0 COMMENT '相关度得分（0-1之间）',
  `joint_project_count` INT DEFAULT 0 COMMENT '关联项目数量',
  `source_invest_count` INT DEFAULT 0 COMMENT '源省份投资次数',
  `target_invest_count` INT DEFAULT 0 COMMENT '目标省份投资次数',
  `total_investment_amount` DECIMAL(18,2) DEFAULT 0 COMMENT '关联项目总投资金额（万元）',
  `industry_similarity` DECIMAL(10,6) DEFAULT 0 COMMENT '行业相似度（0-1之间）',
  `investment_type_similarity` DECIMAL(10,6) DEFAULT 0 COMMENT '投资类型相似度（0-1之间）',
  `geographic_distance` INT DEFAULT NULL COMMENT '地理距离（千米）',
  `related_project_ids` TEXT DEFAULT NULL COMMENT '关联项目ID列表（JSON数组）',
  `common_industries` VARCHAR(500) DEFAULT NULL COMMENT '共同行业（逗号分隔）',
  `common_investment_types` VARCHAR(500) DEFAULT NULL COMMENT '共同投资类型（逗号分隔）',
  `calculation_method` VARCHAR(50) DEFAULT 'DEFAULT' COMMENT '计算方法标识',
  `weight_config` VARCHAR(200) DEFAULT NULL COMMENT '权重配置（JSON）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_province_pair` (`source_province`, `target_province`),
  KEY `idx_source_province` (`source_province`),
  KEY `idx_target_province` (`target_province`),
  KEY `idx_correlation_score` (`correlation_score`),
  KEY `idx_joint_project_count` (`joint_project_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='省份相关度表';

-- 省份相关度历史表（用于追踪相关度变化）
CREATE TABLE `related_provinces_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `relation_id` BIGINT NOT NULL COMMENT '关联的related_provinces表ID',
  `source_province` VARCHAR(10) NOT NULL COMMENT '源省份（简称）',
  `target_province` VARCHAR(10) NOT NULL COMMENT '目标省份（简称）',
  `correlation_score` DECIMAL(10,6) DEFAULT 0 COMMENT '相关度得分',
  `joint_project_count` INT DEFAULT 0 COMMENT '关联项目数量',
  `total_investment_amount` DECIMAL(18,2) DEFAULT 0 COMMENT '总投资金额',
  `change_type` VARCHAR(20) DEFAULT NULL COMMENT '变化类型：INIT-初始化，UPDATE-更新，RECALC-重新计算',
  `calculation_method` VARCHAR(50) DEFAULT NULL COMMENT '计算方法标识',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='省份相关度历史表';

-- 省份维度相关度表（用于存储不同维度的相关度）
CREATE TABLE `related_provinces_dimension` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `relation_id` BIGINT NOT NULL COMMENT '关联的related_provinces表ID',
  `source_province` VARCHAR(10) NOT NULL COMMENT '源省份（简称）',
  `target_province` VARCHAR(10) NOT NULL COMMENT '目标省份（简称）',
  `dimension_type` VARCHAR(50) NOT NULL COMMENT '维度类型：INDUSTRY-行业，INVESTMENT_TYPE-投资类型，GEOGRAPHY-地理，AMOUNT-金额等',
  `dimension_value` VARCHAR(100) NOT NULL COMMENT '维度值（如具体行业名称）',
  `score` DECIMAL(10,6) DEFAULT 0 COMMENT '该维度的相关度得分',
  `count` INT DEFAULT 0 COMMENT '该维度的关联数量',
  `amount` DECIMAL(18,2) DEFAULT 0 COMMENT '该维度的金额（万元）',
  `weight` DECIMAL(5,4) DEFAULT 1 COMMENT '该维度的权重',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_dimension_type` (`dimension_type`),
  UNIQUE KEY `uk_relation_dimension_value` (`relation_id`, `dimension_type`, `dimension_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='省份维度相关度表';

-- 省份地理距离表（存储省份间的地理距离）
CREATE TABLE `province_distance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `province_a` VARCHAR(10) NOT NULL COMMENT '省份A（简称）',
  `province_a_code` VARCHAR(10) NOT NULL COMMENT '省份A编码',
  `province_b` VARCHAR(10) NOT NULL COMMENT '省份B（简称）',
  `province_b_code` VARCHAR(10) NOT NULL COMMENT '省份B编码',
  `distance_km` INT DEFAULT NULL COMMENT '距离（千米）',
  `is_neighbor` TINYINT DEFAULT 0 COMMENT '是否相邻：0-否，1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_province_pair` (`province_a`, `province_b`),
  KEY `idx_is_neighbor` (`is_neighbor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='省份地理距离表';

-- 初始化省份地理距离数据（部分示例数据）
INSERT INTO `province_distance` (`province_a`, `province_a_code`, `province_b`, `province_b_code`, `distance_km`, `is_neighbor`) VALUES
('京', '110000', '津', '120000', 120, 1),
('京', '110000', '冀', '130000', 280, 1),
('沪', '310000', '苏', '320000', 300, 1),
('沪', '310000', '浙', '330000', 180, 1),
('苏', '320000', '浙', '330000', 250, 1),
('苏', '320000', '皖', '340000', 280, 1),
('浙', '330000', '皖', '340000', 350, 1),
('粤', '440000', '湘', '430000', 620, 1),
('粤', '440000', '赣', '360000', 520, 1),
('粤', '440000', '闽', '350000', 780, 1),
('川', '510000', '渝', '500000', 330, 1),
('川', '510000', '陕', '610000', 650, 1),
('川', '510000', '黔', '520000', 750, 1),
('鄂', '420000', '湘', '430000', 350, 1),
('鄂', '420000', '赣', '360000', 370, 1),
('鄂', '420000', '皖', '340000', 450, 1),
('鄂', '420000', '豫', '410000', 470, 1),
('鲁', '370000', '冀', '130000', 350, 1),
('鲁', '370000', '苏', '320000', 420, 1),
('鲁', '370000', '豫', '410000', 380, 1),
('豫', '410000', '冀', '130000', 420, 1),
('豫', '410000', '陕', '610000', 480, 1),
('豫', '410000', '皖', '340000', 420, 1),
('陕', '610000', '甘', '620000', 520, 1),
('陕', '610000', '晋', '140000', 480, 1),
('晋', '140000', '蒙', '150000', 580, 1),
('辽', '210000', '吉', '220000', 550, 1),
('辽', '210000', '蒙', '150000', 780, 1),
('吉', '220000', '黑', '230000', 460, 1),
('吉', '220000', '蒙', '150000', 920, 1),
('云', '530000', '黔', '520000', 680, 1),
('云', '530000', '川', '510000', 860, 1),
('云', '530000', '藏', '540000', 1600, 1),
('藏', '540000', '青', '630000', 1500, 1),
('藏', '540000', '川', '510000', 1200, 1),
('甘', '620000', '青', '630000', 780, 1),
('甘', '620000', '宁', '640000', 420, 1),
('甘', '620000', '蒙', '150000', 1400, 1),
('青', '630000', '宁', '640000', 520, 1),
('青', '630000', '新', '650000', 1800, 1),
('新', '650000', '甘', '620000', 1600, 1),
('新', '650000', '蒙', '150000', 2200, 1),
('桂', '450000', '粤', '440000', 560, 1),
('桂', '450000', '云', '530000', 980, 1),
('桂', '450000', '黔', '520000', 720, 1),
('琼', '460000', '粤', '440000', 580, 1),
('琼', '460000', '桂', '450000', 680, 1);

-- 创建相关度计算配置表
CREATE TABLE `correlation_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dimension_type` VARCHAR(50) NOT NULL COMMENT '维度类型',
  `dimension_name` VARCHAR(100) NOT NULL COMMENT '维度名称',
  `weight` DECIMAL(5,4) DEFAULT 1 COMMENT '权重',
  `algorithm` VARCHAR(100) DEFAULT NULL COMMENT '计算算法',
  `algorithm_params` TEXT DEFAULT NULL COMMENT '算法参数（JSON）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dimension_type` (`dimension_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相关度计算配置表';

-- 初始化相关度计算配置
INSERT INTO `correlation_config` (`dimension_type`, `dimension_name`, `weight`, `algorithm`, `algorithm_params`, `status`) VALUES
('JOINT_PROJECT', '共同项目', 0.4, 'JACCARD_SIMILARITY', '{"threshold": 0.1}', 1),
('INDUSTRY', '行业相似度', 0.2, 'COSINE_SIMILARITY', '{"min_count": 2}', 1),
('INVESTMENT_TYPE', '投资类型相似度', 0.15, 'JACCARD_SIMILARITY', NULL, 1),
('GEOGRAPHY', '地理距离', 0.1, 'INVERSE_DISTANCE', '{"scale": 2000, "decay": 0.5}', 1),
('INVESTMENT_AMOUNT', '投资金额', 0.15, 'NORMALIZED_VALUE', NULL, 1);