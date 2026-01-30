# 股票投资信息展示系统

## 项目简介
这是一个前后端分离的股票投资信息展示系统，支持基于中国地图的可视化投资信息展示。

## 技术栈

### 后端
- Spring Boot 2.7.x
- MySQL 8.0
- Redis
- RabbitMQ
- MyBatis Plus
- JWT认证

### 前端
- Vue 3
- Element Plus
- ECharts 5（中国地图）
- Axios
- Vue Router
- Pinia

## 项目结构
```
stock/
├── backend/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── router/
│   │   ├── store/
│   │   └── api/
│   └── package.json
└── sql/                    # 数据库脚本
    └── init.sql

```

## 功能特性
1. 用户登录认证
2. 基于中国地图的投资信息可视化
3. 省份点击交互，显示该省份近期10条投资信息
4. 关联省份高亮提示
5. 实时数据更新（通过消息队列）

## 快速开始

### 环境要求
- JDK 1.8+ (推荐使用JDK 8/11/17/21/25)
  - 当前系统JDK: OpenJDK 25.0.1
  - 项目编译目标: Java 1.8
  - Maven已配置向下兼容，可使用高版本JDK编译Java 8代码
- Node.js 16+
- MySQL 8.0+
  - 默认用户: root
  - 默认密码: root
- Redis 5.0+
- RabbitMQ 3.8+

### 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 数据库初始化
```bash
mysql -u root -p < sql/init.sql
```

## 配置说明

### Maven配置
项目采用多模块结构：
- 父项目: `/stock/pom.xml`
- 子模块: `/stock/backend/pom.xml`

编译配置：
```xml
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
```

### IDEA配置
- 已启用Maven项目管理
- 已启用注解处理器（支持Lombok）
- 编译目标: Java 1.8

### 配置检查
```bash
# 检查JDK版本
java -version
javac -version

# 检查Maven配置
cd /Users/lifeng/Desktop/code/stock
mvn clean install

# 验证构建成功
# 应看到: BUILD SUCCESS
```

## 访问地址
- 前端: http://localhost:5173
- 后端API: http://localhost:8080
- 默认账号: admin / 123456

## 主要功能说明

### 地图交互
- 点击省份：该省份视觉上浮起，显示投资信息
- 关联提示：高亮显示有相关投资信息的其他省份
- 数据展示：显示该省份近期10条投资信息
