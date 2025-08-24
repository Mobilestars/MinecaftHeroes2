package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class FireworkUseBlocker implements Listener {

    private final CombatPlugin plugin;

    public FireworkUseBlocker(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUseFirework(PlayerInteractEvent event) {
        if (!plugin.isFireworkPlacementAllowed()) {
            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
                    event.setCancelled(true);
                    String msg = plugin.getLanguage().getMessage("firework.use.disabled");
                    plugin.sendMessage(event.getPlayer(), msg);
                }
            }
        }
    }
}
