#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   股票投资信息展示系统 - 快速启动${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 检查Redis
echo -e "${YELLOW}[1/5] 检查Redis...${NC}"
if redis-cli ping > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Redis已启动${NC}"
else
    echo -e "${RED}❌ Redis未启动，请先启动Redis: redis-server${NC}"
    exit 1
fi
echo ""

# 启动认证服务
echo -e "${YELLOW}[2/5] 启动认证服务 (端口8081)...${NC}"
cd stock-auth-service
nohup java -jar target/stock-auth-service-1.0.0.jar > ../logs/auth-service.log 2>&1 &
AUTH_PID=$!
echo -e "${GREEN}✅ 认证服务已启动 (PID: $AUTH_PID)${NC}"
cd ..
echo ""

# 等待认证服务启动
echo -e "${YELLOW}等待认证服务启动...${NC}"
sleep 10

# 启动网关
echo -e "${YELLOW}[3/5] 启动API网关 (端口8080)...${NC}"
cd stock-gateway
mvn clean package -DskipTests > /dev/null 2>&1
nohup java -jar target/stock-gateway-1.0.0.jar > ../logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo -e "${GREEN}✅ API网关已启动 (PID: $GATEWAY_PID)${NC}"
cd ..
echo ""

# 等待网关启动
echo -e "${YELLOW}等待网关启动...${NC}"
sleep 10

# 检查服务状态
echo -e "${YELLOW}[4/5] 检查服务状态...${NC}"
if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 认证服务运行正常${NC}"
else
    echo -e "${RED}❌ 认证服务启动失败，请查看日志: logs/auth-service.log${NC}"
fi

if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ API网关运行正常${NC}"
else
    echo -e "${RED}❌ API网关启动失败，请查看日志: logs/gateway.log${NC}"
fi
echo ""

# 启动前端
echo -e "${YELLOW}[5/5] 启动前端服务 (端口5173)...${NC}"
cd frontend
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}⚠️  安装前端依赖...${NC}"
    npm install
fi
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ 后端服务已启动完成！${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}📝 服务信息：${NC}"
echo -e "   认证服务: ${GREEN}http://localhost:8081${NC}"
echo -e "   API网关:  ${GREEN}http://localhost:8080${NC}"
echo -e "   前端页面: ${GREEN}http://localhost:5173${NC}"
echo ""
echo -e "${YELLOW}📝 测试账号：${NC}"
echo -e "   管理员:   ${GREEN}admin / 123456${NC}"
echo -e "   测试用户: ${GREEN}test / 123456${NC}"
echo ""
echo -e "${YELLOW}💡 提示：${NC}"
echo -e "   - 前端即将启动，请稍候..."
echo -e "   - 按 ${RED}Ctrl+C${NC} 停止前端服务"
echo -e "   - 停止后端服务: ${GREEN}./stop-services.sh${NC}"
echo ""
echo -e "${YELLOW}📋 进程ID：${NC}"
echo -e "   认证服务: ${GREEN}$AUTH_PID${NC}"
echo -e "   API网关:  ${GREEN}$GATEWAY_PID${NC}"
echo ""

# 保存PID到文件
echo "$AUTH_PID" > ../logs/auth-service.pid
echo "$GATEWAY_PID" > ../logs/gateway.pid

# 启动前端（前台运行）
npm run dev
