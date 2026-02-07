#!/bin/bash
set -e

# 等待RabbitMQ启动
sleep 30

# 启动RabbitMQ服务器
rabbitmq-server -detached

# 等待服务启动
sleep 10

# 停止应用
rabbitmqctl stop_app

# 加入集群
rabbitmqctl join_cluster rabbit@$1

# 启动应用
rabbitmqctl start_app

# 设置镜像队列策略
rabbitmqctl set_policy ha-all "^" '{"ha-mode":"all","ha-sync-mode":"automatic"}'

# 保持容器运行
tail -f /dev/null
