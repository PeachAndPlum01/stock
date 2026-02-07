# 单阶段构建 - 使用服务器本地Maven编译的jar包
FROM eclipse-temurin:17-jre

WORKDIR /app

# 安装 curl 和 tzdata
RUN apt-get update && apt-get install -y curl tzdata && rm -rf /var/lib/apt/lists/*

# 从本地复制 jar 文件（需要在服务器上先使用Maven编译）
COPY target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 8083

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}", "app.jar"]
