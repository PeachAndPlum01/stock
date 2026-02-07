# Spring Cloud 组件集成指南

## 📋 已集成的组件

本项目已成功集成以下Spring Cloud组件：

### 1. ✅ 链路追踪（Sleuth + Zipkin）
- **功能**：分布式链路追踪，追踪请求在微服务间的调用链路
- **访问地址**：http://localhost:9411
- **用途**：
  - 追踪请求完整调用链
  - 性能分析和瓶颈定位
  - 服务依赖关系可视化

### 2. ✅ Spring Boot Admin
- **功能**：微服务监控管理界面
- **访问地址**：http://localhost:8090
- **登录账号**：admin / admin123
- **用途**：
  - 可视化监控所有服务
  - 查看日志、JVM信息
  - 管理服务状态
  - 实时查看健康状态

### 3. ✅ Seata 分布式事务
- **功能**：分布式事务解决方案
- **已集成服务**：stock-investment-service
- **用途**：
  - 跨服务的事务一致性保证
  - AT模式自动补偿

### 4. ✅ Jasypt 配置加密
- **功能**：配置信息加密
- **用途**：
  - 数据库密码加密
  - API密钥加密
  - 敏感配置保护

### 5. ✅ Nacos Discovery & Config
- **功能**：服务注册发现与配置中心
- **访问地址**：http://localhost:8848/nacos
- **登录账号**：nacos / nacos

### 6. ✅ OpenFeign
- **功能**：声明式HTTP客户端
- **用途**：服务间调用

### 7. ✅ Sentinel
- **功能**：流量控制、熔断降级
- **用途**：服务保护

### 8. ✅ LoadBalancer
- **功能**：客户端负载均衡
- **用途**：服务调用负载均衡

---

## 🚀 快速启动

### 方式一：Docker Compose（推荐）

```bash
# 1. 构建并启动所有服务
docker-compose up -d

# 2. 查看服务状态
docker-compose ps

# 3. 查看日志
docker-compose logs -f stock-admin-server
docker-compose logs -f zipkin
```

### 方式二：本地开发

```bash
# 1. 启动基础设施（MySQL、Redis、RabbitMQ、Nacos、Zipkin）
docker-compose up -d mysql redis rabbitmq nacos zipkin

# 2. 启动Admin Server
cd stock-admin-server
mvn spring-boot:run

# 3. 启动其他微服务
cd stock-gateway && mvn spring-boot:run
cd stock-auth-service && mvn spring-boot:run
cd stock-data-service && mvn spring-boot:run
cd stock-investment-service && mvn spring-boot:run
cd stock-correlation-service && mvn spring-boot:run
```

---

## 📊 监控访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| Spring Boot Admin | http://localhost:8090 | 微服务监控中心（admin/admin123） |
| Zipkin | http://localhost:9411 | 链路追踪界面 |
| Nacos | http://localhost:8848/nacos | 服务注册与配置中心（nacos/nacos） |
| RabbitMQ | http://localhost:15672 | 消息队列管理（guest/guest） |
| API Gateway | http://localhost:8080 | API网关 |

---

## 🔧 配置说明

### 1. 链路追踪配置

每个微服务的 `application.yml` 中已添加：

```yaml
spring:
  # Zipkin链路追踪配置
  zipkin:
    base-url: http://localhost:9411  # Docker环境使用 http://zipkin:9411
    sender:
      type: web
  sleuth:
    sampler:
      probability: 1.0  # 采样率，生产环境建议0.1
```

### 2. Spring Boot Admin配置

每个微服务的 `application.yml` 中已添加：

```yaml
spring:
  # Spring Boot Admin Client配置
  boot:
    admin:
      client:
        url: http://localhost:8090  # Docker环境使用服务名
        instance:
          prefer-ip: true
          service-base-url: http://localhost:8081

management:
  endpoints:
    web:
      exposure:
        include: '*'  # 暴露所有端点
  endpoint:
    health:
      show-details: always
```

### 3. Seata分布式事务配置

在需要分布式事务的服务（如 `stock-investment-service`）中：

```yaml
seata:
  enabled: true
  application-id: stock-investment-service
  tx-service-group: stock-tx-group
  service:
    vgroup-mapping:
      stock-tx-group: default
```

在代码中使用 `@GlobalTransactional` 注解：

```java
@GlobalTransactional(name = "create-investment", rollbackFor = Exception.class)
public void createInvestment(Investment investment) {
    // 业务逻辑
}
```

### 4. Jasypt配置加密

#### 生成加密密码

```bash
# 使用Maven插件加密
mvn jasypt:encrypt-value -Djasypt.encryptor.password=mySecretKey -Djasypt.plugin.value="root"
```

#### 在配置文件中使用

```yaml
spring:
  datasource:
    password: ENC(加密后的密码)

# 启动时指定解密密钥
java -jar app.jar --jasypt.encryptor.password=mySecretKey
```

