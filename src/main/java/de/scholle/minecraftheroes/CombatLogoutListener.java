package de.scholle.minecraftheroes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CombatLogoutListener implements Listener {

    private final CombatPlugin plugin;

    public CombatLogoutListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.isLoseLifeOnLogoutDuringCombatEnabled()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getCombatManager().isInCombat(uuid)) {
            int currentLives = plugin.getLives(uuid);
            int newLives = currentLives - 1;

            if (newLives <= 0) {
                plugin.removePlayer(uuid);
                Bukkit.getLogger().info(player.getName() + " hat während des Kampfes ausgeloggt und alle Leben verloren.");
            } else {
                plugin.setLives(uuid, newLives);
                Bukkit.getLogger().info(player.getName() + " hat während des Kampfes ausgeloggt und ein Leben verloren. Verbleibende Leben: " + newLives);
            }

            CombatManager cm = plugin.getCombatManager();

            UUID opponent = cm.getOpponent(uuid);

            cm.resetCombat(uuid);

            if (opponent != null) {
                cm.resetCombat(opponent);
            }
        }
    }
}
