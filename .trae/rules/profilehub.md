---
alwaysApply: true
---

# ProfileHub 项目规则

> **注意**：本规则写于项目初期，具体实现思路可能随项目进展调整。如遇矛盾，以实际代码为准。

---

## 🤖 AI 快速参考（必读）

### 项目是什么
一个 Minecraft 客户端 Mod（Fabric 1.21.5），名为 **ProfileHub**。它让玩家把个人配置（视频、音频、语言、无障碍等）保存为 Profile 文件，在新整合包中一键恢复，省去重复配置的麻烦。

### 核心原则（写代码时牢记）

1. **白名单同步** — 只有第十二节列出的 key 才能同步，其余一律忽略。不要同步 options.txt 的全部内容。
2. **兼容性优先** — 恢复前必须做兼容检测（Compatibility Engine），不要直接覆盖。字段不存在就跳过，不要报错崩溃。
3. **不碰按键绑定** — 所有 `key_` 开头的键、所有 Mod 的按键绑定，都不同步。
4. **不复制整个 config 目录** — Mod 配置通过 Adapter 接口逐个恢复，不直接复制文件。
5. **游戏内 GUI** — 使用 Minecraft 原生 Screen 系统，不做外部网页/浏览器。
6. **不实现等价 Mod 转换** — Sodium↔Embeddium、Iris↔Oculus 等转换功能不纳入项目。
7. **业务逻辑放 common** — 约 90% 代码应放在 common 目录，不依赖具体加载器 API。fabric 目录仅放入口和平台相关代码。
8. **Profile 保存格式** — 每个 Profile 是一个目录，包含 `metadata.json`、`video.json`、`audio.json`、`language.json`、`accessibility.json` 和 `mods/` 子目录，不是一个大 JSON。

### 当前开发阶段：Phase 1（MVP）
- Fabric 1.21.5 + Java 21
- 本地 Profile 管理
- options.txt 基础配置同步
- 游戏内 GUI
- Compatibility Engine + Environment Fingerprint

### 目录结构速查

```
.minecraft/ProfileHub/
├── profiles/           ← Profile 存放目录
│   └── <ProfileName>/
│       ├── metadata.json
│       ├── video.json
│       ├── audio.json
│       ├── language.json
│       ├── accessibility.json
│       └── mods/
├── cache/
├── logs/
├── settings.json       ← ProfileHub 自身设置
└── compatibility/      ← 兼容规则
```

### 代码模块结构

```
src/main/java/org/profilehub/
├── common/             ← 90% 业务逻辑，不依赖加载器
│   ├── profile/        ← Profile 数据结构
│   ├── parser/         ← 配置解析
│   ├── compatibility/  ← 兼容性引擎
│   ├── adapter/        ← Mod 适配器
│   ├── gui/            ← 游戏内界面
│   ├── minecraft/      ← Minecraft 相关工具
│   ├── settings/       ← 设置管理
│   └── util/           ← 通用工具
└── fabric/             ← 仅 Fabric 入口和平台代码
```

---

## 📋 项目概述

### 定位
**ProfileHub** — Minecraft 多整合包配置管理器

- **Slogan**：One Profile. Every Modpack.
- **目标**：让玩家只配置一次个人偏好，在所有整合包中快速恢复自己的设置。

### 解决的问题
玩家每下载一个新整合包，都要重复设置：语言、视频选项、FOV、亮度、音量、Sodium/Iris 设置、鼠标灵敏度、UI 大小等。这些属于"玩家个人习惯"，不是整合包的一部分。

---

## 🏗️ 核心理念

### 不同步整个 Minecraft 配置
**绝不能**直接复制 `options.txt`。因为不同整合包的 Minecraft 版本、Mod 列表、配置字段都不同，直接覆盖会导致设置失效、配置损坏甚至崩溃。

**真正同步的是**：玩家的个人配置（Player Profile），而不是当前实例的所有配置。

