package de.scholle.minecraftheroes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CombatManager implements Listener {

    private final CombatPlugin plugin;
    private final Map<UUID, Long> combatTimestamps = new HashMap<>();
    private final Map<UUID, UUID> lastCombatOpponent = new HashMap<>();

    public CombatManager(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        long now = System.currentTimeMillis();
        combatTimestamps.put(damaged.getUniqueId(), now);
        combatTimestamps.put(damager.getUniqueId(), now);
        lastCombatOpponent.put(damaged.getUniqueId(), damager.getUniqueId());
        lastCombatOpponent.put(damager.getUniqueId(), damaged.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();

        long now = System.currentTimeMillis();
        boolean inCombat = (now - combatTimestamps.getOrDefault(uuid, 0L)) <= 30000;

        double keepPct = inCombat ? plugin.getKeepInventoryPercentageCombat() : plugin.getKeepInventoryPercentageNatural();

        if (!inCombat) {
            plugin.sendMessage(player,"§aDu bist außerhalb des Kampfes gestorben. Kein Leben verloren.");
        } else {
            int lives = plugin.getLives(uuid) - 1;
            if (lives <= 0) {
                plugin.removePlayer(uuid);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage("§4Du hast alle deine Leben verloren und bist jetzt raus.");
                    player.setGameMode(GameMode.SPECTATOR);
                }, 2L);
            } else {
                plugin.setLives(uuid, lives);
                plugin.sendMessage(player,"§cDu hast ein Leben im Kampf verloren. Verbleibende Leben: " + lives);
            }
        }

        ItemStack[] inventory = player.getInventory().getContents();
        List<Integer> occupied = new ArrayList<>();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].getType() != Material.AIR) {
                occupied.add(i);
            }
        }

        int keepCount = (int) Math.round(occupied.size() * keepPct);
        Collections.shuffle(occupied);
        List<Integer> keepSlots = occupied.subList(0, Math.min(keepCount, occupied.size()));

        // Items, die behalten werden
        ItemStack[] kept = new ItemStack[inventory.length];
        for (int i = 0; i < inventory.length; i++) {
            if (keepSlots.contains(i)) {
                kept[i] = inventory[i];
            }
        }

        // Temporär entfernen der behaltenen Items aus Inventar, damit GravesX sie nicht als Drop erkennt
        for (Integer slot : keepSlots) {
            player.getInventory().setItem(slot, null);
        }

        // Drops bleiben unverändert, GravesX sieht nur noch die nicht-behaltenen Items
        // Keine Änderung an event.getDrops()!

        // Inventar nach kurzem Tick wiederherstellen
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().setContents(kept);
            plugin.sendMessage(player,"§6Ein Teil deines Inventars wurde behalten.");
        });

        if (inCombat) {
            combatTimestamps.remove(uuid);
            lastCombatOpponent.remove(uuid);
        }
    }

    public boolean isInCombat(UUID uuid) {
        return (System.currentTimeMillis() - combatTimestamps.getOrDefault(uuid, 0L)) <= 30000;
    }

    public UUID getOpponent(UUID uuid) {
        return lastCombatOpponent.get(uuid);
    }

    public void resetCombat(UUID uuid) {
        combatTimestamps.remove(uuid);
        lastCombatOpponent.remove(uuid);
    }

    public Map<UUID, Long> getCombatTimestamps() {
        return combatTimestamps;
    }

    // Gibt verbleibende Kampfzeit in Sekunden zurück
    public int getCombatTimeLeft(UUID uuid) {
        long now = System.currentTimeMillis();
        long combatTimestamp = combatTimestamps.getOrDefault(uuid, 0L);
        long elapsed = now - combatTimestamp;
        int totalCombatDuration = 30000; // 30 Sekunden Kampfzeit

        int timeLeft = (int) ((totalCombatDuration - elapsed) / 1000);
        return Math.max(timeLeft, 0);
    }
}
