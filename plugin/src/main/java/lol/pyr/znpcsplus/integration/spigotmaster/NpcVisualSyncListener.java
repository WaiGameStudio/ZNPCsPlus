package lol.pyr.znpcsplus.integration.spigotmaster;

import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.npc.NpcEntryImpl;
import lol.pyr.znpcsplus.npc.NpcImpl;
import org.bukkit.entity.Player;

/**
 * NPC 视觉同步监听器
 * 当 NPC 显示给玩家时，自动发送视觉同步事件到客户端
 */
public class NpcVisualSyncListener {

    private final NpcVisualSender visualSender;

    public NpcVisualSyncListener(NpcVisualSender visualSender) {
        this.visualSender = visualSender;
    }

    /**
     * 当 NPC 显示给玩家时调用
     *
     * @param player 玩家
     * @param entry NPC 条目
     */
    public void onNpcShow(Player player, NpcEntryImpl entry) {
        if (!SpigotMasterIntegration.getInstance().isEnabled()) {
            return;
        }

        NpcImpl npc = entry.getNpc();
        String npcId = entry.getId();

        // TODO: 这里可以根据 NPC 的属性决定发送什么类型的视觉事件
        // 示例：如果 NPC 有自定义皮肤属性，发送皮肤事件
        // 示例：如果 NPC 有自定义模型属性，发送模型事件

        // 当前仅作为占位符，实际使用时需要根据 NPC 属性判断
        // visualSender.sendNpcSkin(player, npcId, "path/to/skin");
    }

    /**
     * 当 NPC 属性变更时调用
     *
     * @param player 玩家
     * @param entry NPC 条目
     * @param property 变更的属性
     * @param value 新值
     */
    public <T> void onNpcPropertyChange(Player player, NpcEntryImpl entry, EntityProperty<T> property, T value) {
        if (!SpigotMasterIntegration.getInstance().isEnabled()) {
            return;
        }

        // TODO: 根据属性类型发送相应的视觉同步事件
        // 例如：如果是皮肤属性变更，发送皮肤事件
    }
}