#!/bin/bash
# 生产环境快速部署脚本

set -e

echo "========================================="
echo "  股票投资信息展示系统 - 生产环境部署"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker未安装${NC}"
    exit 1
fi

# 检查Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose未安装${NC}"
    exit 1
fi

# 检查.env文件
if [ ! -f .env ]; then
    echo -e "${YELLOW}警告: .env文件不存在，从模板创建...${NC}"
    cp .env.template .env
    echo -e "${RED}请编辑.env文件，填入实际配置后重新运行此脚本${NC}"
    exit 1
fi

# 加载环境变量
source .env

# 检查必要的环境变量
if [ "$MYSQL_ROOT_PASSWORD" == "CHANGE_ME_STRONG_PASSWORD_HERE" ]; then
    echo -e "${RED}错误: 请在.env文件中设置强密码${NC}"
    exit 1
fi

echo -e "${GREEN}✓ 环境检查通过${NC}"
echo ""

# 创建必要的目录
echo "创建数据目录..."
mkdir -p /opt/stock/docker-data/{mysql,redis,rabbitmq,nacos,elasticsearch}
mkdir -p /backup/{mysql,redis}
mkdir -p /var/log/stock
mkdir -p config/ssl

echo -e "${GREEN}✓ 目录创建完成${NC}"
echo ""

# 生成自签名证书（如果不存在）
if [ ! -f config/ssl/cert.pem ]; then
    echo "生成自签名SSL证书..."
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
      -keyout config/ssl/key.pem \
      -out config/ssl/cert.pem \
      -subj "/CN=localhost" \
      2>/dev/null
    echo -e "${YELLOW}注意: 生产环境请使用正式SSL证书${NC}"
fi

echo -e "${GREEN}✓ SSL证书准备完成${NC}"
echo ""

# 询问部署模式
echo "请选择部署模式:"
echo "1) 开发环境 (单实例)"
echo "2) 生产环境 (高可用集群)"
read -p "请输入选项 [1-2]: " DEPLOY_MODE

if [ "$DEPLOY_MODE" == "2" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    echo -e "${GREEN}使用生产环境配置${NC}"
else
    COMPOSE_FILE="docker-compose.yml"
    echo -e "${GREEN}使用开发环境配置${NC}"
fi

echo ""

# 拉取镜像
echo "拉取Docker镜像..."
docker-compose -f $COMPOSE_FILE pull

echo -e "${GREEN}✓ 镜像拉取完成${NC}"
echo ""

# 构建应用镜像
echo "构建应用镜像..."
docker-compose -f $COMPOSE_FILE build

echo -e "${GREEN}✓ 镜像构建完成${NC}"
echo ""

# 启动基础设施
echo "启动基础设施 (MySQL, Redis, RabbitMQ, Nacos)..."
if [ "$DEPLOY_MODE" == "2" ]; then
    docker-compose -f $COMPOSE_FILE up -d \
      mysql-master mysql-slave-1 mysql-slave-2 \
      redis-master redis-slave-1 redis-slave-2 \
      redis-sentinel-1 redis-sentinel-2 redis-sentinel-3 \
      rabbitmq-1 rabbitmq-2 rabbitmq-3 \
      nacos
else
    docker-compose -f $COMPOSE_FILE up -d mysql redis rabbitmq nacos
fi

echo "等待基础设施启动..."
sleep 60

echo -e "${GREEN}✓ 基础设施启动完成${NC}"
echo ""

# 初始化数据库用户
if [ "$DEPLOY_MODE" == "2" ]; then
    echo "初始化数据库用户..."
    ./scripts/init-db-users.sh
    echo -e "${GREEN}✓ 数据库用户初始化完成${NC}"
    echo ""
fi

# 启动监控组件
echo "启动监控组件 (Prometheus, Grafana, ELK, Zipkin)..."
if [ "$DEPLOY_MODE" == "2" ]; then
    docker-compose -f $COMPOSE_FILE up -d \
      prometheus grafana alertmanager \
      elasticsearch logstash kibana \
      zipkin
else
    docker-compose -f $COMPOSE_FILE up -d zipkin
fi

echo "等待监控组件启动..."
sleep 30

echo -e "${GREEN}✓ 监控组件启动完成${NC}"
echo ""

# 启动业务服务
echo "启动业务服务..."
docker-compose -f $COMPOSE_FILE up -d

echo "等待业务服务启动..."
sleep 60

echo -e "${GREEN}✓ 业务服务启动完成${NC}"
echo ""

# 健康检查
echo "执行健康检查..."
HEALTH_CHECK_FAILED=0

# 检查Gateway
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}✓ Gateway健康${NC}"
else
    echo -e "${RED}✗ Gateway不健康${NC}"
    HEALTH_CHECK_FAILED=1
fi

# 检查Nacos
if curl -f -s http://localhost:8848/nacos/actuator/health > /dev/null; then
    echo -e "${GREEN}✓ Nacos健康${NC}"
else
    echo -e "${RED}✗ Nacos不健康${NC}"
    HEALTH_CHECK_FAILED=1
fi

# 检查Prometheus
if [ "$DEPLOY_MODE" == "2" ]; then
    if curl -f -s http://localhost:9090/-/healthy > /dev/null; then
        echo -e "${GREEN}✓ Prometheus健康${NC}"
    else
        echo -e "${RED}✗ Prometheus不健康${NC}"
        HEALTH_CHECK_FAILED=1
    fi
fi

echo ""

if [ $HEALTH_CHECK_FAILED -eq 0 ]; then
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}  部署成功！${NC}"
    echo -e "${GREEN}=========================================${NC}"
    echo ""
    echo "访问地址:"
    echo "  - 前端: http://localhost"
    echo "  - API网关: http://localhost:8080"
    echo "  - Nacos: http://localhost:8848/nacos (nacos/nacos)"
    echo "  - Swagger: http://localhost:8080/swagger-ui.html"
    
    if [ "$DEPLOY_MODE" == "2" ]; then
        echo "  - Grafana: http://localhost:3000 (admin/admin)"
        echo "  - Prometheus: http://localhost:9090"
        echo "  - Kibana: http://localhost:5601"
        echo "  - Zipkin: http://localhost:9411"
    fi
    
    echo ""
    echo "查看日志:"
    echo "  docker-compose -f $COMPOSE_FILE logs -f"
    echo ""
    echo "停止服务:"
    echo "  docker-compose -f $COMPOSE_FILE down"
    echo ""
else
    echo -e "${RED}=========================================${NC}"
    echo -e "${RED}  部署失败，请检查日志${NC}"
    echo -e "${RED}=========================================${NC}"
    echo ""
    echo "查看日志:"
    echo "  docker-compose -f $COMPOSE_FILE logs"
    echo ""
    exit 1
fi
