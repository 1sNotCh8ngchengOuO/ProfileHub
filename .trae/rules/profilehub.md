---
alwaysApply: true
---
**项目规则注意：大部分规则内容写于项目开始阶段，具体实现思路和流程可能会随着项目进展发生变动，请根据实际情况调整。**

ProfileHub —— Minecraft 多整合包配置管理器（项目设计文档）
一、项目名称

ProfileHub

Slogan：

One Profile. Every Modpack.

定位：

一个用于 Minecraft 客户端的配置档（Profile）管理器，在不同整合包之间同步玩家个人配置，同时尽可能保持整合包作者原有的按键与玩法设计。

二、项目目标

玩家每下载一个新的整合包，都会重复：

修改语言
修改视频设置
修改 FOV
修改亮度
修改音量
修改 Sodium 设置
修改 Iris 设置
修改鼠标灵敏度
修改 UI 大小
...

这些设置实际上属于"玩家个人习惯"，而不是整合包的一部分。

ProfileHub 的目标就是：

让玩家只配置一次，以后所有整合包都可以快速恢复自己的设置。

三、核心理念
不同步整个 Minecraft 配置

绝不能：

直接复制 options.txt

因为：

Minecraft 版本不同
Mod 不同
配置字段不同

直接覆盖容易导致：

设置失效
配置损坏
Mod 崩溃

真正同步的是：

玩家的个人配置（Player Profile）

而不是：

当前实例所有配置。

四、Profile

Profile 是 ProfileHub 的核心。

例如：

FPS配置

高画质配置

笔记本配置

直播配置

默认配置

玩家可以拥有很多 Profile。

例如：

我的默认配置

↓

16 Render Distance

↓

Sodium

↓

Gamma=100%

↓

中文

或者：

直播

↓

GUI放大

↓

字幕开启

↓

音量降低
五、整体流程

第一次启动：

启动Minecraft

↓

ProfileHub加载

↓

检测ProfileHub目录

↓

不存在

↓

创建目录

↓

检测首次运行

↓

提示：

欢迎使用ProfileHub:

【导入已有Profile】
【创建默认Profile】
【跳过】
当点击导入、创建时打开本地网页


六、Profile保存格式

Profile 不保存：

options.txt

保存的是：

Profile

↓

metadata

+

settings

例如：

Default.profile

├── metadata.json

├── video.json

├── audio.json

├── language.json

├── accessibility.json

└── mods/

而不是一个巨大 JSON。

这样以后方便扩展。

七、metadata

metadata 保存：

Profile信息

例如：

名称

作者

创建时间

修改时间

描述
Minecraft信息

例如：

Minecraft版本

Loader

Java版本
环境信息

例如：

Fabric

Minecraft 1.21.5

Java21
Mod环境

例如：

Sodium

Iris

EntityCulling

ModernFix

Reese's Sodium Options

保存：

ModID

版本号
八、Environment Fingerprint（环境指纹）

Profile 最大特点。

保存：

Minecraft版本

Loader

Mod列表

Mod版本

例如：

Minecraft

↓

1.21.5

↓

Fabric

↓

Sodium

↓

Iris

↓

ModernFix

以后恢复：

先比较：

当前环境

↓

Profile环境

而不是直接恢复。

九、兼容性检测

恢复流程：

扫描当前环境

↓

读取Profile

↓

比较

↓

生成兼容报告

例如：

Minecraft版本：

不同

Loader：

相同

Sodium：

存在

Iris：

存在

ModernFix：

不存在

EntityCulling：

不存在

然后：

网页：

可以恢复：

√ Video

√ Audio

√ Language

√ Sodium

√ Iris

不能恢复：

× EntityCulling

原因：

Mod不存在
十、兼容等级

恢复之前：

生成兼容等级。

例如：

★★★★★

完全兼容

★★★★☆

Minecraft版本不同

但是字段全部存在

★★★☆☆

缺少一个Mod

★★☆☆☆

缺少多个Mod

★☆☆☆☆

仅恢复基础配置

玩家一眼就知道：

是否值得恢复。

十一、Compatibility Engine

整个项目最重要模块。

不要：

if(version=="1.20.1")

而是：

Compatibility Engine

负责：

这个字段还能恢复吗？

例如：

renderDistance

↓

当前版本存在

↓

恢复
enableVBO

↓

当前版本没有

↓

忽略

以后：

支持：

1.22

↓

不用重写恢复逻辑

只更新规则。

十二、同步范围（白名单模式）

采用白名单模式：只有明确列出的 key 才会被同步，其余一律忽略。

不需要维护 EXCLUDED / OTHER 列表。

同步的基础设置（options.txt）：

video：

