package lol.pyr.znpcsplus;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.impl.netty.NettyManagerImpl;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public abstract class PacketEventsTestSupport {
    protected PacketEventsAPI<Plugin> packetEventsApi;
    private MockedStatic<PacketEvents> packetEvents;

    @BeforeEach
    void setUpPacketEvents() {
        packetEventsApi = mock(PacketEventsAPI.class);
        ServerManager serverManager = mock(ServerManager.class);
        when(packetEventsApi.getServerManager()).thenReturn(serverManager);
        when(packetEventsApi.getSettings()).thenReturn(new PacketEventsSettings());
        when(packetEventsApi.getNettyManager()).thenReturn(new NettyManagerImpl());
        when(packetEventsApi.getLogManager()).thenReturn(mock(com.github.retrooper.packetevents.util.LogManager.class));
        when(serverManager.getVersion()).thenReturn(ServerVersion.V_26_3);
        packetEvents = mockStatic(PacketEvents.class);
        packetEvents.when(PacketEvents::getAPI).thenReturn(packetEventsApi);
    }

    @AfterEach
    void tearDownPacketEvents() {
        packetEvents.close();
    }
}
