package lol.pyr.znpcsplus.entity;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import lol.pyr.znpcsplus.api.entity.PropertyHolder;
import lol.pyr.znpcsplus.packets.PacketFactory;
import lol.pyr.znpcsplus.util.FutureUtil;
import lol.pyr.znpcsplus.util.NpcLocation;
import lol.pyr.znpcsplus.util.Viewable;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Packet entity used by independently positioned hologram lines.
 * It never creates or accepts vehicle/passenger relationships.
 */
public class HologramPacketEntity extends PacketEntity {

    public HologramPacketEntity(PacketFactory packetFactory, PropertyHolder properties,
                                Viewable viewable, EntityType type, NpcLocation location) {
        super(packetFactory, properties, viewable, type, location);
    }

    @Override
    public void setLocation(NpcLocation location) {
        this.location = location;
        for (Player viewer : getViewable().getViewers()) {
            packetFactory.teleportEntity(viewer, this);
        }
    }

    @Override
    public CompletableFuture<Void> spawn(Player player) {
        return FutureUtil.exceptionPrintingRunAsync(() -> {
            if (getType() == EntityTypes.PLAYER) {
                packetFactory.spawnPlayer(player, this, getProperties()).join();
            } else {
                packetFactory.spawnEntity(player, this, getProperties());
            }
        });
    }

    @Override
    public void setVehicle(PacketEntity vehicle) {
        throw new UnsupportedOperationException("Independent hologram entities cannot have vehicles");
    }

    @Override
    public void setVehicleId(Integer vehicleId) {
        throw new UnsupportedOperationException("Independent hologram entities cannot have vehicles");
    }

    @Override
    public void addPassenger(int entityId) {
        throw new UnsupportedOperationException("Independent hologram entities cannot have passengers");
    }

    @Override
    public void removePassenger(int entityId) {
        throw new UnsupportedOperationException("Independent hologram entities cannot have passengers");
    }
}
