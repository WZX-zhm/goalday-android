#!/bin/bash
# =============================================
# GoalDay 一键推送 GitHub 并云端编译 APK
# 用法：
#   1. 修改下面的 GITHUB_USER 和 REPO_NAME
#   2. 在项目根目录运行：bash push-to-github.sh
# =============================================

# ====== 配置区（改成你的）======
GITHUB_USER="your-username"     # 改成你的 GitHub 用户名
REPO_NAME="goalday-android"     # 改成你的仓库名（仓库需先在 GitHub 网页创建）
COMMIT_MSG="feat: GoalDay Android 工程"
# =================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}==> GoalDay 一键推送工具${NC}"

# 检查 git
if ! command -v git &> /dev/null; then
    echo -e "${RED}错误：未安装 git，请先安装 https://git-scm.com${NC}"
    exit 1
fi

# 检查是否已经是 git 仓库
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}==> 初始化 git 仓库${NC}"
    git init
    git checkout -b main 2>/dev/null || git branch -M main
fi

# 配置 git（如果没配置过）
if [ -z "$(git config user.name)" ]; then
    echo -e "${YELLOW}==> 请输入你的 GitHub 用户名（用于 git commit）${NC}"
    read -p "Name: " GIT_NAME
    echo -e "${YELLOW}==> 请输入你的 GitHub 邮箱${NC}"
    read -p "Email: " GIT_EMAIL
    git config user.name "$GIT_NAME"
    git config user.email "$GIT_EMAIL"
fi

# 添加并提交
echo -e "${YELLOW}==> 添加并提交代码${NC}"
git add .
git commit -m "$COMMIT_MSG" 2>/dev/null || echo "  (没有新改动，跳过 commit)"

# 设置远程仓库
REMOTE_URL="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"
if git remote | grep -q origin; then
    git remote set-url origin "$REMOTE_URL"
else
    git remote add origin "$REMOTE_URL"
fi
echo -e "${YELLOW}==> 远程仓库：$REMOTE_URL${NC}"

# 推送
echo -e "${YELLOW}==> 推送到 GitHub${NC}"
git push -u origin main

echo ""
echo -e "${GREEN}✓ 推送成功！${NC}"
echo ""
echo -e "${YELLOW}下一步：${NC}"
echo -e "  1. 打开 https://github.com/${GITHUB_USER}/${REPO_NAME}/actions"
echo -e "  2. 等待编译完成（约 5-10 分钟）"
echo -e "  3. 在 Artifacts 下载 'GoalDay-debug-apk'"
echo -e "  4. 解压得到 app-debug.apk，传到手机安装"
echo ""
echo -e "${YELLOW}提示：${NC}首次推送会要求输入 GitHub 用户名和 Personal Access Token（不是密码）"
echo -e "  Token 生成：https://github.com/settings/tokens"
