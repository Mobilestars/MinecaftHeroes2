package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        if (!plugin.getConfig().getBoolean("noopgap", false)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            String msg = plugin.getLanguage().getMessage("noopgap.disabled");
            plugin.sendMessage(player, msg);
        }
    }
}
