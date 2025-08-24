package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

public class CobwebListener implements Listener {

    private final CombatPlugin plugin;

    public CobwebListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getBlockPlaced().getType() == Material.COBWEB) {
            CobwebManager cobwebManager = plugin.getCobwebManager();
            if (!cobwebManager.canPlaceCobweb(player)) {
                event.setCancelled(true);
                plugin.sendMessage(player, + cobwebManager.getMaxCobwebs() + " Cobwebs platzieren!");
            } else {
                cobwebManager.recordCobwebPlacement(player);
            }
        }
    }
}
