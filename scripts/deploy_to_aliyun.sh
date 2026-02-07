#!/bin/bash

# 阿里云部署脚本
# 用法: ./scripts/deploy_to_aliyun.sh

SERVER_IP="101.132.185.180"
REMOTE_DIR="/root/stock-app"

echo "=================================================="
echo "   Stock App 阿里云部署脚本 (Lite版)"
echo "=================================================="

# 1. 获取 SSH 配置
read -p "请输入 SSH 密钥文件的本地绝对路径 (例如 /Users/xxx/my-key.pem): " SSH_KEY_PATH
if [ ! -f "$SSH_KEY_PATH" ]; then
    echo "错误: 找不到密钥文件: $SSH_KEY_PATH"
    exit 1
fi

read -p "请输入 SSH 用户名 [默认: root]: " SSH_USER
SSH_USER=${SSH_USER:-root}

echo "正在测试连接..."
ssh -i "$SSH_KEY_PATH" -o StrictHostKeyChecking=no "$SSH_USER@$SERVER_IP" "echo '连接成功'"
if [ $? -ne 0 ]; then
    echo "错误: 无法连接到服务器"
    exit 1
fi

# 2. 本地构建
echo "--------------------------------------------------"
echo "开始本地构建..."

# 构建前端
echo "正在构建前端..."
cd frontend
npm install
npm run build
if [ $? -ne 0 ]; then
    echo "错误: 前端构建失败"
    exit 1
fi
cd ..

# 构建后端
echo "正在构建后端..."
cd stock-auth-service
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "错误: 后端构建失败"
    exit 1
fi
cd ..

# 3. 准备远程环境
echo "--------------------------------------------------"
echo "正在准备远程环境..."

ssh -i "$SSH_KEY_PATH" "$SSH_USER@$SERVER_IP" "mkdir -p $REMOTE_DIR/frontend $REMOTE_DIR/stock-auth-service $REMOTE_DIR/config $REMOTE_DIR/logs"

# 4. 上传文件
echo "--------------------------------------------------"
echo "正在上传文件..."

# 上传 docker-compose.lite.yml
scp -i "$SSH_KEY_PATH" docker-compose.lite.yml "$SSH_USER@$SERVER_IP:$REMOTE_DIR/docker-compose.yml"

# 上传 Nginx 配置
scp -i "$SSH_KEY_PATH" config/nginx-lite.conf "$SSH_USER@$SERVER_IP:$REMOTE_DIR/config/nginx-lite.conf"

# 上传前端构建产物
echo "上传前端文件..."
scp -i "$SSH_KEY_PATH" -r frontend/dist/* "$SSH_USER@$SERVER_IP:$REMOTE_DIR/frontend/"

# 上传后端构建产物和 Dockerfile
echo "上传后端文件..."
ssh -i "$SSH_KEY_PATH" "$SSH_USER@$SERVER_IP" "mkdir -p $REMOTE_DIR/stock-auth-service/target"
scp -i "$SSH_KEY_PATH" stock-auth-service/target/*.jar "$SSH_USER@$SERVER_IP:$REMOTE_DIR/stock-auth-service/target/"
scp -i "$SSH_KEY_PATH" stock-auth-service/Dockerfile "$SSH_USER@$SERVER_IP:$REMOTE_DIR/stock-auth-service/"

# 5. 启动服务
echo "--------------------------------------------------"
echo "正在启动服务..."

ssh -i "$SSH_KEY_PATH" "$SSH_USER@$SERVER_IP" "cd $REMOTE_DIR && \
    # 检查是否安装 Docker
    if ! command -v docker &> /dev/null; then
        echo '安装 Docker...'
        curl -fsSL https://get.docker.com | bash
        systemctl enable docker
        systemctl start docker
    fi
    
    # 启动服务
    echo '启动 Docker Compose...'
    
    # 尝试停止可能存在的 Web 服务
    echo '检查并停止占用 80 端口的服务...'
    
    if command -v systemctl &> /dev/null; then
        systemctl stop nginx 2>/dev/null || true
        systemctl stop httpd 2>/dev/null || true
        systemctl stop apache2 2>/dev/null || true
    fi
    
    # 强制清理 80 端口占用 (如果 lsof/fuser 存在)
    if command -v fuser &> /dev/null; then
        fuser -k 80/tcp 2>/dev/null || true
    elif command -v lsof &> /dev/null; then
        # 获取占用 80 端口的 PID 并杀掉
        pids=$(lsof -t -i:80)
        if [ -n "$pids" ]; then
            kill -9 $pids 2>/dev/null || true
        fi
    fi

    chmod -R 755 frontend
    docker compose down
    docker compose up -d --build
    
    echo '服务状态:'
    docker compose ps
"

echo "=================================================="
echo "部署完成!"
echo "访问地址: http://$SERVER_IP"
echo "=================================================="