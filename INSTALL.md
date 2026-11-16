# 快速安装指南

## ✅ 插件已构建成功

插件文件位置：
```
/Users/liang/xiaozaoWorkspace/idea_git_tool/build/distributions/monorepo-git-status-1.0.0.zip
```

文件大小：1.6 MB

## 安装步骤

### 1. 在 IDEA 中安装

1. 打开 **IntelliJ IDEA**
2. 进入 **Settings/Preferences** (macOS: `⌘,` / Windows: `Ctrl+Alt+S`)
3. 选择 **Plugins**
4. 点击右上角 **⚙️ 齿轮图标**
5. 选择 **Install Plugin from Disk...**
6. 选择文件：`/Users/liang/xiaozaoWorkspace/idea_git_tool/build/distributions/monorepo-git-status-1.0.0.zip`
7. 点击 **OK**
8. 重启 IDEA

### 2. 配置监控目录

重启后：

1. 进入 **Settings** → **Tools** → **Monorepo Git Status**
2. 点击 **+** 添加目录
3. 填写：
   - **Display Name**: 显示名称（如 `app`、`server`）
   - **Directory Path**: 相对路径（如 `packages/app`、`modules/server`）
4. 可选：调整自动刷新间隔（默认 30 秒）
5. 点击 **OK**

### 3. 查看状态

配置完成后，IDEA 底部状态栏会显示：

```
app: +3 ~2 -1 | server: +5 ~1 | common: clean
```

- **+** = 新增文件
- **~** = 修改文件
- **-** = 删除文件
- **?** = 未追踪文件

### 4. 使用技巧

- **手动刷新**：点击状态栏小部件
- **查看详情**：鼠标悬停在状态栏上
- **修改配置**：随时在设置中增删目录

## 示例配置

假设你的 monorepo 结构：
```
my-project/
├── packages/
│   ├── frontend/
│   ├── backend/
│   └── shared/
└── tools/
```

配置：
| Display Name | Directory Path     |
|--------------|-------------------|
| frontend     | packages/frontend |
| backend      | packages/backend  |
| shared       | packages/shared   |
| tools        | tools             |

## 重新构建（如需修改）

如果你修改了源码，重新构建：

```bash
cd /Users/liang/xiaozaoWorkspace/idea_git_tool
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
./gradlew buildPlugin
```

## 故障排查

### 状态栏不显示

- 确保在 Git 仓库中打开的项目
- 检查 **View** → **Appearance** → **Status Bar** 已启用

### 显示 "No dirs"

- 进入设置添加监控目录

### 显示 "N/A"

- 确保项目根目录有 `.git` 文件夹
- 确保系统已安装 Git：`git --version`

## 功能特性

✅ 实时监控多个子目录的 Git 变更  
✅ 显示新增/修改/删除/未追踪文件数  
✅ 可配置自动刷新间隔  
✅ 点击手动刷新  
✅ 悬停查看详细信息  
✅ 支持 IDEA 2024.1 - 2025.1  

Enjoy! 🚀
