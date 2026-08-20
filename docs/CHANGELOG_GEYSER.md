# Geyser 兼容性修复 - 更新日志

## 版本：2.1.0+

### 修复的问题

1. ✅ **全息文本掉落** - 基岩版客户端中全息文本不再受重力影响掉落到地面
2. ✅ **坐下 NPC 钻地** - 坐姿 NPC 不再沉入地下
3. ✅ **全息跟随坐下** - 全息文本保持在 NPC 头顶，不受坐姿影响

### 核心修改

#### 1. 新增实体属性（3 个文件）

- `no_gravity` (0x02) - 防止全息盔甲架受重力影响
- `marker` (0x10) - 启用盔甲架标记模式（无碰撞、无物理）

**修改文件**：
- `EntityPropertyRegistryImpl.java` - 注册属性
- `HologramText.java` - 应用到全息文本
- `ArmorStandVehicleProperties.java` - 应用到座位盔甲架

#### 2. 可配置座位偏移（2 个文件）

新增配置项 `sitting-offset`，默认值 `-0.55`（Geyser 兼容）

**修改文件**：
- `MainConfig.java` - 配置接口
- `EntitySittingProperty.java` - 使用配置值

#### 3. 全息位置逻辑优化（1 个文件）

全息文本位置基于 NPC 逻辑坐标，而非实际渲染坐标

**修改文件**：
- `NpcImpl.java` - `setLocation()` 方法

### 配置说明

新增配置项（`config.yml`）：

```yaml
# 坐下 NPC 的 Y 轴偏移量
# 默认: -0.55 (Geyser 兼容)
# 纯 Java 版可用: -0.9
sitting-offset: -0.55
```

### 兼容性

- ✅ Minecraft 1.8 - 1.21.x 全版本
- ✅ Java 版客户端（无任何副作用）
- ✅ 基岩版客户端（通过 Geyser）

### 升级说明

1. 替换插件 jar 文件
2. 重启服务器（会自动生成新配置项）
3. 如需调整坐姿效果，编辑 `config.yml` 中的 `sitting-offset`

### 测试要点

**Java 版**：
- 全息文本正常悬浮 ✓
- 坐下姿势自然 ✓
- 全息不随坐姿移动 ✓

**基岩版（Geyser）**：
- 全息文本不掉落 ✓
- 坐下不钻地 ✓
- 全息位置正确 ✓

### 技术细节

完整技术文档：`docs/GEYSER_FIX_SUMMARY.md`

---

**重要提示**：如果你运行纯 Java 版服务器（无基岩版玩家），可以将 `sitting-offset` 改为 `-0.9` 以获得更自然的坐姿效果。
