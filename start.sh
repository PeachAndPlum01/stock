#!/bin/bash

echo "=========================================="
echo "股票投资信息展示系统 - 启动脚本"
echo "=========================================="

# 检查MySQL
echo "检查MySQL服务..."
if ! command -v mysql &> /dev/null; then
    echo "警告: 未检测到MySQL，请先安装MySQL"
fi

# 检查Redis
echo "检查Redis服务..."
if ! command -v redis-cli &> /dev/null; then
    echo "警告: 未检测到Redis，请先安装Redis"
fi

# 检查RabbitMQ
echo "检查RabbitMQ服务..."
if ! command -v rabbitmqctl &> /dev/null; then
    echo "警告: 未检测到RabbitMQ，请先安装RabbitMQ"
fi

echo ""
echo "=========================================="
echo "启动说明："
echo "1. 确保MySQL、Redis、RabbitMQ服务已启动"
echo "2. 执行 sql/init.sql 初始化数据库"
echo "3. 后端启动: cd backend && mvn spring-boot:run"
echo "4. 前端启动: cd frontend && npm install && npm run dev"
echo "=========================================="
echo ""
echo "访问地址："
echo "前端: http://localhost:5173"
echo "后端: http://localhost:8080/api"
echo "默认账号: admin / 123456"
echo "=========================================="
