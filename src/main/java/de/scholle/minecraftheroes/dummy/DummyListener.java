package de.scholle.minecraftheroes.dummy;

import de.scholle.minecraftheroes.CombatPlugin;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class DummyListener implements Listener {

    private final CombatPlugin plugin;

    public DummyListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getConfig().getBoolean("dummy.enabled")) return;
        DummyManager.spawnDummy(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DummyManager.onPlayerJoin(player.getUniqueId(), player);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        if (!(victim instanceof ArmorStand)) return;

        UUID owner = DummyManager.getOwner(victim);
        if (owner == null) return;

        DummyData data = DummyManager.getDummy(owner);
        if (data == null) return;

        double damage = event.getDamage();
        double newHealth = data.getDummy().getHealth() - damage;
        if (newHealth <= 0) {
            DummyManager.killDummy(owner);
        } else {
            data.getDummy().setHealth(newHealth);
            DummyManager.resetDespawnTimer(owner);
        }

        if (event.getDamager() instanceof Player damager) {
            plugin.getCombatManager().getCombatTimestamps().put(damager.getUniqueId(), System.currentTimeMillis());
            plugin.getCombatManager().getCombatTimestamps().put(owner, System.currentTimeMillis());
        }
    }
}
