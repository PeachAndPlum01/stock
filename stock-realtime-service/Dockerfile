FROM eclipse-temurin:17-jre

WORKDIR /app

# 安装 curl 和 tzdata
RUN apt-get update && apt-get install -y curl tzdata && rm -rf /var/lib/apt/lists/*

COPY target/*.jar app.jar

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8085

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
