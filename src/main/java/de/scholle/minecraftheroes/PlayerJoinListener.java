package de.scholle.minecraftheroes;

import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final CombatPlugin plugin;

    public PlayerJoinListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!this.plugin.hasSavedLives(uuid))
            this.plugin.setLives(uuid, 3);
    }
}
