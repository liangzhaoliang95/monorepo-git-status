# 发布到 JetBrains Marketplace 指南

## 📋 准备清单

- [x] 插件功能完整且测试通过
- [x] 完善的 plugin.xml（描述、变更日志）
- [x] README.md 文档
- [x] LICENSE 文件
- [x] 构建最终版本

## 🚀 发布步骤

### 1. 注册 JetBrains 账号

访问 https://account.jetbrains.com/login 注册账号

### 2. 创建插件页面

1. 访问 https://plugins.jetbrains.com/author/me
2. 点击 **"Add new plugin"**
3. 填写插件信息：
   - **Plugin Name**: Monorepo Git Status
   - **Plugin ID**: com.xiaozao.monorepo-git-status（必须与 plugin.xml 中的 id 一致）
   - **License**: MIT
   - **Category**: Version Control Systems
   - **Tags**: git, monorepo, vcs, productivity

### 3. 上传插件

1. 在插件页面点击 **"Upload plugin"**
2. 选择文件：`build/distributions/monorepo-git-status-1.0.8.zip`
3. 填写版本信息：
   - **Version**: 1.0.8
   - **Channel**: Stable
   - **Compatible with**: 2024.1 — 2026.3.*

### 4. 添加描述和截图

**必需的内容**：
- 插件简介（从 plugin.xml 的 description 复制）
- 至少 1 张截图（建议 3-5 张）
- 使用说明
- 变更日志

**截图建议**：
1. 项目视图中显示 Git 状态的效果
2. 设置页面
3. 实际使用场景（多个目录）

### 5. 提交审核

1. 检查所有信息
2. 点击 **"Publish"** 提交审核
3. 等待 JetBrains 团队审核（通常 1-3 个工作日）

## 📸 准备截图

### 推荐截图内容

**截图 1: 主要功能展示**
```
展示项目视图中多个目录的 Git 状态显示
包含不同颜色的状态：~蓝色、+红色、-灰色
```

**截图 2: 设置页面**
```
Settings → Tools → Monorepo Git Status
显示配置的目录列表
```

**截图 3: 实际使用场景**
```
一个真实的 monorepo 项目
services/ 下多个服务的状态
```

### 截图规范

- 尺寸：至少 1280x800px
- 格式：PNG 或 JPG
- 清晰度：高清，文字清晰可读
- 主题：推荐使用 Darcula（深色主题）

## 📝 插件描述模板

已在 plugin.xml 中完善，包括：
- 功能列表
- 使用说明
- 适用场景

## 🔄 后续更新

### 发布新版本

1. 修改 `build.gradle.kts` 中的版本号
2. 更新 `plugin.xml` 中的 `<change-notes>`
3. 重新构建：`./gradlew buildPlugin`
4. 在插件页面上传新版本
5. 填写更新日志

### 版本号规范

采用语义化版本：`MAJOR.MINOR.PATCH`
- **MAJOR**: 重大功能变更或不兼容改动
- **MINOR**: 新增功能，向后兼容
- **PATCH**: Bug 修复

## ⚠️ 注意事项

1. **插件 ID 不可更改**
   - plugin.xml 中的 `<id>` 一旦发布不能修改
   - 当前 ID：`com.xiaozao.monorepo-git-status`

2. **版本兼容性**
   - `sinceBuild`: 241（对应 2024.1）
   - `untilBuild`: 263.*（对应 2026.3）
   - 定期更新支持的版本范围

3. **审核要求**
   - 代码不能有恶意行为
   - 描述必须准确
   - 必须有清晰的使用说明
   - 建议添加源码仓库链接

4. **邮箱和 URL**
   - 确保 `support@xiaozao.com` 邮箱可用
   - 或修改为你的实际邮箱
   - `url` 改为你的 GitHub 仓库地址

## 📊 发布后

### 推广建议

1. **GitHub**
   - 创建 GitHub 仓库
   - 添加徽章（版本号、下载量）
   - 发布 Release

2. **社交媒体**
   - 发布到技术社区（掘金、V2EX、Reddit）
   - 写一篇介绍文章

3. **持续维护**
   - 及时回复用户反馈
   - 修复 Bug
   - 添加新功能

## 📚 相关链接

- **JetBrains Marketplace**: https://plugins.jetbrains.com/
- **插件开发文档**: https://plugins.jetbrains.com/docs/intellij/
- **发布指南**: https://plugins.jetbrains.com/docs/marketplace/
- **插件审核规则**: https://plugins.jetbrains.com/legal/approval-guidelines

## 💰 收费设置（可选）

JetBrains Marketplace 支持付费插件：
1. 设置为 **Freemium**（基础功能免费，高级功能付费）
2. 设置为 **Paid**（完全付费）
3. 保持 **Free**（完全免费）

当前推荐：**Free**，先积累用户

---

准备好以上内容后，就可以发布到 JetBrains Marketplace 了！
