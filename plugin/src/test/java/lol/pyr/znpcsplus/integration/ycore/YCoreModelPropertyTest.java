package lol.pyr.znpcsplus.integration.ycore;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import lol.pyr.znpcsplus.PacketEventsTestSupport;
import lol.pyr.znpcsplus.entity.EntityPropertyImpl;
import lol.pyr.znpcsplus.entity.PacketEntity;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.util.LazyLoader;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class YCoreModelPropertyTest extends PacketEventsTestSupport {
    @Test
    void modelEquipmentAndInvisibilityAreMergedWithExistingFlags() {
        PacketFactory factory = mock(PacketFactory.class);
        PacketEntity entity = mock(PacketEntity.class);
        Player player = mock(Player.class);
        ItemStack modelItem = mock(ItemStack.class);
        EntityPropertyImpl<ItemStack> helmetProperty = mock(EntityPropertyImpl.class);
        YCoreModelDefinition model = mock(YCoreModelDefinition.class);
        when(model.createPacketItem()).thenReturn(modelItem);
        when(model.isInvisible()).thenReturn(true);

        YCoreModelProperty property = new YCoreModelProperty(factory,
                LazyLoader.of(() -> helmetProperty), key -> model);
        when(entity.getProperty(property)).thenReturn("waigame:bear");
        Map<Integer, EntityData<?>> metadata = new HashMap<>();
        metadata.put(0, new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x01));

        property.apply(player, entity, true, metadata);

        verify(factory).sendEquipment(player, entity, new Equipment(EquipmentSlot.HELMET, modelItem));
        assertEquals((byte) 0x21, metadata.get(0).getValue());
        verify(entity, never()).getProperty(helmetProperty);
    }

    @Test
    void unavailableModelFallsBackToOriginalHelmet() {
        PacketFactory factory = mock(PacketFactory.class);
        PacketEntity entity = mock(PacketEntity.class);
        Player player = mock(Player.class);
        ItemStack helmet = mock(ItemStack.class);
        EntityPropertyImpl<ItemStack> helmetProperty = mock(EntityPropertyImpl.class);
        YCoreModelProperty property = new YCoreModelProperty(factory,
                LazyLoader.of(() -> helmetProperty), key -> {
                    throw new IllegalArgumentException("missing");
                });
        when(entity.getProperty(property)).thenReturn("missing:model");
        when(entity.getProperty(helmetProperty)).thenReturn(helmet);

        property.apply(player, entity, true, new HashMap<>());

        verify(factory).sendEquipment(player, entity, new Equipment(EquipmentSlot.HELMET, helmet));
    }
}
