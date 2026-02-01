#!/bin/bash

# 股票数据微服务停止脚本

echo "========================================"
echo "停止股票数据微服务"
echo "========================================"

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 读取PID文件
if [ -f "stock-data-service.pid" ]; then
    PID=$(cat stock-data-service.pid)
    
    if ps -p $PID > /dev/null; then
        echo "正在停止服务 (PID: $PID)..."
        kill $PID
        
        # 等待进程结束
        for i in {1..10}; do
            if ! ps -p $PID > /dev/null; then
                echo "✓ 服务已成功停止"
                rm -f stock-data-service.pid
                exit 0
            fi
            sleep 1
        done
        
        # 如果进程还在运行，强制杀死
        echo "进程未正常结束，强制停止..."
        kill -9 $PID
        rm -f stock-data-service.pid
        echo "✓ 服务已强制停止"
    else
        echo "服务未运行"
        rm -f stock-data-service.pid
    fi
else
    echo "未找到PID文件，尝试查找运行中的进程..."
    
    # 查找Java进程
    PID=$(ps aux | grep "stock-data-service" | grep -v grep | awk '{print $2}')
    
    if [ -n "$PID" ]; then
        echo "找到进程 (PID: $PID)，正在停止..."
        kill $PID
        sleep 2
        
        if ps -p $PID > /dev/null; then
            kill -9 $PID
        fi
        
        echo "✓ 服务已停止"
    else
        echo "未找到运行中的服务"
    fi
fi

echo "========================================"
