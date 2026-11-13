package lol.pyr.znpcsplus.entity.properties;

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

import static org.mockito.Mockito.*;

class EquipmentPropertyTest extends PacketEventsTestSupport {
    @Test
    void activeYCoreModelOwnsTheHelmetSlot() {
        PacketFactory factory = mock(PacketFactory.class);
        PacketEntity entity = mock(PacketEntity.class);
        Player player = mock(Player.class);
        EntityPropertyImpl<String> modelProperty = mock(EntityPropertyImpl.class);
        EquipmentProperty helmet = new EquipmentProperty(factory, "helmet", EquipmentSlot.HELMET,
                LazyLoader.of(() -> modelProperty));
        when(entity.hasProperty(modelProperty)).thenReturn(true);

        helmet.apply(player, entity, true, new HashMap<>());

        verifyNoInteractions(factory);
    }

    @Test
    void helmetWorksNormallyWithoutYCoreModel() {
        PacketFactory factory = mock(PacketFactory.class);
        PacketEntity entity = mock(PacketEntity.class);
        Player player = mock(Player.class);
        ItemStack item = mock(ItemStack.class);
        EntityPropertyImpl<String> modelProperty = mock(EntityPropertyImpl.class);
        EquipmentProperty helmet = new EquipmentProperty(factory, "helmet", EquipmentSlot.HELMET,
                LazyLoader.of(() -> modelProperty));
        when(entity.hasProperty(modelProperty)).thenReturn(false);
        when(entity.getProperty(helmet)).thenReturn(item);

        helmet.apply(player, entity, true, new HashMap<>());

        verify(factory).sendEquipment(player, entity, new Equipment(EquipmentSlot.HELMET, item));
    }
}
