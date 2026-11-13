# ZNPCsPlus [![](https://img.shields.io/discord/1099449144948555957?label=Discord&logo=Discord&style=plastic)](https://discord.gg/MAZz6XpPcg) [![](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.pyr.lol%2Fjob%2FZNPCsPlus%2F&style=plastic&logo=jenkins)](https://ci.pyr.lol/job/ZNPCsPlus/)
[![](https://img.shields.io/bstats/players/18244?style=plastic&label=bStats%20Players)]((https://bstats.org/plugin/bukkit/ZNPCsPlus/18244/)) [![](https://img.shields.io/bstats/servers/18244?style=plastic&label=bStats%20Servers)]((https://bstats.org/plugin/bukkit/ZNPCsPlus/18244/)) [![](https://img.shields.io/spiget/downloads/109380?style=plastic&label=Spigot%20Downloads)]((https://www.spigotmc.org/resources/znpcsplus.109380/))

[ZNPCsPlus](https://www.spigotmc.org/resources/znpcsplus.109380/) is a Spigot plugin that is used to create fake entities 
that players can interact with to perform actions like switching servers on a network or executing commands.

This plugin is a remake of a plugin called ZNPCs, we originally started because the maintainer of ZNPCs decided to announce that he was 
[dropping support for the plugin](https://github.com/Pyrbu/ZNPCsPlus/blob/2.X/.github/znpc.png?raw=true).

Looking for up-to-date builds of the plugin? Check out our [Jenkins](https://ci.pyr.lol/job/ZNPCsPlus/)

## Why is it so good?
- 100% Packet Based - Nothing is ran on the main thread
- Performance & stability oriented code
- Support for all versions from 1.8 to 26.3, including 1.21.11
- Support for multiple different storage options
- Intuitive command system

### Requirements, Extensions & Supported Software
Requirements:
- Java 17+ (Java 25 for Minecraft 26.3)
- Minecraft 1.8 - 26.3

Supported Softwares:
- Spigot ([Website](https://www.spigotmc.org/))
- Paper ([Github](https://github.com/PaperMC/Paper)) ([Website](https://papermc.io/software/paper))
- Folia ([Github](https://github.com/PaperMC/Folia)) ([Website](https://papermc.io/software/folia))
- ArcLight ([Github](https://github.com/IzzelAliz/Arclight))

Optional Dependencies/Extensions:
- PlaceholderAPI
- SpigotMaster
- YCore-Bukkit 0.6.9 (custom armor-stand entity models)

## YCore custom entity models (自定义实体盔甲架)

YCore 中已发布的实体模型可以直接赋给盔甲架 NPC，模型仍由 ZNPCsPlus 的纯数据包实体展示，不需要生成真实 Bukkit 实体。全息、交互、移动、装备、姿势、朝向和 YAML/SQLite/MySQL 存储仍沿用原有 NPC 功能。

支持的 YCore 条目必须满足：`type=ENTITY`、状态为 `PUBLISHED`、`base=ARMOR_STAND`、`render=HELMET`。读取条目的 `material`、CustomModelData 和 `invisible` 设置；`ITEM_DISPLAY`、`MARKER` 及 `MobModelManager` 动画模型不在此属性的支持范围内。

```text
/npc create <id> armor_stand
/npc property set <id> ycore_model <namespace:model>
```

例如，使用 YCore 内置的 `waigame:bear`（PAPER，CustomModelData=2）：

```text
/npc create bear armor_stand
/npc property set bear ycore_model waigame:bear
```

移除模型并恢复原先的头盔和隐形属性：

```text
/npc property remove bear ycore_model
```

模型激活时，`ycore_model` 优先于 `helmet`；移除后会恢复原头盔。YCore 的 `invisible` 会与 NPC 的其他元数据位合并，不会清除火焰等现有状态。

### 26.3 additions

- PacketEvents 2.14.0 / protocol 777.
- New NPC types: `cushion`, `poplar_boat`, `poplar_chest_boat`.
- New `cushion_color` property using the 26.3 `DYE_COLOR` metadata serializer.

### Build and verification

```bash
./gradlew build
./gradlew :plugin:test :plugin:shadowJar
./gradlew :plugin:runServer
```

`runServer` uses Paper 26.3 and Java 25. The plugin itself retains the Java 17 compilation target.

## Found a bug?
Open an issue in the GitHub [issue tracker](https://github.com/Pyrbu/ZNPCsPlus/issues) or join our [support discord](https://discord.gg/MAZz6XpPcg)

## BStats
[![](https://bstats.org/signatures/bukkit/znpcsplus.svg)](https://bstats.org/plugin/bukkit/ZNPCsPlus/18244/)

#### Like what you see? Want the project to continue improving? Consider starring the repository & leaving a positive review on [Spigot](https://www.spigotmc.org/resources/znpcsplus.109380/)!

## Credits
- [PacketEvents 2.0](https://github.com/retrooper/packetevents) - Packet library
- [Minecraft Wiki Protocol (formally wiki.vg)](https://minecraft.wiki/w/Minecraft_Wiki:Projects/wiki.vg_merge/Main_Page) - Minecraft protocol documentation
- [gson](https://github.com/google/gson) - JSON parsing library made by Google
- [Mineskin.org](https://mineskin.org/) - Website for raw skin file uploads
- [adventure](https://docs.advntr.dev/) - Minecraft text api
- [DazzleConf](https://github.com/A248/DazzleConf) - Configuration library
- [Director](https://github.com/Pyrbu/Director) - Command library
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) - Universal string placeholder library
