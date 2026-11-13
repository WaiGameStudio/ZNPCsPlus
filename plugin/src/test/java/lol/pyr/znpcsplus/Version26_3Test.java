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

package lol.pyr.znpcsplus;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import lol.pyr.znpcsplus.config.ConfigManager;
import lol.pyr.znpcsplus.entity.EntityPropertyImpl;
import lol.pyr.znpcsplus.entity.EntityPropertyRegistryImpl;
import lol.pyr.znpcsplus.entity.PacketEntity;
import lol.pyr.znpcsplus.npc.NpcTypeRegistryImpl;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.scheduling.TaskScheduler;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Version26_3Test extends PacketEventsTestSupport {
    @Test
    void registers26_3EntitiesAndCushionMetadata() {
        ConfigManager config = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
        EntityPropertyRegistryImpl properties = new EntityPropertyRegistryImpl(null, config);
        properties.registerTypes(mock(PacketFactory.class),
                LegacyComponentSerializer.builder().character('&').build(), mock(TaskScheduler.class));
        NpcTypeRegistryImpl types = new NpcTypeRegistryImpl();
        types.registerDefault(packetEventsApi, properties);

        assertNotNull(types.getByName("cushion"));
        assertNotNull(types.getByName("poplar_boat"));
        assertNotNull(types.getByName("poplar_chest_boat"));
        EntityPropertyImpl<DyeColor> cushionColor = properties.getByName("cushion_color", DyeColor.class);
        assertTrue(types.getByName("cushion").getAllowedProperties().contains(cushionColor));

        PacketEntity entity = mock(PacketEntity.class);
        when(entity.getProperty(cushionColor)).thenReturn(DyeColor.WHITE);
        Map<Integer, EntityData<?>> metadata = new HashMap<>();
        cushionColor.apply(null, entity, false, metadata);
        assertEquals(8, metadata.keySet().iterator().next());
        assertSame(EntityDataTypes.DYE_COLOR, metadata.get(8).getType());
        assertEquals(com.github.retrooper.packetevents.protocol.color.DyeColor.WHITE, metadata.get(8).getValue());

        assertEquals(777, ServerVersion.V_26_3.getProtocolVersion());
        assertNotNull(EntityTypes.CUSHION);
        assertNotNull(EntityTypes.POPLAR_BOAT);
        assertNotNull(EntityTypes.POPLAR_CHEST_BOAT);
    }
}
