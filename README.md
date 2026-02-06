# 天问 - 股票投资信息可视化系统

## 📖 项目简介

这是一个基于微服务架构的股票投资信息可视化系统，支持基于中国地图的可视化投资信息展示。采用 Spring Cloud 微服务技术栈，实现服务解耦和独立部署。

### ✨ 核心功能

- 🗺️ **地图可视化**：基于中国地图展示各省份投资信息
- 🔍 **智能查询**：支持多维度股票信息查询和筛选
- 📊 **实时数据**：集成 Tushare API，获取实时股票数据
- 🔐 **安全认证**：基于 JWT 的用户认证和授权
- 🎯 **关联分析**：省份间投资关联度分析

## 🏗️ 微服务架构

```
前端 (80)
    ↓
API网关 (8080)
    ↓
    ├─→ 认证服务 (8081)
    ├─→ 股票数据服务 (8082)
    ├─→ 投资信息服务 (8083)
    └─→ 省份关联服务 (8084)
```

### 📦 服务列表

| 服务名称 | 端口 | 职责 | 技术栈 |
|---------|------|------|--------|
| **stock-gateway** | 8080 | API网关、路由转发、JWT验证 | Spring Cloud Gateway |
| **stock-auth-service** | 8081 | 用户认证、登录注册、Token管理 | Spring Boot + JWT |
| **stock-data-service** | 8082 | 股票数据获取、Tushare API集成 | Spring Boot + Tushare |
| **stock-investment-service** | 8083 | 投资信息查询、统计分析 | Spring Boot + MyBatis Plus |
| **stock-correlation-service** | 8084 | 省份关联度计算、相关度分析 | Spring Boot + MyBatis Plus |
| **MySQL** | 3306 | 数据存储 | MySQL 8.0 |
| **Redis** | 6379 | 缓存、Session | Redis 7.0 |
| **RabbitMQ** | 5672 | 消息队列 | RabbitMQ 3.12 |

## 💻 技术栈

### 后端（微服务）
- **框架**: Spring Boot 2.7.18 + Spring Cloud 2021.0.8
- **API网关**: Spring Cloud Gateway
- **数据库**: MySQL 8.0 + MyBatis Plus 3.5.4
- **缓存**: Redis 7.0
- **消息队列**: RabbitMQ 3.12
- **认证**: JWT (jjwt 0.11.5)
- **股票数据**: Tushare API

### 前端
- Vue 3
- Element Plus
- ECharts 5（中国地图）
- Axios
- Vue Router
- Pinia

### 容器化
- Docker
- Docker Compose

## 📁 项目结构

```
stock/
├── pom.xml                          # 父POM，统一管理依赖版本
├── docker-compose.yml               # Docker Compose编排文件
├── .env.template                    # 环境变量配置模板
│
├── stock-gateway/                   # API网关服务 (8080)
│   ├── src/main/java/com/stock/gateway/
│   │   ├── GatewayApplication.java
│   │   ├── config/CorsConfig.java
│   │   └── filter/AuthFilter.java
│   ├── Dockerfile
│   └── pom.xml
│
├── stock-auth-service/              # 认证服务 (8081)
│   ├── src/main/java/com/stock/auth/
│   │   ├── AuthServiceApplication.java
│   │   ├── controller/AuthController.java
│   │   ├── service/AuthService.java
│   │   └── util/JwtUtil.java
│   ├── Dockerfile
│   └── pom.xml
│
├── stock-data-service/              # 股票数据服务 (8082)
│   ├── src/main/java/com/stock/data/
│   │   ├── StockDataServiceApplication.java
│   │   ├── client/TushareApiClient.java
│   │   ├── service/StockCompanyService.java
│   │   └── controller/StockDataController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── stock-investment-service/        # 投资信息服务 (8083)
│   ├── src/main/java/com/stock/investment/
│   │   ├── InvestmentServiceApplication.java
│   │   ├── service/InvestmentInfoService.java
│   │   └── controller/InvestmentInfoController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── stock-correlation-service/       # 省份关联服务 (8084)
│   ├── src/main/java/com/stock/correlation/
│   │   ├── CorrelationServiceApplication.java
│   │   ├── service/CorrelationService.java
│   │   └── controller/CorrelationController.java
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                        # 前端项目
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── router/
│   │   └── api/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
└── docs/                           # 文档目录
    ├── API接口文档.md
    ├── TUSHARE_API_GUIDE.md
    └── STOCK_COMPANY_SERVICE_README.md
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 16+
- **Maven**: 3.6+
- **Docker**: 20.10+ (推荐)
- **Docker Compose**: 2.0+ (推荐)

### 方式一：Docker Compose 部署（推荐）⭐

#### 1. 配置环境变量

```bash
# 复制环境配置模板
cp .env.template .env

