# 多阶段构建
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# 复制父POM
COPY ../pom.xml ./pom.xml

# 复制服务代码
COPY pom.xml ./stock-gateway/pom.xml
COPY src ./stock-gateway/src

# 构建
WORKDIR /app/stock-gateway
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 复制jar包
COPY --from=builder /app/stock-gateway/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}", "app.jar"]