fov, fovEffectScale, gamma, renderDistance, simulationDistance,
entityDistanceScaling, graphicsMode, ao, particles, maxFps,
enableVsync, fullscreen, guiScale, entityShadows, renderClouds,
forceUnicodeFont, bobView, biomeBlendRadius, mipmapLevels,
screenEffectScale, darknessEffectScale, darkMojangStudiosBackground,
hideLightningFlashes, prioritizeChunkUpdates, showAutosaveIndicator

audio：

soundDevice,
soundCategory_master, soundCategory_music, soundCategory_record,
soundCategory_weather, soundCategory_block, soundCategory_hostile,
soundCategory_neutral, soundCategory_player, soundCategory_ambient,
soundCategory_voice,
showSubtitles, directionalAudio

language：

lang

accessibility：

narrator, autoJump, autoSuggestions,
chatColors, chatLinks, chatLinksPrompt, chatVisibility,
chatOpacity, chatLineSpacing, chatHeightFocused, chatHeightUnfocused,
chatScale, chatWidth, chatDelay,
textBackgroundOpacity, backgroundForChatOnly,
hideServerAddress, reducedDebugInfo,
mouseSensitivity, mouseWheelSensitivity, discrete_mouse_scroll,
rawMouseInput, invertYMouse, touchscreen,
toggleCrouch, toggleSprint, highContrast,
glintSpeed, glintStrength, damageTiltStrength,
notificationDisplayTime, operatorItemsTab

不同步的（白名单之外，自动忽略）：

- 所有按键绑定（key_ 开头）
- 资源包（resourcePacks, incompatibleResourcePacks）
- 服务器信息（lastServer, servers）
- 聊天窗口尺寸（chatHeight/Width/Scale, chatDelay）
- 硬件相关（overrideWidth/Height, fullscreenResolution）
- 皮肤模型（modelPart_*）
- 其他非玩家习惯的设置（realmsNotifications, tutorialStep, telemetryOptInExtra 等）
- 所有 Mod 的 keybind

十三、Mod恢复策略

ProfileHub：

不会恢复：

整个config目录

而是：

建立：

Adapter

例如：

SodiumAdapter

IrisAdapter

ModernFixAdapter

接口：

读取

保存

恢复

恢复：

检测：

当前安装？

↓

安装

↓

恢复

↓

没安装

↓

跳过

取消设计：

等价 Mod 自动转换（如 Sodium→Embeddium、Iris→Oculus）

该功能不纳入项目。

十四、本地网页

采用：

localhost

Java：

HttpServer

游戏：

点击：

管理Profile

↓

打开浏览器

↓

localhost

网页：

ProfileHub

-----------------

当前实例：

ATM10

Minecraft：

1.21.5

Fabric

-----------------

Profile：

默认

FPS

直播

高画质

-----------------

兼容等级：

★★★★★

-----------------

恢复

保存

复制

删除
十五、保存流程
读取：

Minecraft设置

↓

Adapter导出

↓

转换

↓

保存Profile
十六、恢复流程
读取Profile

↓

Compatibility Engine

↓

判断字段

↓

恢复Minecraft

↓

恢复Mod

↓

保存

↓

重新加载设置
十七、第一次启动

第一次：

检测：

ProfileHub目录

↓

没有

↓

创建

↓

创建Default

↓

提示

以后：

不再提示。

十八、目录结构
.minecraft

└── ProfileHub

      ├── profiles

      │      ├── Default

      │      │      ├── metadata.json

      │      │      ├── video.json

      │      │      ├── audio.json

      │      │      ├── language.json

      │      │      └── mods

      │      │

      │      └── FPS

      │

      ├── cache

      ├── logs

      ├── settings.json

      └── compatibility
十九、项目架构

推荐采用模块化架构：

ProfileHub

├── common

│     ├── profile

│     ├── parser

│     ├── compatibility

│     ├── adapter

│     ├── web

│     ├── minecraft

│     ├── settings

│     └── util

│

├── fabric

│

├── forge（未来）

│

└── neoforge（未来）

其中：

common：约 90% 的业务逻辑，尽量不依赖加载器 API。
fabric / forge / neoforge：仅负责 Mod 入口、事件注册、Minecraft API 调用等平台相关实现。

这种结构与 Architectury 推荐的多平台项目组织方式一致，便于未来扩展到 Fabric、Forge、NeoForge 等多个加载器，而无需维护多套业务代码。

二十、开发路线
Phase 1（MVP）
Fabric
Minecraft 1.21.5
Java 21
本地 Profile
options.txt 基础配置同步
本地网页管理
Compatibility Engine
Environment Fingerprint
Phase 2
Sodium
Iris
ModernFix
更多优化 Mod Adapter
Phase 3
支持 Minecraft 1.20.x
完善跨版本兼容规则
Phase 4
基于 Architectury 拆分 common 与平台层
支持 NeoForge
后续支持 Forge（如有需求）