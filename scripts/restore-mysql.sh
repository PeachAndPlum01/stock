#!/bin/bash
# MySQL恢复脚本

set -e

# 检查参数
if [ $# -lt 1 ]; then
  echo "用法: $0 <备份文件路径>"
  echo "示例: $0 /backup/mysql/full_backup_20260206_020000.sql.gz"
  exit 1
fi

BACKUP_FILE=$1

# 检查备份文件是否存在
if [ ! -f "${BACKUP_FILE}" ]; then
  echo "错误: 备份文件不存在: ${BACKUP_FILE}"
  exit 1
fi

echo "警告: 此操作将恢复数据库，可能会覆盖现有数据！"
read -p "确认继续? (yes/no): " CONFIRM

if [ "${CONFIRM}" != "yes" ]; then
  echo "操作已取消"
  exit 0
fi

echo "开始恢复数据库..."

# 停止所有业务服务
echo "停止业务服务..."
docker-compose stop stock-gateway stock-auth-service stock-data-service \
  stock-investment-service stock-correlation-service

# 恢复数据库
echo "恢复数据库: ${BACKUP_FILE}"
gunzip < ${BACKUP_FILE} | docker exec -i stock-mysql-master mysql -uroot -p${MYSQL_ROOT_PASSWORD}

# 检查恢复结果
if [ $? -eq 0 ]; then
  echo "数据库恢复成功"
else
  echo "数据库恢复失败！"
  exit 1
fi

# 重启业务服务
echo "重启业务服务..."
docker-compose start stock-gateway stock-auth-service stock-data-service \
  stock-investment-service stock-correlation-service

echo "恢复完成！"
