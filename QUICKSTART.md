# 快速启动指南

## 前置条件检查

在开始之前，请确保已安装以下软件：

- ✅ JDK 1.8+
- ✅ Maven 3.6+
- ✅ Node.js 16+
- ✅ MySQL 8.0+
- ✅ Redis 5.0+
- ✅ RabbitMQ 3.8+

## 快速启动（5分钟）

### 第一步：启动中间件服务

```bash
# 启动MySQL（macOS）
brew services start mysql

# 启动Redis
brew services start redis

# 启动RabbitMQ
brew services start rabbitmq

# 验证服务状态
brew services list
```

### 第二步：初始化数据库

```bash
# 进入项目目录
cd /Users/lifeng/Desktop/code/stock

# 执行数据库初始化脚本
mysql -u root -p < sql/init.sql

# 输入MySQL密码后，数据库将自动创建并初始化数据
```

### 第三步：启动后端

```bash
# 进入后端目录
cd backend

# 首次启动需要安装依赖（约2-3分钟）
mvn clean install

# 启动后端服务
mvn spring-boot:run

# 看到以下信息表示启动成功：
# ========================================
# 股票投资信息系统启动成功！
# 访问地址: http://localhost:8080/api
# ========================================
```

### 第四步：启动前端

**打开新的终端窗口**

```bash
# 进入前端目录
cd /Users/lifeng/Desktop/code/stock/frontend

# 首次启动需要安装依赖（约1-2分钟）
npm install

# 如果npm速度慢，可以使用淘宝镜像
npm install --registry=https://registry.npmmirror.com

# 启动前端服务
npm run dev

# 看到以下信息表示启动成功：
# VITE v5.0.11  ready in xxx ms
# ➜  Local:   http://localhost:5173/
```

### 第五步：访问系统

1. 打开浏览器访问：http://localhost:5173
2. 使用默认账号登录：
   - 用户名：`admin`
   - 密码：`123456`
3. 登录成功后，您将看到中国地图
4. 点击任意省份，查看该省份的投资信息

## 常见问题快速解决

### 问题1：后端启动失败 - 数据库连接错误

**错误信息**：
```
Access denied for user 'root'@'localhost'
```

**解决方案**：
```bash
# 修改配置文件中的数据库密码
vim backend/src/main/resources/application.yml

# 找到以下配置并修改密码
spring:
  datasource:
    password: your_mysql_password  # 改为你的MySQL密码
```

### 问题2：前端启动失败 - 依赖安装错误

**解决方案**：
```bash
# 删除依赖并重新安装
cd frontend
rm -rf node_modules package-lock.json
npm install --registry=https://registry.npmmirror.com
```

### 问题3：地图不显示

**解决方案**：
1. 检查浏览器控制台是否有错误
2. 确认后端服务是否正常运行
3. 检查 `frontend/src/assets/china.json` 文件是否存在

```bash
# 如果文件不存在，重新下载
curl -o frontend/src/assets/china.json https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json
```

### 问题4：Redis连接失败

**解决方案**：
```bash
# 检查Redis是否运行
redis-cli ping
# 应该返回：PONG

# 如果没有运行，启动Redis
brew services start redis
```

### 问题5：RabbitMQ连接失败

**解决方案**：
```bash
# 检查RabbitMQ状态
rabbitmqctl status

# 如果没有运行，启动RabbitMQ
brew services start rabbitmq

# 访问管理界面验证
open http://localhost:15672
# 默认账号：guest / guest
```

## 验证系统功能

### 1. 验证登录功能
- 访问 http://localhost:5173
- 输入账号密码
- 点击登录按钮
- 应该跳转到首页

### 2. 验证地图功能
- 首页应该显示中国地图
- 不同省份有不同颜色
- 鼠标悬停显示省份信息

### 3. 验证交互功能
- 点击"北京"省份
- 右侧应该显示北京的投资信息
- 顶部显示关联省份标签
- 地图上关联省份高亮显示

### 4. 验证API接口
```bash
# 测试登录接口
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 测试地图数据接口
curl http://localhost:8080/api/investment/map/data

# 测试省份数据接口
curl "http://localhost:8080/api/investment/province/北京?limit=10"
```

## 停止服务

### 停止前端
在前端终端按 `Ctrl + C`

### 停止后端
在后端终端按 `Ctrl + C`

### 停止中间件（可选）
```bash
# 停止MySQL
brew services stop mysql

# 停止Redis
brew services stop redis

# 停止RabbitMQ
brew services stop rabbitmq
```

## 下一步

系统启动成功后，您可以：

1. 📖 阅读 [API.md](API.md) 了解接口详情
2. 📁 阅读 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 了解项目结构
3. 🚀 阅读 [DEPLOY.md](DEPLOY.md) 了解生产部署
4. 💻 开始开发新功能

## 开发模式

### 后端热重载
后端使用 Spring Boot DevTools，修改代码后会自动重启

### 前端热重载
前端使用 Vite，修改代码后会自动刷新浏览器

### 数据库管理
推荐使用以下工具管理数据库：
- Navicat
- DBeaver
- MySQL Workbench

### Redis管理
推荐使用以下工具管理Redis：
- RedisInsight
- Another Redis Desktop Manager

### RabbitMQ管理
访问管理界面：http://localhost:15672
- 用户名：guest
- 密码：guest

## 技术支持

如遇到其他问题：
1. 查看日志文件
2. 检查配置文件
3. 阅读完整文档
4. 搜索错误信息

祝您使用愉快！🎉
