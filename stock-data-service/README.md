# 股票数据微服务

基于Spring Boot的股票数据微服务，使用Tushare API实时获取股票数据，并通过消息队列（RabbitMQ）推送数据。

## 功能特性

- 实时获取股票行情数据（每10秒更新）
- 通过RabbitMQ消息队列推送数据
- Redis缓存支持
- RESTful API接口
- 定时任务支持
- 健康检查监控

## 技术栈

- Spring Boot 2.7.18
- RabbitMQ（消息队列）
- Redis（缓存）
- OkHttp（HTTP客户端）
- Tushare API（股票数据源）

## 项目结构

```
stock-data-service/
├── src/main/java/com/stock/data/
│   ├── StockDataServiceApplication.java    # 主启动类
│   ├── client/
│   │   └── TushareApiClient.java           # Tushare API客户端
│   ├── config/
│   │   ├── TushareProperties.java          # Tushare配置
│   │   └── StockCodeMapping.java           # 股票代码映射
│   ├── controller/
│   │   └── StockDataController.java        # API控制器
│   ├── model/
│   │   └── StockQuote.java                 # 股票行情数据模型
│   ├── rabbitmq/
│   │   ├── RabbitMQConfig.java             # RabbitMQ配置
│   │   └── StockDataProducer.java          # 消息生产者
│   ├── service/
│   │   └── StockRealtimeService.java       # 实时数据服务
│   └── common/
│       └── Result.java                     # 统一响应结果
├── src/main/resources/
│   └── application.yml                     # 配置文件
├── pom.xml                                 # Maven配置
├── start.sh                                # 启动脚本
├── stop.sh                                 # 停止脚本
└── README.md                               # 本文件
```

## 快速开始

### 1. 配置Tushare Token

在 [Tushare官网](https://tushare.pro) 注册账号并获取Token。

有两种方式配置Token：

**方式1：环境变量**
```bash
export TUSHARE_TOKEN=your_token_here
```

**方式2：配置文件**
编辑 `src/main/resources/application.yml`：
```yaml
tushare:
  token: your_token_here
```

### 2. 配置股票代码映射

编辑 `src/main/resources/application.yml`，添加你要监控的股票：
```yaml
stock-codes:
  mappings:
    - company: "中芯国际"
      code: "688981.SH"
    - company: "东方财富"
      code: "300059.SZ"
    # 添加更多股票...
```

### 3. 启动依赖服务

确保以下服务已启动：
- Redis（默认端口：6379）
- RabbitMQ（默认端口：5672）

### 4. 启动服务

```bash
# 使用启动脚本
./start.sh

# 或使用Maven
mvn clean package
java -jar target/stock-data-service-1.0.0.jar
```

### 5. 停止服务

```bash
./stop.sh
```

## API接口

### 获取单只股票实时数据

```
GET /api/stock/realtime/{tsCode}
```

示例：
```bash
curl http://localhost:8082/api/stock/realtime/000001.SZ
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tsCode": "000001.SZ",
    "name": "平安银行",
    "tradeDate": "20240131",
    "open": 10.50,
    "high": 10.80,
    "low": 10.40,
    "close": 10.70,
    "preClose": 10.45,
    "change": 0.25,
    "pctChg": 2.39,
    "vol": 1500000,
    "amount": 16150000,
    "updateTime": "2024-01-31T15:00:00",
    "source": "tushare"
  }
}
```

### 获取所有股票实时数据

```
GET /api/stock/realtime/all
```

示例：
```bash
curl http://localhost:8082/api/stock/realtime/all
```

### 手动触发数据更新

```
POST /api/stock/update
```

示例：
```bash
curl -X POST http://localhost:8082/api/stock/update
```

### 健康检查

```
GET /api/stock/health
```

示例：
```bash
curl http://localhost:8082/api/stock/health
```

## 消息队列

本服务使用RabbitMQ推送实时股票数据，消费者可以订阅以下队列：

### 股票行情队列

- **队列名**: `stock.quote.queue`
- **交换机**: `stock.quote.exchange` (Fanout类型)
- **消息格式**: JSON格式的StockQuote对象

### 股票实时数据队列

- **队列名**: `stock.realtime.queue`
- **交换机**: `stock.realtime.exchange` (Topic类型)
- **路由键**: `stock.realtime` 或 `stock.realtime.{tsCode}`
- **消息格式**: JSON格式的StockQuote对象

### 消费者示例

```java
@Component
public class StockDataConsumer {
    
    @RabbitListener(queues = "stock.quote.queue")
    public void handleStockQuote(StockQuote stockQuote) {
        System.out.println("收到股票行情: " + stockQuote.getName());
    }
}
```

## Redis缓存

股票数据会缓存在Redis中，默认缓存时间1小时。

- **缓存键前缀**: `stock:realtime:`
- **单只股票缓存**: `stock:realtime:{tsCode}`
- **所有股票缓存**: `stock:realtime:all`

## 定时任务

- **实时数据获取**: 每10秒执行一次（仅在交易时间）
- **批量数据更新**: 每60秒执行一次（仅在交易时间）

交易时间定义：
- 上午：9:30-11:30
- 下午：13:00-15:00
- 只在周一到周五执行

## 日志

日志文件位置：`logs/stock-data-service.log`

查看日志：
```bash
tail -f logs/stock-data-service.log
```

## 配置说明

主要配置项位于 `application.yml`：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | 8082 |
| spring.redis.host | Redis地址 | localhost |
| spring.rabbitmq.host | RabbitMQ地址 | localhost |
| tushare.token | Tushare API Token | - |
| tushare.real-time-interval | 实时更新间隔（秒） | 10 |
| tushare.batch-update-interval | 批量更新间隔（秒） | 60 |

## 注意事项

1. 请确保在Tushare官网注册并获取有效的Token
2. Tushare API有调用频率限制，请合理配置更新间隔
3. RabbitMQ和Redis必须先于服务启动
4. 服务仅在交易时间（工作日9:30-15:00）进行数据更新

## 扩展开发

### 添加新的数据接口

1. 在 `TushareApiClient` 中添加新的API调用方法
2. 在 `StockRealtimeService` 中调用新方法
3. 在 `StockDataController` 中暴露API接口

### 添加新的消息队列

1. 在 `RabbitMQConfig` 中定义新的队列和交换机
2. 在 `StockDataProducer` 中添加发送方法
3. 在业务逻辑中调用发送方法

## 监控

服务提供了Actuator端点用于监控：

- 健康检查: http://localhost:8082/actuator/health
- 指标数据: http://localhost:8082/actuator/metrics
- Prometheus: http://localhost:8082/actuator/prometheus
