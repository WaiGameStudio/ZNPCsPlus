# ZNPCsPlus 技术文档

本目录包含 ZNPCsPlus 项目的技术文档和版本支持说明。

## 📚 文档索引

### 1.21.8 版本支持

#### [1.21.8 支持总结](./1.21.8-support-summary.md)
ZNPCsPlus 2.1.0 对 Minecraft 1.21.8 的完整支持验证报告。

**内容概览**:
- ✅ 构建验证结果
- 🔧 技术实现细节
- 📦 JAR 内容验证
- 🎯 支持的版本范围
- 🧪 测试状态

**适用人群**: 项目维护者、贡献者、用户

---

#### [STATUS 阶段修复](./1.21.8-status-phase-fix.md)
1.21.8 服务器列表 ping 时 disconnect 包编码错误的技术修复方案。

**内容概览**:
- 🐛 问题描述与根本原因
- 💡 解决方案架构
- 🔧 实现细节（Netty handler 注入）
- 📊 影响范围与测试结果

**技术关键词**: Netty Pipeline, STATUS Phase, PacketEvents, Reflection

**适用人群**: 开发者、架构师

---

#### [1.21.8 反射策略修复](./1.21.8-reflection-fix.md)
1.21.8 NMS 结构变化导致的反射失败问题及三层渐进式解决方案。

**内容概览**:
- 🚨 错误诊断（NoSuchMethodException）
- 🔍 根本原因分析（MinecraftServer 结构变化）
- 🛠️ 三层反射策略
  - Strategy 1: Getter 方法（1.8-1.21.7）
  - Strategy 2: 字段访问（1.21.8+）
  - Strategy 3: 父类遍历（未来兼容）
- ✅ 验证与测试建议

**技术关键词**: Reflection, NMS, Cross-version Compatibility

**适用人群**: Java 开发者、插件开发者

---

## 🎯 快速导航

### 按读者类型

**用户**:
- [支持总结](./1.21.8-support-summary.md) - 了解插件是否支持你的服务器版本

**插件开发者**:
- [STATUS 阶段修复](./1.21.8-status-phase-fix.md) - 学习 Netty 注入技术
- [反射策略修复](./1.21.8-reflection-fix.md) - 处理跨版本 NMS 兼容性

**项目维护者**:
- 全部三篇文档 - 完整的技术背景和实现细节

### 按技术主题

**Netty & 网络层**:
- [STATUS 阶段修复](./1.21.8-status-phase-fix.md)

**反射 & NMS**:
- [反射策略修复](./1.21.8-reflection-fix.md)

**构建 & 验证**:
- [支持总结](./1.21.8-support-summary.md)

---

## 🔗 相关资源

### 项目文档
- [主 README](../README.md) - 项目概览和使用说明
- [CLAUDE.md](../CLAUDE.md) - 代码架构和开发指南

### 外部链接
- [PacketEvents 文档](https://github.com/retrooper/packetevents)
- [Spigot API 文档](https://hub.spigotmc.org/javadocs/spigot/)
- [Minecraft Protocol 文档](https://wiki.vg/Protocol)

---

## 📝 文档规范

### 创建新文档

1. **文件命名**: `<版本号>-<主题>.md`
   - 示例: `1.21.8-entity-metadata.md`

2. **必备章节**:
   - 问题描述
   - 根本原因
   - 解决方案
   - 验证结果

3. **技术关键词**: 在文档头部标注，方便搜索

4. **更新索引**: 在本 README 中添加链接

### 文档维护

- 技术变更时同步更新相关文档
- 保留历史版本说明（不删除旧版本文档）
- 使用 Git commit 关联文档变更

---

## 📊 版本支持矩阵

| Minecraft 版本 | ZNPCsPlus 版本 | 状态 | 备注 |
|---------------|---------------|-----|-----|
| 1.8.x | 2.1.0 | ✅ 完全支持 | 经典版本 |
| 1.17.x | 2.1.0 | ✅ 完全支持 | Caves & Cliffs |
| 1.19.3+ | 2.1.0 | ✅ 完全支持 | 新协议支持 |
| 1.20.2+ | 2.1.0 | ✅ 完全支持 | 最新特性 |
| 1.21.0-1.21.7 | 2.1.0 | ✅ 完全支持 | Tricky Trials |
| 1.21.8+ | 2.1.0 | ✅ 完全支持 | 反射策略增强 |

---

**最后更新**: 2026-08-17  
**维护者**: ZNPCsPlus 开发团队
