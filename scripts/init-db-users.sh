#!/bin/bash
# 数据库初始化脚本 - 创建独立用户账号

set -e

echo "开始创建数据库用户..."

# 等待MySQL启动
sleep 10

# 创建认证服务用户
mysql -uroot -p${MYSQL_ROOT_PASSWORD} <<EOF
-- 认证服务用户（只能访问用户和角色表）
CREATE USER IF NOT EXISTS '${MYSQL_AUTH_USER}'@'%' IDENTIFIED BY '${MYSQL_AUTH_PASSWORD}';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.users TO '${MYSQL_AUTH_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.roles TO '${MYSQL_AUTH_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.user_roles TO '${MYSQL_AUTH_USER}'@'%';
FLUSH PRIVILEGES;
EOF

echo "认证服务用户创建完成"

# 创建数据服务用户
mysql -uroot -p${MYSQL_ROOT_PASSWORD} <<EOF
-- 数据服务用户（可以访问股票数据表）
CREATE USER IF NOT EXISTS '${MYSQL_DATA_USER}'@'%' IDENTIFIED BY '${MYSQL_DATA_PASSWORD}';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.stock_basic TO '${MYSQL_DATA_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.stock_daily TO '${MYSQL_DATA_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.stock_company TO '${MYSQL_DATA_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.stock_financial TO '${MYSQL_DATA_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.stock_indicator TO '${MYSQL_DATA_USER}'@'%';
FLUSH PRIVILEGES;
EOF

echo "数据服务用户创建完成"

# 创建投资服务用户
mysql -uroot -p${MYSQL_ROOT_PASSWORD} <<EOF
-- 投资服务用户（可以访问投资组合表）
CREATE USER IF NOT EXISTS '${MYSQL_INVESTMENT_USER}'@'%' IDENTIFIED BY '${MYSQL_INVESTMENT_PASSWORD}';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.portfolios TO '${MYSQL_INVESTMENT_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.portfolio_stocks TO '${MYSQL_INVESTMENT_USER}'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.transactions TO '${MYSQL_INVESTMENT_USER}'@'%';
GRANT SELECT ON ${MYSQL_DATABASE}.stock_basic TO '${MYSQL_INVESTMENT_USER}'@'%';
GRANT SELECT ON ${MYSQL_DATABASE}.stock_daily TO '${MYSQL_INVESTMENT_USER}'@'%';
FLUSH PRIVILEGES;
EOF

echo "投资服务用户创建完成"

# 创建关联服务用户
mysql -uroot -p${MYSQL_ROOT_PASSWORD} <<EOF
-- 关联服务用户（只读权限）
CREATE USER IF NOT EXISTS '${MYSQL_CORRELATION_USER}'@'%' IDENTIFIED BY '${MYSQL_CORRELATION_PASSWORD}';
GRANT SELECT ON ${MYSQL_DATABASE}.* TO '${MYSQL_CORRELATION_USER}'@'%';
FLUSH PRIVILEGES;
EOF

echo "关联服务用户创建完成"

# 创建Zipkin数据库和用户
mysql -uroot -p${MYSQL_ROOT_PASSWORD} <<EOF
-- 创建Zipkin数据库
CREATE DATABASE IF NOT EXISTS zipkin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Zipkin表结构
USE zipkin;

CREATE TABLE IF NOT EXISTS zipkin_spans (
  trace_id_high BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
  trace_id BIGINT NOT NULL,
  id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  parent_id BIGINT,
  debug BIT(1),
  start_ts BIGINT COMMENT 'Span.timestamp(): epoch micros used for endTs query and to implement TTL',
  duration BIGINT COMMENT 'Span.duration(): micros used for minDuration and maxDuration query',
  PRIMARY KEY (trace_id_high, trace_id, id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS zipkin_annotations (
  trace_id_high BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
  trace_id BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.trace_id',
  span_id BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.id',
  a_key VARCHAR(255) NOT NULL COMMENT 'BinaryAnnotation.key or Annotation.value if type == -1',
  a_value BLOB COMMENT 'BinaryAnnotation.value(), which must be smaller than 64KB',
  a_type INT NOT NULL COMMENT 'BinaryAnnotation.type() or -1 if Annotation',
  a_timestamp BIGINT COMMENT 'Used to implement TTL; Annotation.timestamp or zipkin_spans.timestamp',
  endpoint_ipv4 INT COMMENT 'Null when Binary/Annotation.endpoint is null',
  endpoint_ipv6 BINARY(16) COMMENT 'Null when Binary/Annotation.endpoint is null, or no IPv6 address',
  endpoint_port SMALLINT COMMENT 'Null when Binary/Annotation.endpoint is null',
  endpoint_service_name VARCHAR(255) COMMENT 'Null when Binary/Annotation.endpoint is null'
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS zipkin_dependencies (
  day DATE NOT NULL,
  parent VARCHAR(255) NOT NULL,
  child VARCHAR(255) NOT NULL,
  call_count BIGINT,
  error_count BIGINT,
  PRIMARY KEY (day, parent, child)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;

-- 创建索引
ALTER TABLE zipkin_spans ADD INDEX(trace_id_high, trace_id) COMMENT 'for getTracesByIds';
ALTER TABLE zipkin_spans ADD INDEX(name) COMMENT 'for getTraces and getSpanNames';
ALTER TABLE zipkin_spans ADD INDEX(start_ts) COMMENT 'for getTraces ordering and range';

ALTER TABLE zipkin_annotations ADD INDEX(trace_id_high, trace_id, span_id) COMMENT 'for joining with zipkin_spans';
ALTER TABLE zipkin_annotations ADD INDEX(trace_id_high, trace_id) COMMENT 'for getTraces/ByIds';
ALTER TABLE zipkin_annotations ADD INDEX(endpoint_service_name) COMMENT 'for getTraces and getServiceNames';
ALTER TABLE zipkin_annotations ADD INDEX(a_type) COMMENT 'for getTraces and autocomplete values';
ALTER TABLE zipkin_annotations ADD INDEX(a_key) COMMENT 'for getTraces and autocomplete values';
ALTER TABLE zipkin_annotations ADD INDEX(trace_id, span_id, a_key) COMMENT 'for dependencies job';

FLUSH PRIVILEGES;
EOF

echo "Zipkin数据库创建完成"

echo "所有数据库用户创建完成！"
