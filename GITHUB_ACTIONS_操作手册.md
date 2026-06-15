# 🚀 一键云端编译 APK — 操作手册

工程已经内置了 **GitHub Actions** 自动构建脚本。**完全免费**，无需本地安装 Java/Gradle。

---

## ⏱️ 全流程预计 10 分钟

| 步骤 | 时间 | 操作 |
|------|------|------|
| 1. 创建 GitHub 仓库 | 1 分钟 | 浏览器 |
| 2. Push 代码 | 2 分钟 | 命令行 |
| 3. 等待 Actions 编译 | 5-10 分钟 | 自动 |
| 4. 下载 APK | 1 分钟 | 浏览器 |
| 5. 装到手机 | 1 分钟 | USB/微信 |

---

## 步骤 1：创建 GitHub 仓库

1. 打开 https://github.com/new
2. 填写：
   - **Repository name**：`goalday-android`（或你喜欢的名字）
   - **Public/Private**：建议 **Public**（这样 Actions 是免费的，否则每月 2000 分钟额度，足够用）
   - **Add a README file**：**不要勾选**（我们已经有了）
3. 点击 **Create repository**

---

## 步骤 2：Push 代码

打开终端（Windows 用 PowerShell 或 Git Bash，Mac/Linux 用 Terminal），执行：

```bash
# 1. 进入解压后的工程目录
cd path/to/GoalDay-Android-Java

# 2. 初始化 git
git init
git add .
git commit -m "feat: GoalDay 完整 Android 工程"

# 3. 关联到 GitHub 仓库（替换成你的用户名和仓库名）
git remote add origin https://github.com/你的用户名/goalday-android.git

# 4. 推送到 main 分支
git branch -M main
git push -u origin main
```

**推送时会要求登录：**
- 推荐用 **Personal Access Token**（PAT）
- 生成方法：GitHub 头像 → Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token → 勾选 `repo` 全部 → 生成
- 把 token 当作密码粘贴到终端

---

## 步骤 3：等待自动编译

1. 进入 GitHub 仓库页面
2. 顶部点击 **Actions** 标签
3. 你会看到一个名为 **"Build GoalDay APK"** 的工作流正在运行
4. 点击进入 → 可以看到实时日志
5. ⏱️ 首次编译约 **5-10 分钟**（下载 Gradle 8.4 + Android SDK）

---

## 步骤 4：下载 APK

编译完成后：

1. 在工作流详情页底部找到 **Artifacts** 区域
2. 点击 **GoalDay-debug-apk** 下载（zip 压缩包）
3. 解压后得到 `app-debug.apk`

---

## 步骤 5：安装到手机

### 方式 A：USB 传输

```bash
# 连接手机 USB + 开启 USB 调试
adb install app-debug.apk
```

### 方式 B：微信/QQ 传输

1. 把 `app-debug.apk` 传到手机（微信文件传输助手 / QQ）
2. 手机上点击该文件
3. 系统会提示"未知来源应用"，点击"允许此次安装"
4. 安装完成

---

## 🆘 常见问题

### Q1: 编译失败 "SDK location not found"
**A**: 这是 Android 项目的常见问题。GitHub Actions 不会遇到（自动注入 SDK），但本地 Android Studio 打开时需要：
- 在项目根目录创建 `local.properties`，写入 `sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk`

### Q2: 编译失败 "Could not resolve com.android.tools.build:gradle"
**A**: 网络问题。GitHub Actions 的服务器在海外，下载正常。如果你在中国本地构建，配置 Maven 镜像。

### Q3: 安装 APK 时提示"未安装应用"
**A**: 检查：
- 手机 Android 版本 ≥ 5.0（minSdk 21）
- 是否已安装同包名旧版？先卸载
- 是否开启了"未知来源"权限

### Q4: 如何发布到应用商店？
**A**: 需要：
- 生成签名 keystore（`keytool -genkey -v -keystore release.keystore ...`）
- 用签名后的 APK/AAB 上传到 Google Play / 华为 / 小米 / OPPO / VIVO 等

---

## 🎁 Bonus：以后改了代码如何重新出 APK？

```bash
# 修改 Java 代码后
git add .
git commit -m "fix: 修复某个 bug"
git push

# → GitHub Actions 自动触发编译
# → 完成后去 Actions 页面下载新 APK
```

---

## 🔧 自定义 Actions 触发条件

打开 `.github/workflows/build-apk.yml`：

```yaml
on:
  push:
    branches: [ main ]       # 只在 main 分支 push 时编译
  workflow_dispatch:         # 允许手动触发（推荐开启）
  schedule:
    - cron: '0 2 * * *'     # 每天凌晨 2 点编译（一般用不到）
```

---

## 📞 进一步帮助

如遇到问题，请把：
1. Actions 日志截图
2. 错误信息原文

发给我，我帮你排查。
