# 项目结构说明

## 目录结构

```
stock/
├── README.md                    # 项目说明文档
├── DEPLOY.md                    # 部署文档
├── .gitignore                   # Git忽略文件
├── start.sh                     # 启动脚本
│
├── sql/                         # 数据库脚本
│   └── init.sql                 # 数据库初始化脚本
│
├── backend/                     # 后端项目
│   ├── pom.xml                  # Maven配置文件
│   └── src/
│       └── main/
│           ├── java/com/stock/
│           │   ├── StockApplication.java          # 启动类
│           │   ├── common/
│           │   │   └── Result.java                # 统一响应结果
│           │   ├── config/
│           │   │   ├── RabbitMQConfig.java        # RabbitMQ配置
│           │   │   ├── RedisConfig.java           # Redis配置
│           │   │   └── WebConfig.java             # Web配置（跨域）
│           │   ├── controller/
│           │   │   ├── AuthController.java        # 认证控制器
│           │   │   └── InvestmentController.java  # 投资信息控制器
│           │   ├── entity/
│           │   │   ├── User.java                  # 用户实体
│           │   │   └── InvestmentInfo.java        # 投资信息实体
│           │   ├── mapper/
│           │   │   ├── UserMapper.java            # 用户Mapper
│           │   │   └── InvestmentInfoMapper.java  # 投资信息Mapper
│           │   ├── service/
│           │   │   ├── UserService.java           # 用户服务
│           │   │   └── InvestmentInfoService.java # 投资信息服务
│           │   └── util/
│           │       ├── JwtUtil.java               # JWT工具类
│           │       └── PasswordUtil.java          # 密码加密工具
│           └── resources/
│               └── application.yml                # 应用配置文件
│
└── frontend/                    # 前端项目
    ├── package.json             # NPM配置文件
    ├── vite.config.js           # Vite配置文件
    ├── index.html               # HTML入口文件
    └── src/
        ├── App.vue              # 根组件
        ├── main.js              # 应用入口
        ├── api/
        │   ├── request.js       # Axios封装
        │   └── index.js         # API接口定义
        ├── assets/
        │   └── china.json       # 中国地图数据
        ├── router/
        │   └── index.js         # 路由配置
        ├── store/
        │   └── user.js          # 用户状态管理
        └── views/
            ├── Login.vue        # 登录页面
            └── Home.vue         # 首页（地图页面）
```

## 核心文件说明

### 后端核心文件

#### 1. StockApplication.java
- **作用**: Spring Boot应用启动类
- **功能**: 启动应用，配置Mapper扫描

#### 2. AuthController.java
- **作用**: 认证相关接口
- **接口**:
  - POST /api/auth/login - 用户登录
  - GET /api/auth/userInfo - 获取用户信息
  - POST /api/auth/logout - 用户登出

#### 3. InvestmentController.java
- **作用**: 投资信息相关接口
- **接口**:
  - GET /api/investment/province/{province} - 根据省份获取投资信息
  - GET /api/investment/map/data - 获取地图数据
  - POST /api/investment/add - 添加投资信息

#### 4. UserService.java
- **作用**: 用户业务逻辑
- **功能**:
  - 用户登录验证
  - JWT令牌生成
  - Redis缓存用户信息

#### 5. InvestmentInfoService.java
- **作用**: 投资信息业务逻辑
- **功能**:
  - 按省份查询投资信息
  - 获取地图展示数据
  - Redis缓存热点数据
  - RabbitMQ消息发送

#### 6. JwtUtil.java
- **作用**: JWT工具类
- **功能**:
  - 生成JWT令牌
  - 验证JWT令牌
  - 解析用户信息

#### 7. RabbitMQConfig.java
- **作用**: RabbitMQ配置
- **功能**:
  - 创建交换机
  - 创建队列
  - 绑定关系

### 前端核心文件

#### 1. main.js
- **作用**: Vue应用入口
- **功能**:
  - 创建Vue应用
  - 注册插件（Router、Pinia、Element Plus）
  - 注册图标组件

