# SpigotMaster 集成文档

## 概述

ZNPCsPlus 集成了 SpigotMaster，支持向网易 MC 客户端发送 NPC 视觉同步事件，实现自定义皮肤和骨骼模型的显示。

## 配置要求

### 1. 依赖配置

已在 `plugin.yml` 中配置硬依赖：

```yaml
depend:
  - SpigotMaster
```

### 2. Gradle 依赖

已在 `plugin/build.gradle` 中添加：

```gradle
compileOnly files("../../WaiGameSpigotMaster/plugin/build/libs/SpigotMaster-1.3.10.jar")
```

## 核心类说明

### SpigotMasterIntegration

集成管理器，单例模式，负责初始化和管理 SpigotMaster 集成。

**初始化：**
```java
SpigotMasterIntegration.initialize();
```

**获取实例：**
```java
SpigotMasterIntegration integration = SpigotMasterIntegration.getInstance();
```

**检查是否启用：**
```java
if (integration.isEnabled()) {
    // SpigotMaster 可用
}
```

### NpcVisualSender

NPC 视觉事件发送器，提供两个核心方法：

#### 1. 发送 NPC 皮肤事件

```java
NpcVisualSender sender = integration.getVisualSender();
sender.sendNpcSkin(
    player,           // 观察者玩家
    "ShopNPC",        // NPC 名称（建议使用 GameProfile 名称）
    "npc/shop_keeper" // 皮肤路径（相对于客户端资源包 textures/models，不带 .png）
);
```

#### 2. 发送 NPC 模型事件（网易骨骼模型）

```java
sender.sendNpcModel(
    player,           // 观察者玩家
    "ShopNPC",        // NPC 名称
    "shop_keeper",    // 模型名称
    "npc/shop_keeper" // 材质路径
);
```

## 事件协议

### 固定常量

**必须在客户端和服务端保持一致：**

```java
private static final String MOD_NAMESPACE = "waigame_npc";
private static final String SERVER_SYSTEM = "WaiGameServerSystem";
private static final String CLIENT_SYSTEM = "WaiGameClientSystem";
private static final String EVENT_NPC_VISUAL = "NpcVisualSyncEvent";
```

### 数据格式

#### 皮肤事件
```json
{
  "npcName": "ShopNPC",
  "visualType": "skin",
  "skin": "npc/shop_keeper"
}
```

#### 模型事件
```json
{
  "npcName": "ShopNPC",
  "visualType": "model",
  "model": "shop_keeper",
  "texture": "npc/shop_keeper"
}
```

## 使用示例

### 在其他插件中使用

```java
import com.neteasemc.spigotmaster.SpigotMaster;
import lol.pyr.znpcsplus.integration.spigotmaster.SpigotMasterIntegration;
import org.bukkit.entity.Player;

// 获取集成实例
SpigotMasterIntegration integration = SpigotMasterIntegration.getInstance();

if (integration.isEnabled()) {
    // 发送皮肤事件
    integration.getVisualSender().sendNpcSkin(
        player,
        "MyNPC",
        "custom/my_skin"
    );
    
    // 或发送模型事件
    integration.getVisualSender().sendNpcModel(
        player,
        "MyNPC",
        "my_model",
        "custom/my_texture"
    );
}
```

### 在 NPC 命令中集成

可以在 `commands/` 目录下创建新命令，例如：

```java
public class NpcCustomSkinCommand implements SubCommandHandler {
    @Override
    public void handle(CommandContext context) {
        Player player = context.sender();
        NpcEntryImpl entry = context.parse(NpcEntryParser.class);
        String skinPath = context.parse(StringParser.class);
        
        if (SpigotMasterIntegration.getInstance().isEnabled()) {
            SpigotMasterIntegration.getInstance()
                .getVisualSender()
                .sendNpcSkin(player, entry.getId(), skinPath);
            
            context.send(Component.text("已发送自定义皮肤事件到客户端"));
        } else {
            context.send(Component.text("SpigotMaster 未启用", NamedTextColor.RED));
        }
    }
}
```

## 注意事项

1. **NPC 名称匹配**：建议使用 NPC 的 GameProfile 名称进行匹配，不要直接使用 Java 端的 entityId，因为它可能与客户端的 entityId 不一致。

2. **资源路径**：皮肤和材质路径都是相对于客户端资源包 `textures/models` 目录，且不需要包含 `.png` 扩展名。

3. **可用性检查**：在使用前务必检查 `SpigotMasterIntegration.getInstance().isEnabled()`，确保 SpigotMaster 插件已加载。

4. **异常处理**：如果在 SpigotMaster 未启用时调用 `getVisualSender()` 或 `getSpigotMaster()`，会抛出 `IllegalStateException`。

## 后续扩展

### 待实现功能

1. **NpcVisualSyncListener**：监听 NPC 显示事件，自动发送视觉同步
2. **自定义属性集成**：添加 NPC 自定义皮肤/模型属性
3. **命令集成**：添加 `/npc customskin` 和 `/npc custommodel` 命令
4. **配置文件支持**：允许在 NPC 配置中保存自定义视觉设置

### 集成到 NPC 属性系统

可以考虑添加新的实体属性：

- `CustomSkinProperty` - 存储自定义皮肤路径
- `CustomModelProperty` - 存储自定义模型信息

这些属性可以在 `EntityPropertyRegistryImpl.registerTypes()` 中注册，并在 NPC 显示时自动触发视觉同步。