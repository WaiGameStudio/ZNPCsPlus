# Geyser 兼容性修复总结

## 修复的问题

### 1. 全息文本掉落到地面
**现象**：基岩版客户端连接 Geyser 时，NPC 的全息文本会掉落到地面或贴在 NPC 脚下。

**原因**：全息文本使用的盔甲架实体缺少基岩版所需的物理标记。

### 2. 坐下的 NPC 钻入地下
**现象**：设置为坐姿的 NPC 会部分或完全沉入地下。

**原因**：座位盔甲架的 Y 偏移量 `-0.9` 适合 Java 版，但基岩版处理乘骑高度的方式不同。

### 3. 全息文本跟随 NPC "坐下"
**现象**：当 NPC 坐下时，全息文本也会跟着下移。

**原因**：全息文本的位置基于 NPC 实体的实际 Y 坐标（包括乘骑偏移），而不是 NPC 的逻辑位置。

## 解决方案

### 修改 1：为全息盔甲架添加物理标记

**文件**：`EntityPropertyRegistryImpl.java`、`HologramText.java`、`ArmorStandVehicleProperties.java`

添加两个关键属性：
- `no_gravity` (0x02) - 防止盔甲架受重力影响
- `marker` (0x10) - 启用标记模式（无碰撞、无 AI）

```java
// EntityPropertyRegistryImpl.java - 注册属性
register(new BitsetProperty("no_gravity", 0, 0x02));
register(new BitsetProperty("marker", armorStandIndex++, 0x10));

// HologramText.java - 应用到全息文本
addProperty(propertyRegistry.getByName("no_gravity"));
addProperty(propertyRegistry.getByName("marker"));

// ArmorStandVehicleProperties.java - 应用到座位盔甲架
_setProperty(propertyRegistry.getByName("no_gravity", Boolean.class), true);
_setProperty(propertyRegistry.getByName("marker", Boolean.class), true);
```

### 修改 2：调整座位偏移量（可配置）

**文件**：`EntitySittingProperty.java`、`MainConfig.java`

将座位 Y 偏移从固定的 `-0.9` 改为可配置的值，默认 `-0.55`。

```java
// MainConfig.java - 新增配置项
@ConfKey("sitting-offset")
@ConfComments({
    "The Y offset for sitting NPCs (armor stand vehicle position)",
    "Default: -0.55 (Geyser-compatible)",
    "Java-only servers may prefer: -0.9"
})
@DefaultDouble(-0.55D)
double sittingOffset();

// EntitySittingProperty.java - 使用配置值
double offset = configManager.getConfig().sittingOffset();
PacketEntity vehiclePacketEntity = new PacketEntity(
    packetFactory, 
    new ArmorStandVehicleProperties(propertyRegistry),
    entity.getViewable(), 
    EntityTypes.ARMOR_STAND, 
    entity.getLocation().withY(entity.getLocation().getY() + offset)
);
```

### 修改 3：全息文本位置独立于 NPC 乘骑状态

**文件**：`NpcImpl.java`

修改 `setLocation()` 方法，使全息文本始终基于 NPC 的逻辑位置，而不是实际渲染位置。

```java
// NpcImpl.java:103-113
public void setLocation(NpcLocation location) {
    this.location = location;
    // ... 玩家视角处理 ...
    
    entity.setLocation(finalLocation);
    
    // 全息文本始终基于原始 location 的 Y 坐标
    // 这样即使 NPC 坐在盔甲架上，全息文本也保持在正确位置
    hologram.setLocation(location.withY(location.getY() + type.getHologramOffset()));
}
```

**关键改变**：
- **之前**：`hologram.setLocation(finalLocation.withY(finalLocation.getY() + ...))`
- **之后**：`hologram.setLocation(location.withY(location.getY() + ...))`

这确保了：
1. 当 NPC 坐下时（乘骑盔甲架），`finalLocation` 的 Y 坐标会改变
2. 但全息文本使用原始的 `location.getY()`，保持在 NPC 头顶的正确位置
3. 在 Geyser 环境下，基岩版客户端看到的全息文本不会跟随 NPC "坐下"

## 配置说明

### config.yml 新增选项

```yaml
# 坐下 NPC 的 Y 轴偏移量（盔甲架载具位置）
# 默认: -0.55（兼容 Geyser）
# 纯 Java 版服务器可使用: -0.9
# 如果坐下的 NPC 显得太高或陷入地面，请调整此值
sitting-offset: -0.55
```

### 推荐配置

