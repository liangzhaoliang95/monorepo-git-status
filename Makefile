.PHONY: help build clean tag release

# 默认目标
help:
	@echo "Monorepo Git Status - Makefile"
	@echo ""
	@echo "可用命令:"
	@echo "  make build        - 本地构建插件"
	@echo "  make clean        - 清理构建文件"
	@echo "  make tag VERSION=x.x.x - 创建版本标签"
	@echo "  make release VERSION=x.x.x - 创建标签并推送（触发自动发布）"
	@echo ""
	@echo "示例:"
	@echo "  make release VERSION=1.0.9"

# 本地构建
build:
	@echo "🔨 开始构建插件..."
	./gradlew clean buildPlugin
	@echo "✅ 构建完成！文件位置: build/distributions/"
	@ls -lh build/distributions/*.zip

# 清理
clean:
	@echo "🧹 清理构建文件..."
	./gradlew clean
	@echo "✅ 清理完成！"

# 创建标签（但不推送）
tag:
	@if [ -z "$(VERSION)" ]; then \
		echo "❌ 错误: 请指定版本号"; \
		echo "用法: make tag VERSION=1.0.9"; \
		exit 1; \
	fi
	@echo "🏷️  创建标签 v$(VERSION)..."
	@git tag -a "v$(VERSION)" -m "Release version $(VERSION)"
	@echo "✅ 标签创建成功！"
	@echo ""
	@echo "提示: 使用 'git push origin v$(VERSION)' 推送标签"
	@echo "或使用 'make release VERSION=$(VERSION)' 自动推送并触发发布"

# 发布：创建标签并推送（触发 GitHub Actions）
release:
	@if [ -z "$(VERSION)" ]; then \
		echo "❌ 错误: 请指定版本号"; \
		echo "用法: make release VERSION=1.0.9"; \
		exit 1; \
	fi
	@echo "📦 准备发布版本 $(VERSION)..."
	@echo ""
	@echo "⚠️  请确认:"
	@echo "  1. 已更新 build.gradle.kts 中的版本号为 $(VERSION)"
	@echo "  2. 已更新 plugin.xml 中的 version 和 change-notes"
	@echo "  3. 已提交所有更改"
	@echo ""
	@read -p "确认继续? [y/N] " confirm; \
	if [ "$$confirm" != "y" ] && [ "$$confirm" != "Y" ]; then \
		echo "❌ 已取消"; \
		exit 1; \
	fi
	@echo ""
	@echo "🏷️  创建标签 v$(VERSION)..."
	@git tag -a "v$(VERSION)" -m "Release version $(VERSION)"
	@echo "📤 推送标签到 GitHub..."
	@git push origin "v$(VERSION)"
	@echo ""
	@echo "✅ 发布成功！"
	@echo ""
	@echo "🚀 GitHub Actions 将自动构建并创建 Release"
	@echo "📍 查看进度: https://github.com/liangzhaoliang95/monorepo-git-status/actions"

# 查看当前版本
version:
	@echo "当前版本信息:"
	@grep "^version" build.gradle.kts || echo "未找到版本信息"

# 列出所有标签
tags:
	@echo "已有标签:"
	@git tag -l | sort -V
