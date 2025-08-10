package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class NoOpgapEatListener implements Listener {

    private final CombatPlugin plugin;

    public NoOpgapEatListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        // Prüfen, ob noopgap in config aktiviert ist
        if (!plugin.getConfig().getBoolean("noopgap", false)) {
            // Kein Verbot aktiviert, nichts machen
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cDas Essen von OP-Gaps ist deaktiviert!");
        }
    }
}
