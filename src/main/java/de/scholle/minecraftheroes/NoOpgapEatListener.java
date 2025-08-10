package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class NoOpgapEatListener implements Listener {

    private final CombatPlugin plugin;

    public NoOpgapEatListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (!plugin.getConfig().getBoolean("noopgap", false)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            plugin.sendMessage(player, "§cDas Essen von OP-Gaps ist deaktiviert!");
        }
    }
}
