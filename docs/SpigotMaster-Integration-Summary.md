# SpigotMaster 集成使用指南

## 已完成的工作

### 1. 核心类实现

#### SpigotMasterIntegration
- 单例管理器，负责初始化和管理 SpigotMaster 集成
- 自动检测 SpigotMaster 插件是否存在
- 位置：`plugin/src/main/java/lol/pyr/znpcsplus/integration/spigotmaster/SpigotMasterIntegration.java`

#### NpcVisualSender
- NPC 视觉事件发送器
- 提供 `sendNpcSkin()` 和 `sendNpcModel()` 方法
- 位置：`plugin/src/main/java/lol/pyr/znpcsplus/integration/spigotmaster/NpcVisualSender.java`

#### NpcVisualSyncListener
- 监听器框架（待集成）
- 位置：`plugin/src/main/java/lol/pyr/znpcsplus/integration/spigotmaster/NpcVisualSyncListener.java`

#### NpcCustomSkinCommand
- 示例命令实现
- 位置：`plugin/src/main/java/lol/pyr/znpcsplus/commands/NpcCustomSkinCommand.java`

### 2. 构建配置

- 已在 `plugin.yml` 添加 `softdepend: SpigotMaster`
- 已在 `plugin/build.gradle` 添加 SpigotMaster 依赖
- 已将项目 Java 版本从 8 升级到 17（匹配 SpigotMaster）
- 构建成功，生成 `ZNPCsPlus-2.1.0.jar` (6.7M)

### 3. 集成初始化

在 `ZNpcsPlus.onEnable()` 中添加了初始化调用：
```java
SpigotMasterIntegration.initialize();
```

## 使用方法

### 基础使用

```java
// 检查集成是否可用
if (SpigotMasterIntegration.getInstance().isEnabled()) {
    // 获取发送器
    NpcVisualSender sender = SpigotMasterIntegration.getInstance().getVisualSender();
    
    // 发送皮肤事件
    sender.sendNpcSkin(player, "ShopNPC", "npc/shop_keeper");
    
    // 或发送模型事件
    sender.sendNpcModel(player, "ShopNPC", "shop_keeper", "npc/shop_keeper");
}
```

### 在其他插件中使用

```java
import lol.pyr.znpcsplus.integration.spigotmaster.SpigotMasterIntegration;

// 直接调用
if (SpigotMasterIntegration.getInstance().isEnabled()) {
    SpigotMasterIntegration.getInstance()
        .getVisualSender()
        .sendNpcSkin(player, npcName, skinPath);
}
```

## 事件协议

### 固定常量（客户端和服务端必须一致）

```java
MOD_NAMESPACE = "waigame_npc"
SERVER_SYSTEM = "WaiGameServerSystem"
CLIENT_SYSTEM = "WaiGameClientSystem"
EVENT_NPC_VISUAL = "NpcVisualSyncEvent"
```

### 数据格式

**皮肤事件：**
```json
{
  "npcName": "ShopNPC",
  "visualType": "skin",
  "skin": "npc/shop_keeper"
}
```

**模型事件：**
```json
{
  "npcName": "ShopNPC",
  "visualType": "model",
  "model": "shop_keeper",
  "texture": "npc/shop_keeper"
}
```

## 后续扩展建议

### 1. 添加命令支持

将 `NpcCustomSkinCommand` 集成到命令系统：

```java
// 在 ZNpcsPlus.registerCommands() 中添加
multiCommand.addSubcommand("customskin", new NpcCustomSkinCommand());
```

并在 `plugin/src/main/resources/messages/` 添加对应的帮助文本。

### 2. 添加 NPC 属性

创建新的实体属性来持久化自定义皮肤/模型：

```java
public class CustomSkinProperty extends StringProperty {
    public CustomSkinProperty() {
        super("custom_skin", null, new String[0]);
    }
}
```

在 `EntityPropertyRegistryImpl.registerTypes()` 中注册。

### 3. 自动同步

在 `NpcImpl.UNSAFE_show()` 中集成自动发送逻辑：

```java
@Override
protected CompletableFuture<Void> UNSAFE_show(Player player) {
    CompletableFuture<Void> future = CompletableFuture.allOf(
        entity.spawn(player), 
        hologram.show(player)
    );
    
    // 自动发送视觉同步
    if (SpigotMasterIntegration.getInstance().isEnabled()) {
        // 检查 NPC 是否有自定义皮肤属性
        // 发送相应的视觉事件
    }
    
    return future;
}
```

## 注意事项

1. **软依赖配置**：已配置为 `softdepend`，ZNPCsPlus 可以在没有 SpigotMaster 的情况下运行
2. **Java 版本**：项目已升级到 Java 17，确保服务器使用 Java 17+ 运行
3. **NPC 名称匹配**：建议使用 NPC 的 ID 或 GameProfile 名称进行匹配
4. **资源路径**：皮肤和材质路径相对于客户端资源包 `textures/models` 目录

## 测试步骤

1. 将 `SpigotMaster-1.3.10.jar` 放入服务器 plugins 目录
2. 将 `ZNPCsPlus-2.1.0.jar` 放入服务器 plugins 目录
3. 启动服务器，检查日志中的初始化信息
4. 使用自定义命令或 API 发送视觉事件
5. 在客户端验证 NPC 皮肤/模型是否变更

## 文件清单

```
plugin/src/main/java/lol/pyr/znpcsplus/
├── integration/spigotmaster/
│   ├── SpigotMasterIntegration.java
│   ├── NpcVisualSender.java
│   └── NpcVisualSyncListener.java
├── commands/
│   └── NpcCustomSkinCommand.java
└── ZNpcsPlus.java (已修改)

docs/
└── SpigotMaster-Integration.md (详细文档)

build.gradle (已修改 - Java 17)
plugin/build.gradle (已添加依赖)
plugin/src/main/resources/plugin.yml (已添加 softdepend)
```

## 构建状态

✅ 编译成功  
✅ 无编译错误  
✅ 生成 jar: `plugin/build/libs/ZNPCsPlus-2.1.0.jar` (6.7M)