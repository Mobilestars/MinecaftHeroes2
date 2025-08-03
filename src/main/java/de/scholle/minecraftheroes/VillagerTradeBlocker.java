package de.scholle.minecraftheroes;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerTradeBlocker implements Listener {

    private final boolean villagerTradingEnabled;

    public VillagerTradeBlocker(boolean villagerTradingEnabled) {
        this.villagerTradingEnabled = villagerTradingEnabled;
    }

    @EventHandler
    public void onPlayerInteractWithVillager(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) {
            return; // Nur für Dorfbewohner
        }

        if (!villagerTradingEnabled) {
            event.setCancelled(true); // Verhindert den Handel, wenn deaktiviert
        }
    }
}