### Profile 概念
Profile 是核心。玩家可以创建多个 Profile 应对不同场景，例如：

| Profile 名称 | 用途 |
|-------------|------|
| 默认配置 | 日常使用，16 Render Distance，中文，Gamma 100% |
| FPS 配置 | 低画质，追求帧率 |
| 高画质配置 | 截图/录视频用 |
| 笔记本配置 | 省电模式 |
| 直播配置 | GUI 放大，字幕开启，音量降低 |

---

## 🔄 工作流程

### 首次启动流程
1. Minecraft 启动 → ProfileHub 加载
2. 检测 `.minecraft/ProfileHub/` 目录
3. 不存在 → 创建目录 → 创建默认 Profile
4. 弹窗提示：「欢迎使用 ProfileHub」→ 选择【导入已有 Profile】【创建默认 Profile】【跳过】
5. 所有操作在游戏内 GUI 完成，无需浏览器

### 保存流程
```
Minecraft 设置 → Adapter 导出 → 格式转换 → 保存到 Profile 目录
```

### 恢复流程
```
读取 Profile → Compatibility Engine 兼容检测 → 判断字段 → 恢复 Minecraft 设置 → 恢复 Mod 设置 → 保存 → 重新加载
```

---

## 📁 Profile 保存格式

每个 Profile 是一个**目录**（不是一个大 JSON），方便以后扩展：

```
Default.profile/
├── metadata.json          ← Profile 元信息 + 环境指纹
├── video.json             ← 视频设置
├── audio.json             ← 音频设置
├── language.json          ← 语言设置
├── accessibility.json     ← 无障碍设置
└── mods/                  ← 各 Mod 的配置
```

### metadata.json 包含内容

| 类别 | 字段 | 说明 |
|------|------|------|
| **Profile 信息** | name, author, created, modified, description | 名称、作者、时间、描述 |
| **Minecraft 信息** | mcVersion, loader, javaVersion | 例如 1.21.5, Fabric, Java 21 |
| **环境指纹** | mods (ModID + 版本号列表) | 例如 Sodium 0.6.10, Iris 1.8.8 |

---

## 🔍 Environment Fingerprint（环境指纹）

Profile 会保存创建时的环境信息（Minecraft 版本、Loader、Mod 列表及版本）。恢复时先对比当前环境与 Profile 环境，而不是直接恢复。

---

## ✅ 兼容性检测

### 检测流程
1. 扫描当前 Minecraft 环境
2. 读取 Profile 的环境指纹
3. 逐项比较，生成兼容报告

### 兼容等级

| 等级 | 含义 |
|------|------|
| ★★★★★ | 完全兼容 |
| ★★★★☆ | Minecraft 版本不同，但字段全部存在 |
| ★★★☆☆ | 缺少一个 Mod |
| ★★☆☆☆ | 缺少多个 Mod |
| ★☆☆☆☆ | 仅能恢复基础配置 |

玩家一眼就能判断是否值得恢复。

---

## ⚙️ Compatibility Engine（兼容性引擎）

**这是项目最重要的模块。**

不要写 `if(version == "1.20.1")` 这种硬编码逻辑。而是通过规则引擎判断：这个字段在当前版本/环境下还能恢复吗？

- 字段存在 → 恢复
- 字段不存在 → 忽略（不报错、不崩溃）

以后支持新 Minecraft 版本时，只需更新规则，不用重写恢复逻辑。

---

## 📋 同步范围（白名单模式）

**只有明确列出的 key 才会被同步，其余一律忽略。**

### 同步的 options.txt 字段

**video（视频）：**
`fov`, `fovEffectScale`, `gamma`, `renderDistance`, `simulationDistance`, `entityDistanceScaling`, `graphicsMode`, `ao`, `particles`, `maxFps`, `enableVsync`, `fullscreen`, `guiScale`, `entityShadows`, `renderClouds`, `forceUnicodeFont`, `bobView`, `biomeBlendRadius`, `mipmapLevels`, `screenEffectScale`, `darknessEffectScale`, `darkMojangStudiosBackground`, `hideLightningFlashes`, `prioritizeChunkUpdates`, `showAutosaveIndicator`

