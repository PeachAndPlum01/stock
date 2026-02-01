#!/bin/bash

# 股票投资信息展示系统 - 微服务架构
# 停止所有服务脚本

echo "========================================"
echo "股票投资信息展示系统 - 停止所有服务"
echo "========================================"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 停止服务函数
stop_service() {
    local service_name=$1
    local service_dir=$2
    
    echo "停止 ${service_name}..."
    
    cd "$SCRIPT_DIR/$service_dir"
    
    # 读取PID文件
    if [ -f "${service_name}.pid" ]; then
        local pid=$(cat ${service_name}.pid)
        
        if ps -p $pid > /dev/null; then
            echo "正在停止服务 (PID: $pid)..."
            kill $pid
            
            # 等待进程结束
            for i in {1..10}; do
                if ! ps -p $pid > /dev/null; then
                    echo -e "${GREEN}✓ ${service_name} 已成功停止${NC}"
                    rm -f ${service_name}.pid
                    cd "$SCRIPT_DIR"
                    return 0
                fi
                sleep 1
            done
            
            # 如果进程还在运行，强制杀死
            echo "进程未正常结束，强制停止..."
            kill -9 $pid 2>/dev/null
            rm -f ${service_name}.pid
            echo -e "${GREEN}✓ ${service_name} 已强制停止${NC}"
        else
            echo -e "${YELLOW}服务未运行${NC}"
            rm -f ${service_name}.pid
        fi
    else
        echo -e "${YELLOW}未找到PID文件，尝试查找运行中的进程...${NC}"
        
        # 查找Java进程
        local pid=$(ps aux | grep "${service_dir}" | grep -v grep | awk '{print $2}')
        
        if [ -n "$pid" ]; then
            echo "找到进程 (PID: $pid)，正在停止..."
            kill $pid 2>/dev/null
            sleep 2
            
            if ps -p $pid > /dev/null; then
                kill -9 $pid 2>/dev/null
            fi
            
            echo -e "${GREEN}✓ ${service_name} 已停止${NC}"
        else
            echo -e "${YELLOW}未找到运行中的服务${NC}"
        fi
    fi
    
    echo ""
    cd "$SCRIPT_DIR"
}

# 按顺序停止各服务（与启动顺序相反）
echo ""

# 1. 停止API网关
stop_service "stock-gateway" "stock-gateway"

# 2. 停止省份关联服务
stop_service "stock-correlation-service" "stock-correlation-service"

# 3. 停止投资信息服务
stop_service "stock-investment-service" "stock-investment-service"

# 4. 停止股票数据服务
stop_service "stock-data-service" "stock-data-service"

# 5. 停止认证服务
stop_service "stock-auth-service" "stock-auth-service"

echo "========================================"
echo "所有服务已停止"
echo "========================================"
