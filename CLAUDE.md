# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建命令

```bash
# 构建完整插件 (生成 shadowed jar)
./gradlew shadowJar

# 构建所有子项目
./gradlew build

# 启动本地测试服务器 (Paper 1.21.11, 需要 Java 21)
./gradlew :plugin:runServer

# 发布 API 到 Maven 仓库
./gradlew :api:publish
```

输出 jar 位于 `plugin/build/libs/ZNPCsPlus-<version>.jar`。

## 项目结构

多模块 Gradle 项目，包含两个子模块：

- **`api/`** — 公开 API，发布至 `repo.pyr.lol`，供外部插件集成。包含接口定义：`NpcApi`、`NpcRegistry`、`EntityProperty`、`SkinDescriptor` 等，以及 `lol.pyr.znpcsplus.util` 下的全部枚举类型。
- **`plugin/`** — 插件实现，依赖 `api` 模块，最终通过 Shadow 打包为单一 fat jar。

### 核心架构

**入口与生命周期**
- `ZNpcsPlusBootstrap` — Bukkit `JavaPlugin` 入口，处理旧版数据迁移后委托给 `ZNpcsPlus`。
- `ZNpcsPlus` — 核心组件装配类，在 `onEnable` 中按顺序初始化所有子系统，在 `onDisable` 中反向执行注册的 shutdown tasks。

**数据包层 (`packets/`)**
- `PacketFactory` 接口定义全部 NPC 相关数据包操作（spawn、destroy、metadata、equipment 等）。
- 按协议版本提供多个实现：`V1_8`、`V1_17`、`V1_19_3`、`V1_20_2`、`V1_21_3`。启动时根据服务端版本自动选择最合适的实现（向下兼容匹配）。
- 底层使用 **PacketEvents 2.x** 库，所有操作均为 100% packet-based，不占用主线程。

**实体属性系统 (`entity/`)**
- `EntityPropertyRegistry` 管理所有可配置的 NPC 属性（皮肤、发光、装备、姿势等）。
- 每个属性实现 `EntityProperty<T>`，通过 `PropertyHolder` 挂载到 NPC。
- `properties/` 下包含大量具体属性，编码属性（`EncodedByteProperty`、`EncodedIntegerProperty` 等）负责将值写入 MC 实体元数据位字段。

**NPC 管理 (`npc/`)**
- `NpcRegistryImpl` — 全局 NPC 注册表，负责加载/保存/创建/删除。
- `NpcImpl` — 单个 NPC 实例，持有 `PacketEntity`、`Hologram`、交互动作列表。
- `NpcTypeRegistryImpl` — 管理支持的实体类型（PLAYER、ZOMBIE 等）。

**存储层 (`storage/`)**
- 支持三种后端：`yaml`（默认）、`sqlite`、`mysql`。
- 通过 `ConfigManager` 的 `storageType` 配置切换，支持 `npc storage migrate` 命令在线迁移。

**调度器抽象 (`scheduling/`)**
- `TaskScheduler` 接口屏蔽 Spigot 与 Folia 差异，运行时由 `FoliaUtil.isFolia()` 决定实例化哪个实现。

**皮肤系统 (`skin/`)**
- `MojangSkinCache` 缓存从 Mojang API 获取的皮肤数据，存储于 `plugins/ZNPCsPlus/skins/`。
- 支持四种 descriptor：按名称获取、按 UUID 获取、直接预取值、镜像另一个 NPC。

### 添加新实体属性

1. 在 `plugin/src/main/java/.../entity/properties/` 下新建属性类，继承合适的基类（如 `BooleanProperty`、`EncodedByteProperty`）。
2. 在 `EntityPropertyRegistryImpl.registerTypes()` 中注册该属性。
3. 若需版本差异处理，在对应 `PacketFactory` 实现中添加逻辑。

### 添加新命令

1. 在 `commands/` 对应子目录下新建命令类，实现 Director 框架的命令接口。
2. 在 `ZNpcsPlus.registerCommands()` 中通过 `addSubcommand()` 注册。
3. 在 `plugin/src/main/resources/messages/` 下添加对应的帮助文本文件（`.txt`）。

## 关键依赖

| 依赖 | 用途 |
|------|------|
| PacketEvents 2.x | 数据包拦截与发送 |
| Director | 命令框架 |
| Adventure | 文本组件与 MiniMessage |
| DazzleConf | YAML 配置 |
| Gson | JSON 序列化 |
| bStats | 插件统计 |

## 编译注意事项

- 根 `build.gradle` 设置 Java 8 toolchain（兼容性目标），但 `runServer` 任务使用 Java 21 运行测试服务器。
- Shadow 打包时所有第三方库均被 relocate 至 `lol.pyr.znpcsplus.libraries.*` 命名空间以避免冲突。
- 版本号：`2.1.0`；CI 环境（存在 `BUILD_ID` 环境变量）时附加 `-SNAPSHOT` 后缀。
