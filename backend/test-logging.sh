#!/bin/bash

# 日志系统测试脚本

echo "================================"
echo "日志系统测试"
echo "================================"
echo ""

# 检查日志目录
if [ -d "logs" ]; then
    echo "✅ 日志目录存在: logs/"
    echo ""
    echo "📁 日志文件列表:"
    ls -lh logs/*.log 2>/dev/null || echo "   暂无日志文件"
    echo ""
else
    echo "⚠️  日志目录不存在，将在应用启动后自动创建"
    echo ""
fi

# 检查配置文件
echo "📋 检查配置文件:"
if [ -f "src/main/resources/logback-spring.xml" ]; then
    echo "✅ logback-spring.xml 配置文件存在"
else
    echo "❌ logback-spring.xml 配置文件不存在"
fi

if [ -f "src/main/resources/application.yml" ]; then
    echo "✅ application.yml 配置文件存在"
else
    echo "❌ application.yml 配置文件不存在"
fi

echo ""
echo "================================"
echo "测试建议:"
echo "================================"
echo "1. 启动应用: mvn spring-boot:run"
echo "2. 访问登录接口触发日志记录"
echo "3. 查看日志文件: tail -f logs/all.log"
echo "4. 查看错误日志: tail -f logs/error.log"
echo "5. 调用日志API: curl http://localhost:8080/api/logs/files"
echo ""
echo "================================"
echo "日志文件说明:"
echo "================================"
echo "- all.log    : 所有级别日志"
echo "- error.log  : ERROR 级别日志"
echo "- warn.log   : WARN 级别日志"
echo "- info.log   : INFO 级别日志"
echo "- debug.log  : DEBUG 级别日志"
echo ""
