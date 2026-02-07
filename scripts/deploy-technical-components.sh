#!/bin/bash

# 股票系统技术组件部署脚本
# 用途：快速部署新增的技术组件

set -e

echo "========================================="
echo "  股票系统技术组件部署脚本"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Docker是否运行
check_docker() {
    echo -e "${YELLOW}[1/6] 检查Docker环境...${NC}"
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}错误: Docker未运行，请先启动Docker${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker运行正常${NC}"
    echo ""
}

# 创建必要的目录
create_directories() {
    echo -e "${YELLOW}[2/6] 创建数据目录...${NC}"
    sudo mkdir -p /opt/stock/docker-data/{mysql,redis,rabbitmq,nacos/{data,logs},influxdb/{data,config}}
    sudo chmod -R 777 /opt/stock/docker-data
    echo -e "${GREEN}✓ 数据目录创建完成${NC}"
    echo ""
}

# 编译项目
build_project() {
    echo -e "${YELLOW}[3/6] 编译项目...${NC}"
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ 项目编译成功${NC}"
    else
        echo -e "${RED}✗ 项目编译失败${NC}"
        exit 1
    fi
    echo ""
}

# 启动基础设施
start_infrastructure() {
    echo -e "${YELLOW}[4/6] 启动基础设施...${NC}"
    docker-compose up -d mysql redis rabbitmq nacos influxdb zipkin
    
    echo "等待基础设施启动..."
    sleep 30
    
    # 检查服务状态
    echo "检查服务状态..."
    docker-compose ps
    echo -e "${GREEN}✓ 基础设施启动完成${NC}"
    echo ""
}

# 启动微服务
start_microservices() {
    echo -e "${YELLOW}[5/6] 启动微服务...${NC}"
    docker-compose up -d stock-admin-server stock-gateway stock-auth-service \
        stock-data-service stock-investment-service stock-correlation-service \
        stock-realtime-service
    
    echo "等待微服务启动..."
    sleep 40
    
    echo -e "${GREEN}✓ 微服务启动完成${NC}"
    echo ""
}

# 验证部署
verify_deployment() {
    echo -e "${YELLOW}[6/6] 验证部署...${NC}"
    
    services=(
        "http://localhost:8080/actuator/health|API网关"
        "http://localhost:8081/actuator/health|认证服务"
        "http://localhost:8082/actuator/health|数据服务"
        "http://localhost:8083/actuator/health|投资服务"
        "http://localhost:8084/actuator/health|关联服务"
        "http://localhost:8085/actuator/health|实时行情服务"
        "http://localhost:8086/health|InfluxDB"
        "http://localhost:8090/actuator/health|监控服务"
        "http://localhost:9411/health|Zipkin"
    )
    
    echo "检查服务健康状态..."
    for service in "${services[@]}"; do
        IFS='|' read -r url name <<< "$service"
        if curl -s -f "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $name 运行正常${NC}"
        else
            echo -e "${RED}✗ $name 启动失败${NC}"
        fi
    done
    echo ""
}

# 显示访问信息
show_access_info() {
    echo ""
    echo "========================================="
    echo "  部署完成！"
    echo "========================================="
    echo ""
    echo "📊 服务访问地址："
    echo "  - API网关:        http://localhost:8080"
    echo "  - 实时行情服务:   http://localhost:8085"
    echo "  - 监控服务:       http://localhost:8090 (admin/admin123)"
    echo "  - InfluxDB:       http://localhost:8086 (admin/admin123456)"
    echo "  - Zipkin:         http://localhost:9411"
    echo "  - Nacos:          http://localhost:8848/nacos (nacos/nacos)"
    echo ""
    echo "🧪 测试命令："
    echo "  # 生成模拟行情"
    echo "  curl -X POST http://localhost:8085/api/realtime/quote/mock/000001?stockName=平安银行"
    echo ""
    echo "  # 获取实时行情"
    echo "  curl http://localhost:8085/api/realtime/quote/000001"
    echo ""
    echo "  # WebSocket连接"
    echo "  ws://localhost:8085/ws/stock"
    echo ""
    echo "📚 文档："
    echo "  - 技术组件文档: docs/TECHNICAL-COMPONENTS.md"
    echo "  - 部署文档:     docs/DEPLOYMENT.md"
    echo ""
    echo "🔍 查看日志："
    echo "  docker-compose logs -f stock-realtime-service"
    echo ""
}

# 主函数
main() {
    check_docker
    create_directories
    build_project
    start_infrastructure
    start_microservices
    verify_deployment
    show_access_info
}

# 执行主函数
main