- **Geyser 服务器**（混合客户端）：`-0.55`（默认值）
- **纯 Java 版服务器**：`-0.9`（更自然的坐姿）
- **需要微调**：在 `-0.4` 到 `-0.7` 之间调整

## 技术细节

### Minecraft 实体元数据

**通用实体标志**（索引 0）：
```
0x01 - On Fire（着火）
0x02 - NO_GRAVITY（无重力）★ 新增
0x20 - Invisible（隐身）
0x40 - Glowing（发光）
```

**盔甲架标志**（版本依赖索引）：
```
0x01 - Small（小型）
0x04 - Has Arms（有手臂）
0x08 - No Base Plate（无底板）
0x10 - MARKER（标记模式）★ 新增
```

### Marker 模式的作用

启用 Marker 模式后，盔甲架：
- 碰撞体积为零（完全无碰撞）
- 不受重力影响（即使没设置 NO_GRAVITY）
- 不会被推动或击退
- 不参与物理模拟

这对全息文本至关重要，确保盔甲架"不存在于物理世界"，仅用于渲染文本。

### 座位偏移量计算

Java 版和基岩版对乘骑实体的 Y 坐标处理不同：

- **Java 版**：乘客的 Y = 载具Y + 载具高度 - 乘客眼高
  - 盔甲架高度约 1.975，玩家实体眼高约 1.62
  - 需要 `-0.9` 偏移才能让 NPC 看起来"坐"在地面上

- **基岩版（通过 Geyser）**：坐标计算略有不同
  - 使用 `-0.9` 会导致 NPC 过低，部分沉入地下
  - 使用 `-0.55` 更接近基岩版的预期表现

## 兼容性

### 客户端兼容性

| 客户端类型 | 全息文本 | 坐下姿势 | 状态 |
|-----------|---------|---------|------|
| Java 版 | ✅ 正常 | ✅ 正常 | 完全兼容 |
| 基岩版（Geyser）| ✅ 修复 | ✅ 修复 | 修复后兼容 |
| 纯基岩版服务器 | ❌ 不适用 | ❌ 不适用 | ZNPCsPlus 不支持 |

### 版本兼容性

- **Minecraft 1.8 - 1.21.x**：全版本支持
- **Marker 属性**：1.8+ 可用
- **NO_GRAVITY 标志**：1.10+ 正式引入，但对 1.8-1.9 也能发送（会被忽略）

## 测试建议

### Java 版客户端测试

1. 创建 NPC 并添加全息文本
2. 验证全息文本正确悬浮在 NPC 头顶
3. 设置 NPC 为坐姿（`/npc toggle entity_sitting`）
4. 验证 NPC 坐下且全息文本保持在头顶

### Geyser + 基岩版客户端测试

1. 使用基岩版客户端连接 Geyser
2. 验证全息文本不会"掉落"，正确悬浮显示
3. 验证坐下的 NPC 不会沉入地下
4. 验证坐下时全息文本不会跟着下移

### 配置调优测试

如果坐下的 NPC 显示不理想：
1. 编辑 `config.yml`，调整 `sitting-offset` 值
2. 重载配置：`/npc reload`
3. 重新测试 NPC 坐姿显示效果
4. 在 `-0.4` 到 `-0.7` 范围内微调

## 相关文件

### 核心修改

- `plugin/src/main/java/lol/pyr/znpcsplus/entity/EntityPropertyRegistryImpl.java` - 注册新属性
- `plugin/src/main/java/lol/pyr/znpcsplus/hologram/HologramText.java` - 全息文本标记
- `plugin/src/main/java/lol/pyr/znpcsplus/entity/ArmorStandVehicleProperties.java` - 座位盔甲架标记
- `plugin/src/main/java/lol/pyr/znpcsplus/entity/properties/EntitySittingProperty.java` - 可配置座位偏移
- `plugin/src/main/java/lol/pyr/znpcsplus/config/MainConfig.java` - 配置接口
- `plugin/src/main/java/lol/pyr/znpcsplus/npc/NpcImpl.java` - 全息位置逻辑

### 基础类

- `plugin/src/main/java/lol/pyr/znpcsplus/entity/properties/BitsetProperty.java` - 位掩码属性基类
- `plugin/src/main/java/lol/pyr/znpcsplus/hologram/HologramLine.java` - 全息行基类

## 参考资料

- [Minecraft Wiki - Entity Metadata](https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Entity_metadata)
- [Geyser 项目](https://geysermc.org/)
- [PacketEvents 文档](https://github.com/retrooper/packetevents)
- [Armor Stand 实体规范](https://minecraft.wiki/w/Armor_Stand)
