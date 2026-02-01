#!/bin/bash

# 股票投资信息展示系统 - 微服务架构
# 启动所有服务脚本

echo "========================================"
echo "股票投资信息展示系统 - 微服务启动"
echo "========================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo -e "${RED}错误: 未找到Java环境，请先安装Java 8或更高版本${NC}"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}错误: 未找到Maven，请先安装Maven${NC}"
    exit 1
fi

# 检查MySQL
echo "检查MySQL服务..."
if ! pgrep -x "mysqld" > /dev/null; then
    echo -e "${YELLOW}警告: MySQL服务未运行，请先启动MySQL${NC}"
fi

# 检查Redis
echo "检查Redis服务..."
if ! pgrep -x "redis-server" > /dev/null; then
    echo -e "${YELLOW}警告: Redis服务未运行，请先启动Redis${NC}"
fi

# 检查RabbitMQ
echo "检查RabbitMQ服务..."
if ! pgrep -f "rabbitmq" > /dev/null; then
    echo -e "${YELLOW}警告: RabbitMQ服务未运行，请先启动RabbitMQ${NC}"
fi

echo ""
echo "========================================"
echo "开始编译所有服务..."
echo "========================================"

# 编译父项目
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}编译失败，请检查错误信息${NC}"
    exit 1
fi

echo -e "${GREEN}编译成功！${NC}"
echo ""

# 创建日志目录
mkdir -p logs
mkdir -p stock-gateway/logs
mkdir -p stock-auth-service/logs
mkdir -p stock-data-service/logs
mkdir -p stock-investment-service/logs
mkdir -p stock-correlation-service/logs

# 启动服务函数
start_service() {
    local service_name=$1
    local service_port=$2
    local service_dir=$3
    
    echo "========================================"
    echo "启动 ${service_name} (端口: ${service_port})..."
    echo "========================================"
    
    cd "$SCRIPT_DIR/$service_dir"
    
    # 检查端口是否被占用
    if lsof -Pi :${service_port} -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${YELLOW}警告: 端口 ${service_port} 已被占用${NC}"
        echo "正在尝试停止占用端口的进程..."
        lsof -ti:${service_port} | xargs kill -9 2>/dev/null
        sleep 2
    fi
    
    # 启动服务
    nohup mvn spring-boot:run > logs/${service_name}.log 2>&1 &
    local pid=$!
    echo $pid > ${service_name}.pid
    
    echo "服务已启动，PID: $pid"
    echo "日志文件: $service_dir/logs/${service_name}.log"
    
    # 等待服务启动
    echo "等待服务启动..."
    sleep 10
    
    # 检查服务是否启动成功
    if ps -p $pid > /dev/null; then
        echo -e "${GREEN}✓ ${service_name} 启动成功！${NC}"
    else
        echo -e "${RED}✗ ${service_name} 启动失败，请查看日志${NC}"
    fi
    
    echo ""
    cd "$SCRIPT_DIR"
}

# 按顺序启动各服务
echo ""
echo "========================================"
echo "开始启动微服务..."
echo "========================================"
echo ""

# 1. 启动认证服务
start_service "stock-auth-service" 8081 "stock-auth-service"

# 2. 启动股票数据服务
start_service "stock-data-service" 8082 "stock-data-service"

# 3. 启动投资信息服务
start_service "stock-investment-service" 8083 "stock-investment-service"

# 4. 启动省份关联服务
start_service "stock-correlation-service" 8084 "stock-correlation-service"

# 5. 启动API网关
start_service "stock-gateway" 8080 "stock-gateway"

echo "========================================"
echo "所有服务启动完成！"
echo "========================================"
echo ""
echo "服务列表："
echo "  - API网关:        http://localhost:8080"
echo "  - 认证服务:       http://localhost:8081"
echo "  - 股票数据服务:   http://localhost:8082"
echo "  - 投资信息服务:   http://localhost:8083"
echo "  - 省份关联服务:   http://localhost:8084"
echo ""
echo "健康检查："
echo "  curl http://localhost:8080/actuator/health"
echo ""
echo "查看日志："
echo "  tail -f */logs/*.log"
echo ""
echo "停止所有服务："
echo "  ./stop-all.sh"
echo ""
echo "========================================"
