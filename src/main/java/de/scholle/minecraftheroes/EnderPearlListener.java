package de.scholle.minecraftheroes;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class EnderPearlListener implements Listener {

    private final CombatPlugin plugin;

    public EnderPearlListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnderPearlUse(PlayerTeleportEvent event) {
        if (event.getCause() != TeleportCause.ENDER_PEARL) return;
        Player player = event.getPlayer();
        EnderPearlManager manager = plugin.getEnderPearlManager();

        if (!manager.canUseEnderPearl(player)) {
            event.setCancelled(true);
            String msg = plugin.getLanguage()
                    .getMessage("enderpearl.limit")
                    .replace("%max%", String.valueOf(manager.getMaxPearls()));
            plugin.sendMessage(player, msg);
        } else {
            manager.recordEnderPearlUse(player);
        }
    }
}
