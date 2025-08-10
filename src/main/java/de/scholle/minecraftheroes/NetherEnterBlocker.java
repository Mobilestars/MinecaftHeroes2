package de.scholle.minecraftheroes;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class NetherEnterBlocker implements Listener {

    private final CombatPlugin plugin;

    public NetherEnterBlocker(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (!plugin.isNoNether()) return;

        World.Environment toEnv = event.getTo().getWorld().getEnvironment();
        if (toEnv == World.Environment.NETHER) {
            event.setCancelled(true);
            plugin.sendMessage(event.getPlayer(), "§cDer Nether ist derzeit deaktiviert.");
        }
    }
}