# 编辑.env文件，填写实际配置（特别是TUSHARE_TOKEN）
vim .env
```

`.env` 文件配置示例：
```env
# Tushare API Token（必须配置）
TUSHARE_TOKEN=your_tushare_token_here

# MySQL配置
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=stock_investment

# JWT配置
JWT_SECRET=stock-investment-system-jwt-secret-key-2024
JWT_EXPIRATION=86400000
```

#### 2. 启动所有服务

```bash
# 构建并启动所有服务（包括MySQL、Redis、RabbitMQ和所有微服务）
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 3. 验证服务

```bash
# 检查所有服务健康状态
curl http://localhost:8080/actuator/health  # 网关
curl http://localhost:8081/actuator/health  # 认证服务
curl http://localhost:8082/actuator/health  # 股票数据服务
curl http://localhost:8083/actuator/health  # 投资信息服务
curl http://localhost:8084/actuator/health  # 省份关联服务
```

#### 4. 访问系统

- **前端页面**: http://localhost
- **API网关**: http://localhost:8080
- **RabbitMQ管理**: http://localhost:15672 (guest/guest)
- **默认账号**: admin / 123456

#### 5. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v
```

### 方式二：本地开发部署

#### 1. 启动基础设施

```bash
# 启动MySQL、Redis、RabbitMQ
docker-compose up -d mysql redis rabbitmq

# 等待数据库初始化完成（约30秒）
sleep 30
```

#### 2. 配置 Tushare Token

编辑 `stock-data-service/src/main/resources/application.yml`：
```yaml
tushare:
  token: your_tushare_token_here
```

#### 3. 启动后端服务

```bash
# 手动启动各服务
mvn clean package -DskipTests
cd stock-gateway && mvn spring-boot:run &
cd stock-auth-service && mvn spring-boot:run &
cd stock-data-service && mvn spring-boot:run &
cd stock-investment-service && mvn spring-boot:run &
cd stock-correlation-service && mvn spring-boot:run &
```

#### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

#### 5. 访问系统

- **前端**: http://localhost:5173
- **API网关**: http://localhost:8080



## 📚 API 接口文档

详细的 API 文档请查看：[API接口文档.md](./docs/API接口文档.md)

### 快速示例

#### 1. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

#### 2. 查询股票列表（需要Token）

```bash
# 先登录获取token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' \
  | jq -r '.data.token')

# 查询股票列表
curl -X GET "http://localhost:8080/api/investment/list?page=1&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

#### 3. 查询省份统计

```bash
curl -X GET "http://localhost:8080/api/investment/stats/province" \
  -H "Authorization: Bearer $TOKEN"
```

## 🔧 配置说明

### 端口配置

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 80 | 前端页面 |
| API Gateway | 8080 | 统一入口 |
| Auth Service | 8081 | 用户认证 |
| Stock Data Service | 8082 | 股票数据 |
| Investment Service | 8083 | 投资信息 |
| Correlation Service | 8084 | 关联分析 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | 消息队列 |
| RabbitMQ Management | 15672 | 管理界面 |

### JWT 配置

网关和认证服务需要使用相同的JWT密钥：
```yaml
jwt:
  secret: stock-investment-system-jwt-secret-key-2024
  expiration: 86400000  # 24小时
```

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stock_investment
    username: root
    password: root
