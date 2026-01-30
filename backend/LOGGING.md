# 日志系统使用说明

## 📋 概述

项目使用 Logback 作为日志框架，所有日志统一存储在 `backend/logs/` 目录下，方便查看和管理。

## 📁 日志文件结构

```
backend/logs/
├── all.log          # 所有级别的日志（DEBUG、INFO、WARN、ERROR）
├── error.log        # 仅ERROR级别日志
├── warn.log         # 仅WARN级别日志
├── info.log         # 仅INFO级别日志
├── debug.log        # 仅DEBUG级别日志
└── *.yyyy-MM-dd.*.log  # 历史归档日志（按日期滚动）
```

## 🎯 日志级别说明

| 级别 | 用途 | 示例场景 |
|------|------|----------|
| **ERROR** | 错误信息，需要立即关注 | 数据库连接失败、业务异常、系统错误 |
| **WARN** | 警告信息，可能存在问题 | 参数校验失败、资源不足警告 |
| **INFO** | 一般信息，记录关键业务流程 | 用户登录、订单创建、接口调用 |
| **DEBUG** | 调试信息，开发环境使用 | 方法参数、SQL语句、详细流程 |

## 🔧 日志配置特性

### 1. 日志滚动策略
- **按时间滚动**：每天生成新的日志文件
- **按大小滚动**：单个文件超过限制后自动分割
  - all.log / info.log / debug.log：最大 100MB
  - error.log / warn.log：最大 50MB

### 2. 日志保留策略
- **ERROR/WARN/INFO**：保留 30 天
- **DEBUG**：保留 7 天
- **ALL**：保留 30 天

### 3. 环境配置
- **开发环境（dev）**：输出所有级别日志，包括控制台
- **生产环境（prod）**：仅输出 INFO 及以上级别，关闭 DEBUG
- **默认环境**：与开发环境相同

## 📡 API 接口

项目提供了日志查看的 REST API，前端可以直接调用：

### 1. 获取日志文件列表
```http
GET /api/logs/files
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "name": "error.log",
      "size": "2.34 MB",
      "lastModified": "2026-01-30T14:15:00",
      "path": "logs/error.log"
    }
  ]
}
```

### 2. 读取日志内容
```http
GET /api/logs/content?fileName=error.log&lines=100&level=ERROR
```

**参数说明：**
- `fileName`：日志文件名（必填）
- `lines`：读取行数，默认 100
- `level`：日志级别过滤（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileName": "error.log",
    "totalLines": 50,
    "content": ["日志行1", "日志行2"],
    "fileSize": "2.34 MB",
    "lastModified": "2026-01-30T14:15:00"
  }
}
```

### 3. 搜索日志
```http
GET /api/logs/search?fileName=all.log&keyword=exception&maxLines=100
```

**参数说明：**
- `fileName`：日志文件名（必填）
- `keyword`：搜索关键词（必填）
- `maxLines`：最大返回行数，默认 100

### 4. 获取日志统计
```http
GET /api/logs/stats
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "errorSize": "2.34 MB",
    "warnSize": "1.23 MB",
    "infoSize": "10.45 MB",
    "debugSize": "5.67 MB",
    "allSize": "20.12 MB",
    "totalFiles": 8,
    "totalSize": "40.00 MB"
  }
}
```

## 💻 代码中使用日志

### 1. 引入日志
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    // 或使用 Lombok 注解
    // @Slf4j
}
```

### 2. 记录日志
```java
// DEBUG - 调试信息
log.debug("用户查询参数: {}", userId);

// INFO - 一般信息
log.info("用户登录成功: userId={}, username={}", userId, username);

// WARN - 警告信息
log.warn("用户尝试访问无权限资源: userId={}, resource={}", userId, resource);

// ERROR - 错误信息
log.error("数据库操作失败: {}", e.getMessage(), e);
```

### 3. 最佳实践
```java
// ✅ 推荐：使用占位符
log.info("用户 {} 创建订单 {}", username, orderId);

// ❌ 不推荐：字符串拼接
log.info("用户 " + username + " 创建订单 " + orderId);

// ✅ 推荐：记录异常堆栈
try {
    // 业务代码
} catch (Exception e) {
    log.error("业务处理失败: {}", e.getMessage(), e);
}

// ❌ 不推荐：只记录消息
catch (Exception e) {
    log.error("业务处理失败: " + e.getMessage());
}
```

## 🔍 日志查看方式

### 方式1：直接查看文件
```bash
# 查看最新的错误日志
tail -f backend/logs/error.log

# 查看最近100行
tail -n 100 backend/logs/all.log

# 搜索关键词
grep "exception" backend/logs/error.log
```

### 方式2：通过 API 接口
前端可以调用上述 API 接口，实现日志查看页面。

### 方式3：使用日志分析工具
- ELK Stack（Elasticsearch + Logstash + Kibana）
- Grafana Loki
- 其他日志管理平台

## ⚠️ 注意事项

1. **安全性**：日志 API 建议添加权限控制，避免敏感信息泄露
2. **性能**：大文件读取可能影响性能，建议限制读取行数
3. **磁盘空间**：定期清理历史日志，避免占用过多磁盘空间
4. **敏感信息**：不要在日志中记录密码、token 等敏感信息
5. **日志级别**：生产环境建议使用 INFO 级别，避免过多日志

## 🚀 启动项目

启动后，日志会自动生成在 `backend/logs/` 目录下：

```bash
# 开发环境启动
cd backend
mvn spring-boot:run

# 或指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📊 日志格式

```
2026-01-30 14:15:28.123 [http-nio-8080-exec-1] INFO  com.stock.controller.UserController - 用户登录成功: userId=1, username=admin
```

格式说明：
- `2026-01-30 14:15:28.123`：时间戳（精确到毫秒）
- `[http-nio-8080-exec-1]`：线程名
- `INFO`：日志级别
- `com.stock.controller.UserController`：类名
- `用户登录成功...`：日志消息