#### 2. router/index.js
- **作用**: 路由配置
- **功能**:
  - 定义路由规则
  - 路由守卫（登录验证）

#### 3. store/user.js
- **作用**: 用户状态管理
- **功能**:
  - 管理用户token
  - 管理用户信息
  - LocalStorage持久化

#### 4. api/request.js
- **作用**: Axios封装
- **功能**:
  - 请求拦截（添加token）
  - 响应拦截（错误处理）

#### 5. api/index.js
- **作用**: API接口定义
- **功能**: 定义所有后端接口调用方法

#### 6. views/Login.vue
- **作用**: 登录页面
- **功能**:
  - 用户登录表单
  - 表单验证
  - 登录逻辑

#### 7. views/Home.vue
- **作用**: 首页（地图页面）
- **功能**:
  - 中国地图展示
  - 省份点击交互
  - 投资信息展示
  - 关联省份高亮

## 数据流程

### 登录流程
```
用户输入账号密码 
  -> 前端验证 
  -> 调用登录API 
  -> 后端验证密码 
  -> 生成JWT令牌 
  -> 缓存用户信息到Redis 
  -> 返回token和用户信息 
  -> 前端保存到LocalStorage 
  -> 跳转到首页
```

### 地图交互流程
```
加载首页 
  -> 调用地图数据API 
  -> 后端从Redis获取缓存 
  -> 如无缓存则查询数据库 
  -> 按省份统计投资数据 
  -> 返回地图数据 
  -> 前端渲染地图

点击省份 
  -> 调用省份投资信息API 
  -> 后端从Redis获取缓存 
  -> 如无缓存则查询数据库 
  -> 查询该省份近10条投资信息 
  -> 解析关联省份 
  -> 返回投资信息和关联省份 
  -> 前端展示信息并高亮关联省份
```

### 缓存策略
- **用户信息**: 30分钟过期
- **投资信息**: 10分钟过期
- **地图数据**: 10分钟过期

### 消息队列
- **用途**: 新增投资信息时发送通知
- **交换机**: investment.exchange (Direct)
- **队列**: investment.queue
- **路由键**: investment.new

## 技术特点

### 后端特点
1. **分层架构**: Controller -> Service -> Mapper
2. **统一响应**: Result统一封装返回结果
3. **JWT认证**: 无状态认证，支持分布式
4. **Redis缓存**: 提升查询性能
5. **消息队列**: 异步处理，解耦业务
6. **MyBatis Plus**: 简化数据库操作

### 前端特点
1. **组件化**: Vue 3 Composition API
2. **状态管理**: Pinia轻量级状态管理
3. **UI组件**: Element Plus美观易用
4. **数据可视化**: ECharts强大的图表库
5. **路由守卫**: 自动登录验证
6. **响应式设计**: 适配不同屏幕尺寸

## 扩展建议

### 功能扩展
1. 添加用户管理功能
2. 添加投资信息管理（增删改查）
3. 添加数据统计分析
4. 添加导出功能
5. 添加实时数据推送（WebSocket）

### 性能优化
1. 数据库查询优化（索引、分页）
2. 前端虚拟滚动（大数据列表）
3. CDN加速静态资源
4. 图片懒加载
5. 接口防抖节流

### 安全加固
1. 密码加密升级（BCrypt）
2. 接口限流
3. SQL注入防护
4. XSS防护
5. CSRF防护

## 开发规范

### 后端规范
- 类名使用大驼峰命名
- 方法名使用小驼峰命名
- 常量使用全大写下划线分隔
- 注释使用Javadoc格式
- 异常统一处理

### 前端规范
- 组件名使用大驼峰命名
- 变量名使用小驼峰命名
- 常量使用全大写下划线分隔
- 使用ESLint代码检查
- 组件单一职责原则

## 测试建议

### 单元测试
- 后端: JUnit + Mockito
- 前端: Vitest + Vue Test Utils

### 集成测试
- API测试: Postman
- E2E测试: Cypress

### 性能测试
- 压力测试: JMeter
- 前端性能: Lighthouse
