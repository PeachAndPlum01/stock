FROM eclipse-temurin:17-jre

WORKDIR /app

# 安装 curl 和 tzdata
RUN apt-get update && apt-get install -y curl tzdata && rm -rf /var/lib/apt/lists/*

COPY target/*.jar app.jar

EXPOSE 8090

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
