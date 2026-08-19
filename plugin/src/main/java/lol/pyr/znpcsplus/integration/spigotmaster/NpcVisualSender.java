package lol.pyr.znpcsplus.integration.spigotmaster;

import com.neteasemc.spigotmaster.SpigotMaster;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * NPC 视觉同步发送器，用于向客户端发送 NPC 皮肤/模型变更事件
 */
public final class NpcVisualSender {

    private static final String MOD_NAMESPACE = "waigame_npc";
    private static final String SERVER_SYSTEM = "WaiGameServerSystem";
    private static final String CLIENT_SYSTEM = "WaiGameClientSystem";
    private static final String EVENT_NPC_VISUAL = "NpcVisualSyncEvent";

    private final SpigotMaster spigotMaster;

    public NpcVisualSender(SpigotMaster spigotMaster) {
        this.spigotMaster = spigotMaster;
    }

    /**
     * 发送 NPC 皮肤变更事件
     *
     * @param viewer 观察者玩家
     * @param npcName NPC 名称（建议使用 GameProfile 名称）
     * @param skin 皮肤资源路径（相对于客户端资源包 textures/models，不带 .png）
     */
    public void sendNpcSkin(Player viewer, String npcName, String skin) {
        Map<String, Object> data = new HashMap<>();
        data.put("npcName", npcName);
        data.put("visualType", "skin");
        data.put("skin", skin);

        spigotMaster.notifyToClient(
            viewer,
            MOD_NAMESPACE,
            SERVER_SYSTEM,
            EVENT_NPC_VISUAL,
            data
        );
    }

    /**
     * 发送 NPC 模型变更事件（网易骨骼模型）
     *
     * @param viewer 观察者玩家
     * @param npcName NPC 名称（建议使用 GameProfile 名称）
     * @param modelName 模型名称
     * @param texture 材质资源路径
     */
    public void sendNpcModel(Player viewer, String npcName, String modelName, String texture) {
        Map<String, Object> data = new HashMap<>();
        data.put("npcName", npcName);
        data.put("visualType", "model");
        data.put("model", modelName);
        data.put("texture", texture);

        spigotMaster.notifyToClient(
            viewer,
            MOD_NAMESPACE,
            SERVER_SYSTEM,
            EVENT_NPC_VISUAL,
            data
        );
    }
}