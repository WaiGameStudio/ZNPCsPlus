package lol.pyr.znpcsplus.commands;

import lol.pyr.director.adventure.command.CommandContext;
import lol.pyr.director.adventure.command.CommandHandler;
import lol.pyr.director.common.command.CommandExecutionException;
import lol.pyr.znpcsplus.integration.spigotmaster.SpigotMasterIntegration;
import lol.pyr.znpcsplus.npc.NpcEntryImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Example command to send custom skin to NPC
 * Usage: /npc customskin <npc_id> <skin_path>
 */
public class NpcCustomSkinCommand implements CommandHandler {

    @Override
    public void run(CommandContext context) throws CommandExecutionException {
        context.setUsage(context.getLabel() + " customskin <id> <skin_path>");

        Player player = context.ensureSenderIsPlayer();
        NpcEntryImpl entry = context.parse(NpcEntryImpl.class);
        context.ensureArgsNotEmpty();
        String skinPath = context.dumpAllArgs();

        if (!SpigotMasterIntegration.getInstance().isEnabled()) {
            context.halt(Component.text("SpigotMaster integration is not enabled", NamedTextColor.RED));
        }

        // Send custom skin event to client
        SpigotMasterIntegration.getInstance()
            .getVisualSender()
            .sendNpcSkin(player, entry.getId(), skinPath);

        context.send(Component.text("Custom skin event sent to client: " + skinPath, NamedTextColor.GREEN));
    }
}