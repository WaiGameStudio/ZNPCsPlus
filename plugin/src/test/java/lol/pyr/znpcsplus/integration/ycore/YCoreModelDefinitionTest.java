package lol.pyr.znpcsplus.integration.ycore;

import cn.cyanbukkit.ycore.utils.model.EntityRender;
import cn.cyanbukkit.ycore.utils.model.ResourceState;
import cn.cyanbukkit.ycore.utils.model.TextureEntry;
import cn.cyanbukkit.ycore.utils.model.TextureType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class YCoreModelDefinitionTest {
    @Test
    void acceptsPublishedArmorStandHelmetModel() {
        YCoreModelDefinition definition = YCoreModelDefinition.fromEntry(entry(
                TextureType.ENTITY, "ARMOR_STAND", EntityRender.HELMET, ResourceState.PUBLISHED));

        assertNotNull(definition);
        assertTrue(definition.isInvisible());
    }

    @Test
    void rejectsNonArmorStandBase() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                YCoreModelDefinition.fromEntry(entry(
                        TextureType.ENTITY, "ZOMBIE", EntityRender.HELMET, ResourceState.PUBLISHED)));

        assertTrue(error.getMessage().contains("ARMOR_STAND"));
    }

    @Test
    void rejectsNonHelmetRenderer() {
        assertThrows(IllegalArgumentException.class, () -> YCoreModelDefinition.fromEntry(entry(
                TextureType.ENTITY, "ARMOR_STAND", EntityRender.ITEM_DISPLAY, ResourceState.PUBLISHED)));
    }

    @Test
    void rejectsUnpublishedOrNonEntityEntries() {
        assertThrows(IllegalArgumentException.class, () -> YCoreModelDefinition.fromEntry(entry(
                TextureType.ENTITY, "ARMOR_STAND", EntityRender.HELMET, ResourceState.DRAFT)));
        assertThrows(IllegalArgumentException.class, () -> YCoreModelDefinition.fromEntry(entry(
                TextureType.ITEM, "ARMOR_STAND", EntityRender.HELMET, ResourceState.PUBLISHED)));
    }

    private static TextureEntry entry(TextureType type, String base, EntityRender render, ResourceState state) {
        return new TextureEntry("waigame:bear", "waigame", "bear", type, 2, state,
                null, 9, 8, "test", "PAPER", null, Collections.emptyList(), base, render, true);
    }
}
