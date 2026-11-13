/*
 * Copyright (c) 2026.
 * # 太霄玉府五雷使院镇煞符
 * # 敕令：诸 BUG 急退，急急如律令！
 * #
 * # 雷  火  雷
 * #    部  令
 * # 雷  火  雷
 * #
 * # 净天地神咒（节选）
 * # 天地自然，秽气分散，洞中玄虚，晃朗太元；
 * # 八方威神，使我自然，灵宝符命，普告九天。
 * #
 * # 本代码受太上老君、九天应元雷声普化天尊庇佑，
 * # 如生 BUG，则坎离交泰，雷火丹成，BUG 自化虚无。
 *
 */

package lol.pyr.znpcsplus.integration.ycore;

import cn.cyanbukkit.ycore.utils.model.TextureRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public final class YCoreIntegration {
    private static final Logger LOGGER = Logger.getLogger("ZNPCsPlus");
    private static boolean enabled;

    private YCoreIntegration() {
    }

    public static void initialize() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("YCore-Bukkit");
        enabled = plugin != null && plugin.isEnabled();
        LOGGER.info(enabled
                ? "[ZNPCsPlus] YCore armor-stand model support enabled"
                : "[ZNPCsPlus] YCore-Bukkit not found, skipping model integration");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static YCoreModelDefinition requireModel(String key) {
        if (!enabled) throw new IllegalArgumentException("YCore-Bukkit integration is not enabled");
        return YCoreModelDefinition.fromEntry(TextureRegistry.INSTANCE.entry(key));
    }
}