---

## 📈 使用示例

### 1. 查看链路追踪

1. 访问 http://localhost:9411
2. 点击"Run Query"查看最近的调用链路
3. 点击具体的Trace查看详细信息
4. 可以看到请求经过了哪些服务，每个服务的耗时

### 2. 使用Spring Boot Admin监控

1. 访问 http://localhost:8090
2. 使用 admin/admin123 登录
3. 在"应用墙"中查看所有服务状态
4. 点击具体服务查看：
   - 健康状态
   - JVM内存使用
   - 线程信息
   - 日志输出
   - 环境变量
   - HTTP追踪

### 3. 分布式事务使用

```java
@Service
public class InvestmentService {
    
    @Autowired
    private InvestmentMapper investmentMapper;
    
    @Autowired
    private StockDataFeignClient stockDataClient;
    
    // 使用全局事务注解
    @GlobalTransactional(name = "create-investment-tx", rollbackFor = Exception.class)
    public void createInvestment(Investment investment) {
        // 1. 保存投资记录
        investmentMapper.insert(investment);
        
        // 2. 调用股票数据服务更新统计
        stockDataClient.updateStatistics(investment.getStockCode());
        
        // 如果任何一步失败，整个事务回滚
    }
}
```

---

## 🔍 故障排查

### 1. Zipkin无法收集链路数据

**问题**：Zipkin界面看不到任何Trace

**解决方案**：
- 检查Zipkin服务是否启动：`docker ps | grep zipkin`
- 检查微服务配置中的zipkin地址是否正确
- 检查采样率是否为0：`spring.sleuth.sampler.probability`

### 2. Admin Server看不到服务

**问题**：Admin界面没有显示微服务

**解决方案**：
- 检查Admin Server是否启动
- 检查微服务的Admin Client配置
- 检查Actuator端点是否暴露：`management.endpoints.web.exposure.include`
- 查看微服务日志是否有注册错误

### 3. Seata事务不生效

**问题**：分布式事务没有回滚

**解决方案**：
- 检查是否添加了 `@GlobalTransactional` 注解
- 检查Seata Server是否启动
- 检查事务组配置是否正确
- 查看Seata日志

---

## 📝 待完善的配置

由于服务较多，以下服务的配置文件需要手动添加链路追踪和Admin Client配置：

### 需要更新的文件：

1. **stock-investment-service**
   - `src/main/resources/application.yml`
   - `src/main/resources/application-docker.yml`

2. **stock-correlation-service**
   - `src/main/resources/application.yml`
   - `src/main/resources/application-docker.yml`

3. **stock-gateway**
   - `src/main/resources/application.yml`
   - `src/main/resources/application-docker.yml`

### 配置模板：

在每个服务的 `application.yml` 中添加：

```yaml
spring:
  # Zipkin链路追踪配置
  zipkin:
    base-url: http://localhost:9411
    sender:
      type: web
  sleuth:
    sampler:
      probability: 1.0

  # Spring Boot Admin Client配置
  boot:
    admin:
      client:
        url: http://localhost:8090
        instance:
          prefer-ip: true
          service-base-url: http://localhost:${server.port}

management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
```

在 `application-docker.yml` 中使用Docker服务名：

```yaml
spring:
  zipkin:
    base-url: http://zipkin:9411
  boot:
    admin:
      client:
        url: http://stock-admin-server:8090
        instance:
          service-base-url: http://${spring.application.name}:${server.port}
```

---

## 🎯 最佳实践

### 1. 链路追踪

- **开发环境**：采样率设置为1.0（100%）
- **生产环境**：采样率设置为0.1（10%），减少性能开销
- 为关键业务方法添加自定义Span标签

### 2. 监控告警

- 定期查看Admin监控面板
- 关注JVM内存使用率
- 监控服务响应时间
- 设置健康检查告警

### 3. 分布式事务

- 仅在必要时使用分布式事务
- 优先考虑最终一致性方案
- 合理设置事务超时时间
- 做好事务补偿逻辑

### 4. 配置加密

- 所有敏感配置都应加密
- 密钥不要提交到代码仓库
- 使用环境变量传递密钥
- 定期更换加密密钥

---

## 📚 参考文档

- [Spring Cloud Sleuth官方文档](https://spring.io/projects/spring-cloud-sleuth)
- [Zipkin官方文档](https://zipkin.io/)
- [Spring Boot Admin官方文档](https://codecentric.github.io/spring-boot-admin/)
- [Seata官方文档](https://seata.io/)
- [Jasypt官方文档](https://github.com/ulisesbocchio/jasypt-spring-boot)

---

## 🆘 技术支持

如有问题，请查看：
1. 各服务的日志文件：`logs/` 目录
2. Docker容器日志：`docker-compose logs -f [服务名]`
3. Spring Boot Admin监控面板
4. Zipkin链路追踪界面
