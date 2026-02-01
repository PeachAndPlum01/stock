#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   天问 - 前端启动${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查Node.js
echo -e "${YELLOW}[1/4] 检查Node.js环境...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js未安装，请先安装Node.js${NC}"
    exit 1
fi
NODE_VERSION=$(node -v)
echo -e "${GREEN}✅ Node.js版本: $NODE_VERSION${NC}"
echo ""

# 检查npm
echo -e "${YELLOW}[2/4] 检查npm环境...${NC}"
if ! command -v npm &> /dev/null; then
    echo -e "${RED}❌ npm未安装${NC}"
    exit 1
fi
NPM_VERSION=$(npm -v)
echo -e "${GREEN}✅ npm版本: $NPM_VERSION${NC}"
echo ""

# 进入前端目录
cd "$(dirname "$0")/frontend"

# 检查依赖
echo -e "${YELLOW}[3/4] 检查依赖包...${NC}"
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}⚠️  依赖包未安装，开始安装...${NC}"
    npm install
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ 依赖安装失败${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ 依赖安装成功${NC}"
else
    echo -e "${GREEN}✅ 依赖包已存在${NC}"
fi
echo ""

# 启动前端
echo -e "${YELLOW}[4/4] 启动前端服务...${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ 前端服务启动中...${NC}"
echo -e "${GREEN}✅ 访问地址: http://localhost:5173${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}📝 测试账号：${NC}"
echo -e "   管理员: ${GREEN}admin / 123456${NC}"
echo -e "   测试用户: ${GREEN}test / 123456${NC}"
echo ""
echo -e "${YELLOW}💡 提示：${NC}"
echo -e "   - 按 ${RED}Ctrl+C${NC} 停止服务"
echo -e "   - 确保后端服务已启动（端口8080）"
echo -e "   - 确保MySQL、Redis已启动"
echo ""

npm run dev
