package lol.pyr.znpcsplus.entity.properties;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import lol.pyr.znpcsplus.entity.EntityPropertyImpl;
import lol.pyr.znpcsplus.entity.PacketEntity;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.util.LazyLoader;
import org.bukkit.entity.Player;

import java.util.Map;

public class EquipmentProperty extends EntityPropertyImpl<ItemStack> {
    private final PacketFactory packetFactory;
    private final EquipmentSlot slot;
    private final LazyLoader<EntityPropertyImpl<String>> ycoreModelProperty;

    public EquipmentProperty(PacketFactory packetFactory, String name, EquipmentSlot slot) {
        this(packetFactory, name, slot, null);
    }

    public EquipmentProperty(PacketFactory packetFactory, String name, EquipmentSlot slot,
                             LazyLoader<EntityPropertyImpl<String>> ycoreModelProperty) {
        super(name, null, ItemStack.class);
        this.packetFactory = packetFactory;
        this.slot = slot;
        this.ycoreModelProperty = ycoreModelProperty;
    }

    @Override
    public void apply(Player player, PacketEntity entity, boolean isSpawned, Map<Integer, EntityData<?>> properties) {
        if (ycoreModelProperty != null) {
            EntityPropertyImpl<String> property = ycoreModelProperty.get();
            if (property != null && entity.hasProperty(property)) return;
        }
        packetFactory.sendEquipment(player, entity, new Equipment(slot, entity.getProperty(this)));
    }
}
