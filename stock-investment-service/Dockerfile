# 多阶段构建
FROM maven:3.8-openjdk-8-slim AS builder
WORKDIR /app

# 复制父POM和服务POM
COPY pom.xml ./
COPY stock-investment-service/pom.xml ./stock-investment-service/
COPY stock-investment-service/src ./stock-investment-service/src

# 构建
WORKDIR /app/stock-investment-service
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:8-jre-slim
WORKDIR /app

# 复制jar包
COPY --from=builder /app/stock-investment-service/target/*.jar app.jar

# 暴露端口
EXPOSE 8083

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}", "app.jar"]
