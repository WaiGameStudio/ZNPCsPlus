# Geyser 全息文本修复说明

## 问题描述

在 Geyser 环境中（Java 版服务器 + 基岩版客户端），ZNPCsPlus 的全息文本会出现"掉落"或"贴在 NPC 脚下"的现象。

### 根本原因

ZNPCsPlus 使用真实的 `ARMOR_STAND`（盔甲架）实体来显示全息文本，但缺少基岩版客户端所需的关键标记：

1. **NO_GRAVITY (无重力)** - 实体元数据索引 0，位掩码 `0x02`
2. **MARKER (标记模式)** - 盔甲架元数据索引（根据版本），位掩码 `0x10`

当 Geyser 将 Java 协议包转发给基岩版客户端时，基岩版会按照自己的物理规则处理这个盔甲架，导致其受重力影响而"掉落"。

## 解决方案

### 1. 注册新属性

在 `EntityPropertyRegistryImpl.java` 中注册两个新的实体属性：

- `no_gravity` - 实体元数据索引 0，位掩码 `0x02`，防止实体受重力影响
- `marker` - 盔甲架特有属性，位掩码 `0x10`，启用 Marker 模式（无碰撞、无 AI）

```java
// 在通用实体标记中添加 no_gravity
register(new BitsetProperty("no_gravity", 0, 0x02));
linkProperties("glow", "fire", "invisible", "no_gravity");

// 在盔甲架属性中添加 marker
register(new BitsetProperty("marker", armorStandIndex++, 0x10));
linkProperties("small", "arms", "base_plate", "marker");
```

### 2. 更新全息文本实现

修改 `HologramText.java`，使全息文本盔甲架自动启用这些属性：

```java
public HologramText(...) {
    super(viewable, text, packetFactory, EntityTypes.ARMOR_STAND, location);
    addProperty(propertyRegistry.getByName("name"));
    addProperty(propertyRegistry.getByName("invisible"));
    addProperty(propertyRegistry.getByName("no_gravity"));     // 新增
    addProperty(propertyRegistry.getByName("marker"));         // 新增
}

public <T> T getProperty(EntityProperty<T> key) {
    if (key.getName().equalsIgnoreCase("invisible")) return (T) Boolean.TRUE;
    if (key.getName().equalsIgnoreCase("no_gravity")) return (T) Boolean.TRUE;  // 新增
    if (key.getName().equalsIgnoreCase("marker")) return (T) Boolean.TRUE;      // 新增
    if (key.getName().equalsIgnoreCase("name")) return (T) getValue();
    return super.getProperty(key);
}
```

### 3. 更新座位盔甲架

修改 `ArmorStandVehicleProperties.java`，为 NPC 座位功能使用的盔甲架也添加这些属性：

```java
public ArmorStandVehicleProperties(EntityPropertyRegistryImpl propertyRegistry) {
    _setProperty(propertyRegistry.getByName("small", Boolean.class), true);
    _setProperty(propertyRegistry.getByName("invisible", Boolean.class), true);
    _setProperty(propertyRegistry.getByName("base_plate", Boolean.class), false);
    _setProperty(propertyRegistry.getByName("no_gravity", Boolean.class), true);  // 新增
    _setProperty(propertyRegistry.getByName("marker", Boolean.class), true);      // 新增
}
```

## 技术细节

### Minecraft 实体元数据结构

实体元数据使用位掩码（bitmask）来存储多个布尔标志：

- **索引 0（通用实体标志）**：
  - `0x01` - On Fire（着火）
  - `0x02` - Crouching/Sneaking（潜行）/ **NO_GRAVITY（无重力）**（1.10+）
  - `0x04` - Unused（未使用）
  - `0x08` - Sprinting（疾跑）
  - `0x10` - Swimming（游泳）
  - `0x20` - Invisible（隐身）
  - `0x40` - Glowing（发光）
  - `0x80` - Flying with Elytra（鞘翅飞行）

- **盔甲架专用索引（版本依赖）**：
  - `0x01` - Small（小型）
  - `0x04` - Has Arms（有手臂）
  - `0x08` - No Base Plate（无底板）
  - `0x10` - **Marker（标记模式）**

### Marker 模式的作用

Marker 模式（1.8+）使盔甲架：
- 完全无碰撞体积（hitbox 为零）
- 不受重力影响
- 不会被推动
- 不会旋转头部看向玩家

这对于全息文本显示非常重要，因为它确保盔甲架完全"不存在于物理世界"，仅用于显示文本。

## 兼容性

- **Java 版客户端**：完全兼容，这些标记是标准 Minecraft 实体元数据。
- **基岩版客户端（通过 Geyser）**：修复后全息文本将正确显示在预期位置，不再"掉落"。
- **版本支持**：支持 Minecraft 1.8 - 1.21.x 所有版本。

## 测试建议

1. 在纯 Java 版服务器上测试，确认全息文本正常显示
2. 在 Geyser 环境中使用基岩版客户端连接，验证全息文本不再下落
3. 测试 NPC 座位功能，确保玩家能正常坐在隐形盔甲架上

## 相关文件

- `plugin/src/main/java/lol/pyr/znpcsplus/entity/EntityPropertyRegistryImpl.java` - 实体属性注册
- `plugin/src/main/java/lol/pyr/znpcsplus/hologram/HologramText.java` - 全息文本实现
- `plugin/src/main/java/lol/pyr/znpcsplus/entity/ArmorStandVehicleProperties.java` - 座位盔甲架属性
- `plugin/src/main/java/lol/pyr/znpcsplus/entity/properties/BitsetProperty.java` - 位掩码属性基类

## 参考资料

- [Minecraft Wiki - Entity Metadata](https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Entity_metadata)
- [Geyser 项目](https://geysermc.org/)
- [PacketEvents 文档](https://github.com/retrooper/packetevents)
