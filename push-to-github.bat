# =============================================
# GoalDay 一键推送 GitHub 并云端编译 APK (Windows)
# 用法：双击运行
# =============================================

@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ============================================
echo    GoalDay 一键推送 GitHub 工具
echo ============================================
echo.

REM ====== 配置区（改成你的）======
set GITHUB_USER=your-username
set REPO_NAME=goalday-android
set COMMIT_MSG=feat: GoalDay Android 工程
REM =================================

REM 检查 git
where git >nul 2>nul
if errorlevel 1 (
    echo [错误] 未安装 git，请先安装 https://git-scm.com
    pause
    exit /b 1
)

REM 初始化 git
if not exist ".git" (
    echo ==^> 初始化 git 仓库
    git init
    git checkout -b main 2>nul
)

REM 检查 git 配置
for /f "tokens=*" %%i in ('git config user.name') do set GIT_NAME=%%i
for /f "tokens=*" %%i in ('git config user.email') do set GIT_EMAIL=%%i
if "%GIT_NAME%"=="" (
    echo ==^> 请输入你的 GitHub 用户名
    set /p GIT_NAME="Name: "
    git config user.name "!GIT_NAME!"
)
if "%GIT_EMAIL%"=="" (
    echo ==^> 请输入你的 GitHub 邮箱
    set /p GIT_EMAIL="Email: "
    git config user.email "!GIT_EMAIL!"
)

REM 添加并提交
echo ==^> 添加并提交代码
git add .
git commit -m "%COMMIT_MSG%" 2>nul

REM 设置远程仓库
set REMOTE_URL=https://github.com/%GITHUB_USER%/%REPO_NAME%.git
git remote remove origin 2>nul
git remote add origin %REMOTE_URL%
echo ==^> 远程仓库：%REMOTE_URL%

REM 推送
echo ==^> 推送到 GitHub
git push -u origin main
if errorlevel 1 (
    echo.
    echo [错误] 推送失败！请检查：
    echo   1. GitHub 仓库是否已创建
    echo   2. 用户名是否正确
    echo   3. Personal Access Token 是否正确（首次推送需要）
    echo   Token 生成：https://github.com/settings/tokens
    pause
    exit /b 1
)

echo.
echo ============================================
echo  推送成功！
echo ============================================
echo.
echo 下一步：
echo   1. 打开 https://github.com/%GITHUB_USER%/%REPO_NAME%/actions
echo   2. 等待编译完成（约 5-10 分钟）
echo   3. 在 Artifacts 下载 GoalDay-debug-apk
echo   4. 解压得到 app-debug.apk，传到手机安装
echo.
echo 提示：首次推送会要求输入 GitHub 用户名和 Personal Access Token（不是密码）
echo.
pause
