package lol.pyr.znpcsplus.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.config.ConfigManager;
import lol.pyr.znpcsplus.entity.EntityPropertyRegistryImpl;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.util.NpcLocation;
import lol.pyr.znpcsplus.util.Viewable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class HologramText extends HologramLine<Component> {

    private static final Component BLANK  = Component.text("%blank%");
    private static final double ARMOR_STAND_NAME_OFFSET = 1.975D;

    public HologramText(Viewable viewable, EntityPropertyRegistryImpl propertyRegistry, ConfigManager configManager, PacketFactory packetFactory, NpcLocation location, Component text) {
        super(viewable, text, packetFactory, EntityTypes.ARMOR_STAND, location, configManager);
        addProperty(propertyRegistry.getByName("name"));
        addProperty(propertyRegistry.getByName("invisible"));
        addProperty(propertyRegistry.getByName("no_gravity"));
        addProperty(propertyRegistry.getByName("marker"));
    }

    @Override
    public void setLocation(NpcLocation location) {
        super.setLocation(isIndependent()
                ? location
                : location.withY(location.getY() - ARMOR_STAND_NAME_OFFSET));
    }

    @Override
    public CompletableFuture<Void> show(Player player) {
        if (getValue().equals(BLANK)) return CompletableFuture.completedFuture(null);
        return super.show(player);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(EntityProperty<T> key) {
        if (key.getName().equalsIgnoreCase("invisible")) return (T) Boolean.TRUE;
        if (key.getName().equalsIgnoreCase("no_gravity")) return (T) Boolean.TRUE;
        if (key.getName().equalsIgnoreCase("marker")) return (T) Boolean.TRUE;
        if (key.getName().equalsIgnoreCase("name")) return (T) getValue();
        return super.getProperty(key);
    }

    @Override
    public boolean hasProperty(EntityProperty<?> key) {
        return key.getName().equalsIgnoreCase("name")
            || key.getName().equalsIgnoreCase("invisible")
            || key.getName().equalsIgnoreCase("no_gravity")
            || key.getName().equalsIgnoreCase("marker");
    }
}
