#!/bin/bash
# MySQL全量备份脚本

set -e

# 配置
BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# 创建备份目录
mkdir -p ${BACKUP_DIR}

echo "开始MySQL全量备份: ${DATE}"

# 执行备份
docker exec stock-mysql-master mysqldump \
  -uroot -p${MYSQL_ROOT_PASSWORD} \
  --all-databases \
  --single-transaction \
  --quick \
  --lock-tables=false \
  --routines \
  --triggers \
  --events \
  --master-data=2 \
  | gzip > ${BACKUP_DIR}/full_backup_${DATE}.sql.gz

# 检查备份文件
if [ -f "${BACKUP_DIR}/full_backup_${DATE}.sql.gz" ]; then
  BACKUP_SIZE=$(du -h "${BACKUP_DIR}/full_backup_${DATE}.sql.gz" | cut -f1)
  echo "备份完成: full_backup_${DATE}.sql.gz (${BACKUP_SIZE})"
else
  echo "备份失败！"
  exit 1
fi

# 清理旧备份
echo "清理${RETENTION_DAYS}天前的备份..."
find ${BACKUP_DIR} -name "full_backup_*.sql.gz" -mtime +${RETENTION_DAYS} -delete

# 备份到远程（可选）
# rsync -avz ${BACKUP_DIR}/full_backup_${DATE}.sql.gz user@remote-server:/backup/mysql/

echo "备份任务完成"
