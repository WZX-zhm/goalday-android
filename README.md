# GoalDay 计划本 — Android Java 原生版

> **100% 离线 / Java 原生 / 单 APK 文件** —— 这是用 Java 重写的完整 Android Studio 工程，编译后即可生成可分发的 `.apk` 软件安装包。

## ✨ 功能特性

| 模块 | 功能 |
|------|------|
| 📅 **日程** | 周视图（左右分栏）· 时间轴 · 周导航 · 待办清单一键加入日程 · 完成勾选 |
| 📔 **日记** | 今日日期显示 · 7 种心情选择 · 文字日记 + 字数统计 · 自动汇总「今日完成」 |
| ✅ **打卡** | 自定义目标 · 20 个图标可选 · 连续天数统计 · 本周进度可视化 |

- **数据完全本地化**：SQLite 存储，无需任何网络权限
- **Material Design 风格** UI
- **内置 9 个示例目标**（读书 / 早起 / 运动 / 喝水 / 冥想…）

---

## 🚀 5 分钟编译出 APK

### 方法 1：用 Android Studio 编译（最简单）

1. **下载并安装** [Android Studio](https://developer.android.com/studio)（免费）
2. **打开工程**：启动 Android Studio → `File` → `Open` → 选择 `GoalDay-Android-Java` 目录
3. **等待首次同步**：Android Studio 会自动下载 Gradle 和 Android 依赖（需要 5-15 分钟）
4. **编译 APK**：菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. **找到 APK**：编译完成后右下角点击 `locate`，或在 `app/build/outputs/apk/debug/app-debug.apk`
6. **安装到手机**：把 APK 传到手机，点击安装（需开启"未知来源"）

### 方法 2：用命令行（适合开发者）

```bash
cd GoalDay-Android-Java
gradle assembleDebug
# APK 位置: app/build/outputs/apk/debug/app-debug.apk
```

> 第一次运行会下载 Gradle Wrapper（需要联网一次）

---

## 📱 编译要求

| 项目 | 要求 |
|------|------|
| Android Studio | Hedgehog (2023.1.1) 或更新 |
| JDK | 17（Android Studio 自带） |
| Gradle | 8.0+（首次打开自动下载） |
| compileSdk | 34（首次同步自动下载） |
| minSdk | 21（Android 5.0+） |

---

## 📁 工程结构

```
GoalDay-Android-Java/
├── app/
│   ├── build.gradle              # App 模块构建
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/goalday/app/
│       │   ├── MainActivity.java       # 启动 + 底部导航
│       │   ├── db/GoalDayDb.java       # SQLite 数据库
│       │   ├── model/                  # 数据模型
│       │   │   ├── DateUtil.java       # 日期工具
│       │   │   ├── ScheduleItem.java   # 日程
│       │   │   ├── TodoItem.java       # 待办
│       │   │   ├── Diary.java          # 日记
│       │   │   └── Habit.java          # 打卡
│       │   └── ui/                     # 三个 Fragment
│       │       ├── ScheduleFragment.java
│       │       ├── DiaryFragment.java
│       │       └── HabitFragment.java
│       └── res/                        # 布局、图标、颜色、主题
├── build.gradle                  # 顶层 Gradle
├── settings.gradle
└── gradle.properties
```

---

## 🎨 主题色自定义

打开 `app/src/main/res/values/colors.xml`，修改 `primary` 即可：

```xml
<color name="primary">#5C6BC0</color>   <!-- 改为你喜欢的颜色 -->
<color name="accent">#FF7043</color>
```

---

## 💾 数据存储位置

应用数据存在 Android 私有目录 `/data/data/com.goalday.app/databases/goalday.db`
- 卸载 App = 删除全部数据
- 重新安装 = 全新数据
- 如需迁移：可使用 `adb pull` 或后续加导出功能

---

## 🛠️ 二次开发建议

1. **加导出/导入**：`GoalDayDb` 已封装好，加 `exportToJson()` 即可
2. **加通知提醒**：在 `ScheduleItem` 加上 `remindMinutes`，用 `AlarmManager` 调度
3. **加云同步**（可选）：集成第三方 SDK（如 WorkBuddy 提供的）
4. **加小组件**：用 `AppWidgetProvider` 把今日待办放到桌面

---

## 📜 许可

本工程为示例教学项目，可自由使用、修改、商用。

