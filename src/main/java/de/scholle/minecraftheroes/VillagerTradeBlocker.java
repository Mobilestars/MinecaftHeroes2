package de.scholle.minecraftheroes;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerTradeBlocker implements Listener {

    private final boolean villagerTradingEnabled;
    private final CombatPlugin plugin;

    public VillagerTradeBlocker(CombatPlugin plugin, boolean villagerTradingEnabled) {
        this.plugin = plugin;
        this.villagerTradingEnabled = villagerTradingEnabled;
    }

    @EventHandler
    public void onPlayerInteractWithVillager(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;

        if (!villagerTradingEnabled) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            plugin.sendMessage(player, plugin.getLanguage().getMessage("villager.trade.disabled"));
        }
    }
}
