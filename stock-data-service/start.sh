#!/bin/bash

# 股票数据微服务启动脚本

echo "========================================"
echo "启动股票数据微服务"
echo "========================================"

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 8或更高版本"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 设置Tushare Token（如果未设置环境变量）
if [ -z "$TUSHARE_TOKEN" ]; then
    echo "警告: 未设置TUSHARE_TOKEN环境变量"
    echo "请在 application.yml 中配置 tushare.token，或设置环境变量："
    echo "export TUSHARE_TOKEN=your_token_here"
    echo ""
fi

# 编译项目
echo "正在编译项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "错误: 编译失败"
    exit 1
fi

# 启动服务
echo "正在启动服务..."
echo "========================================"
nohup java -jar target/stock-data-service-1.0.0.jar > logs/stock-data-service.log 2>&1 &

# 获取进程ID
PID=$!
echo $PID > stock-data-service.pid

echo "服务已启动，进程ID: $PID"
echo "日志文件: logs/stock-data-service.log"
echo "访问地址: http://localhost:8082"
echo "健康检查: http://localhost:8082/api/stock/health"
echo "========================================"

# 等待服务启动
echo "等待服务启动..."
sleep 5

# 检查服务是否启动成功
if ps -p $PID > /dev/null; then
    echo "✓ 服务启动成功！"
else
    echo "✗ 服务启动失败，请查看日志"
    exit 1
fi
