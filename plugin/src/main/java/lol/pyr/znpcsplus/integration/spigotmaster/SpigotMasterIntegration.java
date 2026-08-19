package lol.pyr.znpcsplus.integration.spigotmaster;

import com.neteasemc.spigotmaster.SpigotMaster;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * SpigotMaster Integration Manager
 */
public class SpigotMasterIntegration {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static SpigotMasterIntegration instance;

    private final SpigotMaster spigotMaster;
    private final NpcVisualSender visualSender;
    private final boolean enabled;

    private SpigotMasterIntegration() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SpigotMaster");

        if (plugin instanceof SpigotMaster) {
            this.spigotMaster = (SpigotMaster) plugin;
            this.visualSender = new NpcVisualSender(spigotMaster);
            this.enabled = true;
            LOGGER.info("[ZNPCsPlus] SpigotMaster integration enabled");
        } else {
            this.spigotMaster = null;
            this.visualSender = null;
            this.enabled = false;
            LOGGER.info("[ZNPCsPlus] SpigotMaster not found, skipping integration");
        }
    }

    /**
     * Initialize SpigotMaster integration
     */
    public static void initialize() {
        if (instance == null) {
            instance = new SpigotMasterIntegration();
        }
    }

    /**
     * Get integration instance
     */
    public static SpigotMasterIntegration getInstance() {
        return instance;
    }

    /**
     * Check if SpigotMaster is available
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get NPC visual sender
     */
    public NpcVisualSender getVisualSender() {
        if (!enabled) {
            throw new IllegalStateException("SpigotMaster integration not enabled");
        }
        return visualSender;
    }

    /**
     * Get SpigotMaster instance
     */
    public SpigotMaster getSpigotMaster() {
        if (!enabled) {
            throw new IllegalStateException("SpigotMaster integration not enabled");
        }
        return spigotMaster;
    }
}