```

## 🐛 故障排查

### 查看服务日志

```bash
# Docker部署
docker-compose logs -f stock-gateway
docker-compose logs -f stock-data-service

# 本地部署
tail -f stock-gateway/logs/gateway.log
tail -f stock-data-service/logs/stock-data-service.log
```

### 常见问题

#### 1. 服务启动失败

```bash
# 检查端口占用
lsof -i :8080

# 检查MySQL是否启动
docker-compose ps mysql

# 查看服务日志
docker-compose logs stock-data-service
```

#### 2. 网关路由不通

```bash
# 检查目标服务是否启动
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health

# 查看网关日志
docker-compose logs stock-gateway
```

#### 3. Token验证失败

- 检查Token是否正确
- 检查Token是否过期（默认24小时）
- 确保请求头格式正确：`Authorization: Bearer {token}`
- 确认JWT密钥配置一致

#### 4. 数据库连接失败

```bash
# 检查MySQL是否就绪
docker-compose exec mysql mysqladmin -uroot -proot ping

# 查看MySQL日志
docker-compose logs mysql

# 等待数据库完全启动
sleep 30
```

## 📊 监控与管理

### 健康检查

每个服务都提供健康检查端点：
```bash
curl http://localhost:8080/actuator/health  # 网关
curl http://localhost:8081/actuator/health  # 认证服务
curl http://localhost:8082/actuator/health  # 股票数据服务
```

### RabbitMQ 管理界面

访问：http://localhost:15672
- 用户名：guest
- 密码：guest

### 日志管理

```bash
# Docker部署 - 查看所有服务日志
docker-compose logs -f

# 本地部署 - 查看所有服务日志
tail -f */logs/*.log
```

## 🔐 安全建议

### 生产环境配置

1. **修改默认密码**
   - MySQL root 密码
   - RabbitMQ 用户密码
   - JWT 密钥

2. **配置防火墙**
   ```bash
   # 只开放必要端口
   firewall-cmd --permanent --add-port=80/tcp
   firewall-cmd --permanent --add-port=8080/tcp
   firewall-cmd --reload
   ```

3. **使用 HTTPS**
   - 配置 SSL 证书
   - 启用 HTTPS

4. **限制访问**
   - 配置安全组规则
   - 使用 VPN 或内网访问

## 📈 性能优化

- ✅ Redis 缓存热点数据
- ✅ 数据库索引优化
- ✅ 服务独立扩展
- ✅ 消息队列异步处理
- ✅ 连接池配置优化

## 🔄 更新部署

### 更新后端服务

```bash
# 重新构建并启动
docker-compose build --no-cache stock-data-service
docker-compose up -d stock-data-service

# 查看日志确认启动成功
docker-compose logs -f stock-data-service
```

### 更新前端服务

```bash
# 重新构建并启动
docker-compose build --no-cache frontend
docker-compose up -d frontend

# 查看日志确认启动成功
docker-compose logs -f frontend
```

## 📖 相关文档

- [API接口文档](./docs/API接口文档.md) - 完整的API接口说明
- [Tushare API指南](./docs/TUSHARE_API_GUIDE.md) - Tushare API使用指南
- [股票公司服务说明](./docs/STOCK_COMPANY_SERVICE_README.md) - 股票公司服务详细说明

## 🎯 后续优化计划

- [ ] 集成 Nacos 实现服务注册与发现
- [ ] 使用 Nacos Config 统一管理配置
- [ ] 集成 Prometheus + Grafana 监控
- [ ] 集成 Sleuth + Zipkin 链路追踪
- [ ] 使用 Sentinel 实现熔断降级
- [ ] 使用 Seata 实现分布式事务
- [ ] Kubernetes 容器编排

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 📞 技术支持

如遇问题，请：
1. 查看服务日志：`docker-compose logs -f`
2. 检查健康状态：`curl http://localhost:8080/actuator/health`
3. 查看相关文档
4. 提交 Issue

---

**项目版本**: 1.0.0  
**最后更新**: 2026-02-06  
**微服务改造**: ✅ 已完成