**audio（音频）：**
`soundDevice`, `soundCategory_master`, `soundCategory_music`, `soundCategory_record`, `soundCategory_weather`, `soundCategory_block`, `soundCategory_hostile`, `soundCategory_neutral`, `soundCategory_player`, `soundCategory_ambient`, `soundCategory_voice`, `showSubtitles`, `directionalAudio`

**language（语言）：**
`lang`

**accessibility（无障碍/杂项）：**
`narrator`, `autoJump`, `autoSuggestions`, `chatColors`, `chatLinks`, `chatLinksPrompt`, `chatVisibility`, `chatOpacity`, `chatLineSpacing`, `chatHeightFocused`, `chatHeightUnfocused`, `chatScale`, `chatWidth`, `chatDelay`, `textBackgroundOpacity`, `backgroundForChatOnly`, `hideServerAddress`, `reducedDebugInfo`, `mouseSensitivity`, `mouseWheelSensitivity`, `discrete_mouse_scroll`, `rawMouseInput`, `invertYMouse`, `touchscreen`, `toggleCrouch`, `toggleSprint`, `highContrast`, `glintSpeed`, `glintStrength`, `damageTiltStrength`, `notificationDisplayTime`, `operatorItemsTab`

### 不同步的（白名单之外，自动忽略）
- 所有按键绑定（`key_` 开头）
- 资源包（`resourcePacks`, `incompatibleResourcePacks`）
- 服务器信息（`lastServer`, `servers`）
- 硬件相关（`overrideWidth`, `overrideHeight`, `fullscreenResolution`）
- 皮肤模型（`modelPart_*`）
- 其他非玩家习惯的设置（`realmsNotifications`, `tutorialStep`, `telemetryOptInExtra` 等）
- 所有 Mod 的按键绑定

---

## 🔌 Mod 恢复策略

### 不恢复整个 config 目录
通过 **Adapter 接口** 逐个 Mod 处理。

### Adapter 接口
每个需要支持的 Mod 实现一个 Adapter，例如：`SodiumAdapter`、`IrisAdapter`、`ModernFixAdapter`。

接口提供三个方法：**读取**（从当前实例导出）、**保存**（写入 Profile）、**恢复**（从 Profile 写入当前实例）。

### 恢复检测
```
当前 Mod 已安装？ → 是 → 恢复配置
                 → 否 → 跳过（不报错）
```

### 不实现的功能
等价 Mod 自动转换（如 Sodium→Embeddium、Iris→Oculus）不纳入本项目。

---

## 🖥️ 游戏内 GUI

使用 Minecraft 原生 Screen 系统构建，无需外部浏览器。

| 界面 | 功能 |
|------|------|
| **ProfileListScreen**（主界面） | 展示所有 Profile 列表，每个旁边显示兼容等级（★），底部放功能按钮 |
| **ProfileDetailScreen**（详情） | 点击 Profile 进入，展示兼容详细报告（可恢复/不可恢复及原因），提供恢复、删除、复制等操作 |

---

## 🗺️ 开发路线

| 阶段 | 内容 |
|------|------|
| **Phase 1（MVP）** ← 当前 | Fabric 1.21.5 + Java 21，本地 Profile，options.txt 基础同步，游戏内 GUI，Compatibility Engine，Environment Fingerprint |
| **Phase 2** | Sodium、Iris、ModernFix 等优化 Mod 的 Adapter |
| **Phase 3** | 支持 Minecraft 1.20.x，完善跨版本兼容规则 |
| **Phase 4** | 基于 Architectury 拆分 common 与平台层，支持 NeoForge，后续支持 Forge |