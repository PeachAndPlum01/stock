#!/bin/bash

# 日志查看快速启动脚本

echo "================================"
echo "📋 日志查看工具"
echo "================================"
echo ""

# 检查是否在 backend 目录
if [ ! -f "pom.xml" ]; then
    echo "❌ 请在 backend 目录下运行此脚本"
    echo "   cd backend && ./view-logs.sh"
    exit 1
fi

# 检查日志目录
if [ ! -d "logs" ]; then
    echo "⚠️  日志目录不存在"
    echo "   请先启动应用: mvn spring-boot:run"
    echo ""
    read -p "是否现在启动应用？(y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🚀 正在启动应用..."
        mvn spring-boot:run &
        echo "⏳ 等待应用启动（30秒）..."
        sleep 30
    else
        exit 0
    fi
fi

# 显示菜单
while true; do
    echo ""
    echo "================================"
    echo "请选择操作："
    echo "================================"
    echo "1. 实时查看所有日志 (all.log)"
    echo "2. 实时查看错误日志 (error.log)"
    echo "3. 实时查看警告日志 (warn.log)"
    echo "4. 查看最近100行日志"
    echo "5. 搜索日志内容"
    echo "6. 查看日志统计"
    echo "7. 查看日志文件列表"
    echo "8. 清空屏幕"
    echo "0. 退出"
    echo "================================"
    read -p "请输入选项 (0-8): " choice
    
    case $choice in
        1)
            echo "📄 实时查看所有日志（按 Ctrl+C 停止）..."
            sleep 1
            tail -f logs/all.log
            ;;
        2)
            echo "❌ 实时查看错误日志（按 Ctrl+C 停止）..."
            sleep 1
            tail -f logs/error.log
            ;;
        3)
            echo "⚠️  实时查看警告日志（按 Ctrl+C 停止）..."
            sleep 1
            tail -f logs/warn.log
            ;;
        4)
            echo "📋 查看最近100行日志："
            echo "--------------------------------"
            tail -n 100 logs/all.log
            echo "--------------------------------"
            read -p "按回车继续..."
            ;;
        5)
            read -p "请输入搜索关键词: " keyword
            if [ -n "$keyword" ]; then
                echo "🔍 搜索结果："
                echo "--------------------------------"
                grep --color -i "$keyword" logs/all.log | tail -n 50
                echo "--------------------------------"
                echo "显示最近50条匹配结果"
            fi
            read -p "按回车继续..."
            ;;
        6)
            echo "📊 日志统计信息："
            echo "--------------------------------"
            echo "文件大小："
            ls -lh logs/*.log 2>/dev/null | awk '{print $9, $5}'
            echo ""
            echo "日志数量统计："
            if [ -f "logs/all.log" ]; then
                echo "  ERROR: $(grep -c 'ERROR' logs/all.log 2>/dev/null || echo 0)"
                echo "  WARN:  $(grep -c 'WARN' logs/all.log 2>/dev/null || echo 0)"
                echo "  INFO:  $(grep -c 'INFO' logs/all.log 2>/dev/null || echo 0)"
                echo "  DEBUG: $(grep -c 'DEBUG' logs/all.log 2>/dev/null || echo 0)"
            else
                echo "  暂无日志数据"
            fi
            echo "--------------------------------"
            read -p "按回车继续..."
            ;;
        7)
            echo "📁 日志文件列表："
            echo "--------------------------------"
            ls -lh logs/ 2>/dev/null || echo "日志目录为空"
            echo "--------------------------------"
            read -p "按回车继续..."
            ;;
        8)
            clear
            echo "================================"
            echo "📋 日志查看工具"
            echo "================================"
            ;;
        0)
            echo "👋 再见！"
            exit 0
            ;;
        *)
            echo "❌ 无效选项，请重新选择"
            sleep 1
            ;;
    esac
done
