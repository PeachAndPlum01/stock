#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   停止后端服务${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 停止认证服务
if [ -f "logs/auth-service.pid" ]; then
    AUTH_PID=$(cat logs/auth-service.pid)
    if ps -p $AUTH_PID > /dev/null 2>&1; then
        echo -e "${YELLOW}停止认证服务 (PID: $AUTH_PID)...${NC}"
        kill $AUTH_PID
        echo -e "${GREEN}✅ 认证服务已停止${NC}"
    else
        echo -e "${YELLOW}⚠️  认证服务未运行${NC}"
    fi
    rm -f logs/auth-service.pid
else
    echo -e "${YELLOW}⚠️  未找到认证服务PID文件${NC}"
fi

# 停止网关
if [ -f "logs/gateway.pid" ]; then
    GATEWAY_PID=$(cat logs/gateway.pid)
    if ps -p $GATEWAY_PID > /dev/null 2>&1; then
        echo -e "${YELLOW}停止API网关 (PID: $GATEWAY_PID)...${NC}"
        kill $GATEWAY_PID
        echo -e "${GREEN}✅ API网关已停止${NC}"
    else
        echo -e "${YELLOW}⚠️  API网关未运行${NC}"
    fi
    rm -f logs/gateway.pid
else
    echo -e "${YELLOW}⚠️  未找到API网关PID文件${NC}"
fi

# 额外检查并停止所有相关Java进程
echo ""
echo -e "${YELLOW}检查其他相关进程...${NC}"
STOCK_PIDS=$(ps aux | grep -E "stock-(auth|gateway)" | grep -v grep | awk '{print $2}')
if [ ! -z "$STOCK_PIDS" ]; then
    echo -e "${YELLOW}发现其他相关进程，正在停止...${NC}"
    echo "$STOCK_PIDS" | xargs kill 2>/dev/null
    echo -e "${GREEN}✅ 已停止所有相关进程${NC}"
else
    echo -e "${GREEN}✅ 没有其他相关进程${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   所有后端服务已停止${NC}"
echo -e "${GREEN}========================================${NC}"
