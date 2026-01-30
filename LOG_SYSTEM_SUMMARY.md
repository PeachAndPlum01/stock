# 日志系统配置完成总结

## ✅ 已完成的工作

### 1. 后端日志配置

#### 📄 创建的文件
- **logback-spring.xml** - Logback 日志配置文件
  - 位置：`backend/src/main/resources/logback-spring.xml`
  - 功能：配置日志输出格式、文件滚动策略、日志级别等

#### 📝 修改的文件
- **application.yml** - 移除 MyBatis 控制台日志输出
  - 改为使用 Logback 统一管理日志
  
- **AuthController.java** - 添加日志记录示例
  - 引入 SLF4J Logger
  - 在登录、获取用户信息、登出等方法中添加日志记录
  - 展示 DEBUG、INFO、WARN、ERROR 各级别日志的使用

#### 🆕 新增的功能
- **LogController.java** - 日志查看 REST API
  - `GET /api/logs/files` - 获取日志文件列表
  - `GET /api/logs/content` - 读取日志内容（支持行数限制和级别过滤）
  - `GET /api/logs/search` - 搜索日志内容
  - `GET /api/logs/stats` - 获取日志统计信息

### 2. 日志文件结构

所有日志统一存储在 `backend/logs/` 目录：

```
backend/logs/
├── all.log          # 所有级别日志（DEBUG、INFO、WARN、ERROR）
├── error.log        # 仅 ERROR 级别
├── warn.log         # 仅 WARN 级别
├── info.log         # 仅 INFO 级别
├── debug.log        # 仅 DEBUG 级别
└── *.yyyy-MM-dd.*.log  # 历史归档日志
```

### 3. 日志特性

#### 🔄 滚动策略
- **按时间滚动**：每天生成新文件
- **按大小滚动**：
  - all.log / info.log / debug.log：最大 100MB
  - error.log / warn.log：最大 50MB

#### 📅 保留策略
- ERROR/WARN/INFO：保留 30 天
- DEBUG：保留 7 天
- ALL：保留 30 天

#### 🌍 环境配置
- **开发环境（dev）**：输出所有级别，包括控制台
- **生产环境（prod）**：仅 INFO 及以上，关闭 DEBUG
- **默认环境**：与开发环境相同

### 4. 前端日志查看

#### 📱 创建的组件
- **LogViewer.vue** - 日志查看页面
  - 位置：`frontend/src/views/LogViewer.vue`
  - 功能：
    - 显示日志统计信息（各级别文件大小）
    - 日志文件列表
    - 日志内容查看（支持行数、级别过滤）
    - 日志搜索
    - 实时刷新
    - 语法高亮（不同级别不同颜色）

### 5. 文档和工具

#### 📚 文档
- **LOGGING.md** - 详细的日志系统使用文档
  - 位置：`backend/LOGGING.md`
  - 内容：配置说明、API 文档、使用示例、最佳实践

- **README.md** - 更新主文档
  - 添加日志系统章节
  - 说明日志文件结构和查看方式

#### 🛠️ 工具
- **test-logging.sh** - 日志系统测试脚本
  - 位置：`backend/test-logging.sh`
  - 功能：检查配置文件、日志目录、提供测试建议

#### 🚫 Git 配置
- **.gitignore** - 添加日志目录忽略规则
  - 避免将日志文件提交到版本控制

## 🎯 使用方法

### 后端使用日志

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class YourService {
    private static final Logger log = LoggerFactory.getLogger(YourService.class);
    
    public void yourMethod() {
        log.debug("调试信息: {}", param);
        log.info("一般信息: {}", result);
        log.warn("警告信息: {}", warning);
        log.error("错误信息: {}", e.getMessage(), e);
    }
}
```

### 查看日志

#### 方式1：命令行
```bash
# 实时查看所有日志
tail -f backend/logs/all.log

# 查看错误日志
tail -f backend/logs/error.log

# 搜索关键词
grep "exception" backend/logs/error.log
```

#### 方式2：API 接口
```bash
# 获取日志文件列表
curl http://localhost:8080/api/logs/files

# 读取日志内容
curl "http://localhost:8080/api/logs/content?fileName=error.log&lines=100"

# 搜索日志
curl "http://localhost:8080/api/logs/search?fileName=all.log&keyword=exception"

# 获取统计信息
curl http://localhost:8080/api/logs/stats
```

#### 方式3：前端页面
访问日志查看页面（需要在路由中配置）：
```
http://localhost:5173/logs
```

## 📊 日志格式

```
2026-01-30 14:15:28.123 [http-nio-8080-exec-1] INFO  com.stock.controller.AuthController - 用户登录成功: username=admin
```

- 时间戳（精确到毫秒）
- 线程名
- 日志级别
- 类名
- 日志消息

## ⚠️ 注意事项

1. **首次启动**：logs 目录会自动创建
2. **权限控制**：建议为日志 API 添加权限验证
3. **性能考虑**：大文件读取可能影响性能，已限制读取行数
4. **磁盘空间**：定期清理历史日志
5. **敏感信息**：不要在日志中记录密码、token 等敏感数据
6. **生产环境**：建议使用 INFO 级别，关闭 DEBUG

## 🚀 测试步骤

1. **启动应用**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **触发日志记录**
   - 访问登录接口
   - 触发各种业务操作

3. **查看日志文件**
   ```bash
   ls -lh backend/logs/
   tail -f backend/logs/all.log
   ```

4. **测试 API**
   ```bash
   curl http://localhost:8080/api/logs/files
   ```

5. **访问前端页面**
   - 在路由中添加日志查看页面
   - 访问并测试各项功能

## 📦 文件清单

### 新增文件
- `backend/src/main/resources/logback-spring.xml`
- `backend/src/main/java/com/stock/controller/LogController.java`
- `backend/LOGGING.md`
- `backend/test-logging.sh`
- `frontend/src/views/LogViewer.vue`

### 修改文件
- `backend/src/main/resources/application.yml`
- `backend/src/main/java/com/stock/controller/AuthController.java`
- `.gitignore`
- `README.md`

## ✨ 特色功能

1. **多级别分离**：不同级别日志分别存储，便于快速定位问题
2. **自动滚动**：按时间和大小自动滚动，避免单文件过大
3. **环境区分**：开发和生产环境使用不同配置
4. **REST API**：提供完整的日志查看 API
5. **前端界面**：美观的日志查看界面，支持搜索和过滤
6. **安全防护**：API 包含路径遍历攻击防护

## 🎉 完成！

日志系统已经完全配置完成，可以开始使用了！

如有任何问题，请查看 `backend/LOGGING.md` 文档。
