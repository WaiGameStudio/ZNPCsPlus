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

import cn.cyanbukkit.ycore.utils.model.EntityRender;
import cn.cyanbukkit.ycore.utils.model.TextureEntry;
import cn.cyanbukkit.ycore.utils.model.TextureType;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

final class YCoreModelDefinition {
    private final Material material;
    private final int modelId;
    private final boolean invisible;

    private YCoreModelDefinition(Material material, int modelId, boolean invisible) {
        this.material = material;
        this.modelId = modelId;
        this.invisible = invisible;
    }

    static YCoreModelDefinition fromEntry(TextureEntry entry) {
        if (entry == null || entry.getType() != TextureType.ENTITY || !entry.getUsable()) {
            throw new IllegalArgumentException("YCore entity model is not registered or published");
        }
        if (!"ARMOR_STAND".equalsIgnoreCase(entry.getBase() == null ? "" : entry.getBase().trim())) {
            throw new IllegalArgumentException("YCore model " + entry.getKey() + " must use the ARMOR_STAND base");
        }
        if (entry.getRender() != EntityRender.HELMET) {
            throw new IllegalArgumentException("YCore model " + entry.getKey() + " must use HELMET rendering");
        }
        Integer modelId = entry.getModelId();
        if (modelId == null) throw new IllegalArgumentException("YCore model " + entry.getKey() + " has no CustomModelData");
        Material material = entry.getMaterial() == null
                ? Material.PAPER
                : Material.matchMaterial(entry.getMaterial());
        if (material == null) material = Material.PAPER;
        return new YCoreModelDefinition(material, modelId, entry.getInvisible());
    }

    com.github.retrooper.packetevents.protocol.item.ItemStack createPacketItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            try {
                try {
                    meta.getClass().getMethod("setCustomModelData", int.class).invoke(meta, modelId);
                } catch (NoSuchMethodException ignored) {
                    meta.getClass().getMethod("setCustomModelData", Integer.class).invoke(meta, modelId);
                }
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("YCore models require an ItemMeta implementation with CustomModelData", exception);
            }
            item.setItemMeta(meta);
        }
        return SpigotConversionUtil.fromBukkitItemStack(item);
    }

    boolean isInvisible() {
        return invisible;
    }
}
