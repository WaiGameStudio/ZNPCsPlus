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

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import lol.pyr.znpcsplus.entity.EntityPropertyImpl;
import lol.pyr.znpcsplus.entity.PacketEntity;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.util.LazyLoader;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class YCoreModelProperty extends EntityPropertyImpl<String> {
    private static final Logger LOGGER = Logger.getLogger("ZNPCsPlus");

    private final PacketFactory packetFactory;
    private final LazyLoader<EntityPropertyImpl<ItemStack>> helmetProperty;
    private final Function<String, YCoreModelDefinition> modelResolver;
    private final Set<String> warnedModels = ConcurrentHashMap.newKeySet();

    public YCoreModelProperty(PacketFactory packetFactory, LazyLoader<EntityPropertyImpl<ItemStack>> helmetProperty) {
        this(packetFactory, helmetProperty, YCoreIntegration::requireModel);
    }

    YCoreModelProperty(PacketFactory packetFactory, LazyLoader<EntityPropertyImpl<ItemStack>> helmetProperty,
                       Function<String, YCoreModelDefinition> modelResolver) {
        super("ycore_model", null, String.class);
        this.packetFactory = packetFactory;
        this.helmetProperty = helmetProperty;
        this.modelResolver = modelResolver;
    }

    @Override
    public void validatePlayerValue(String value) {
        if (value != null) modelResolver.apply(value);
    }

    @Override
    public void apply(Player player, PacketEntity entity, boolean isSpawned, Map<Integer, EntityData<?>> properties) {
        String key = entity.getProperty(this);
        if (key == null) return;

        YCoreModelDefinition model = null;
        try {
            model = modelResolver.apply(key);
        } catch (IllegalArgumentException exception) {
            if (warnedModels.add(key)) {
                LOGGER.warning("[ZNPCsPlus] Cannot render YCore model " + key + ": " + exception.getMessage());
            }
        }

        ItemStack item = model == null ? entity.getProperty(helmetProperty.get()) : model.createPacketItem();
        packetFactory.sendEquipment(player, entity, new Equipment(EquipmentSlot.HELMET, item));
        if (model == null || !model.isInvisible()) return;

        EntityData<?> oldData = properties.get(0);
        byte flags = oldData != null && oldData.getValue() instanceof Number
                ? ((Number) oldData.getValue()).byteValue()
                : 0;
        properties.put(0, newEntityData(0, EntityDataTypes.BYTE, (byte) (flags | 0x20)));
    }
}
