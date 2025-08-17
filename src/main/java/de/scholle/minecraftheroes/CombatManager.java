package de.scholle.minecraftheroes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;

import java.util.*;

public class CombatManager implements Listener {

    private final CombatPlugin plugin;
    private final Map<UUID, Long> combatTimestamps = new HashMap<>();
    private final Map<UUID, UUID> lastCombatOpponent = new HashMap<>();
    private final Map<UUID, UUID> activeDuels = new HashMap<>();
    private final long combatDurationMs;

    public CombatManager(CombatPlugin plugin) {
        this.plugin = plugin;
        int seconds = plugin.getConfig().getInt("combat-duration-seconds", 30);
        this.combatDurationMs = seconds * 1000L;
    }

    // Spieler kommt in den Kampfmodus
    private void enterCombat(Player a, Player b) {
        long now = System.currentTimeMillis();
        combatTimestamps.put(a.getUniqueId(), now);
        combatTimestamps.put(b.getUniqueId(), now);
        lastCombatOpponent.put(a.getUniqueId(), b.getUniqueId());
        lastCombatOpponent.put(b.getUniqueId(), a.getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;

        Player damager = null;

        if (event.getDamager() instanceof Player p) {
            damager = p;
        } else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                damager = shooter;
            }
        } else if (event.getDamager() instanceof TNTPrimed tnt) {
            if (tnt.hasMetadata("manual")) {
                String shooterId = tnt.getMetadata("manual").get(0).asString();
                damager = Bukkit.getPlayer(UUID.fromString(shooterId));
            }
        }

        if (damager != null) {
            enterCombat(damaged, damager);
        }
    }

    public void markTNTManual(TNTPrimed tnt, Player shooter) {
        tnt.setMetadata("manual", new FixedMetadataValue(plugin, shooter.getUniqueId().toString()));
    }

    // ---------------- Duel Methoden ----------------
    public boolean isInDuel(UUID uuid) {
        return activeDuels.containsKey(uuid);
    }

    public UUID getDuelOpponent(UUID uuid) {
        return activeDuels.get(uuid);
    }

    public void startDuel(Player a, Player b) {
        activeDuels.put(a.getUniqueId(), b.getUniqueId());
        activeDuels.put(b.getUniqueId(), a.getUniqueId());
        sendMessage(a, "§aDuell gestartet mit " + b.getName() + "!");
        sendMessage(b, "§c" + a.getName() + " hat ein Duell mit dir gestartet!");
    }

    public void endDuel(UUID uuid) {
        UUID opponent = activeDuels.remove(uuid);
        if (opponent != null) {
            activeDuels.remove(opponent);
            Player opp = Bukkit.getPlayer(opponent);
            if (opp != null && opp.isOnline()) {
                sendMessage(opp, "§eDas Duell wurde beendet!");
            }
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            sendMessage(player, "§eDas Duell wurde beendet!");
        }
    }

    private void sendMessage(Player player, String message) {
        plugin.sendMessage(player, message);
    }

    // ---------------- Death Handling ----------------
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();

        long now = System.currentTimeMillis();
        boolean inCombat = (now - combatTimestamps.getOrDefault(uuid, 0L)) <= combatDurationMs;
        boolean inDuel = isInDuel(uuid);

        double keepPct = inCombat ? plugin.getKeepInventoryPercentageCombat() : plugin.getKeepInventoryPercentageNatural();

        // Leben nur verlieren, wenn NICHT im Duell
        if (!inDuel) {
            if (inCombat) {
                int lives = plugin.getLives(uuid) - 1;
                if (lives <= 0) {
                    plugin.removePlayer(uuid);
                    String action = plugin.getConfig().getString("player-death-action", "spec").toLowerCase();
                    switch (action) {
                        case "ban" -> {
                            player.kick(Component.text("§cDu hast alle deine Leben verloren!"));
                            Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                                    .addBan(player.getName(), "Alle Leben verloren", null, null);
                        }
                        case "timeout" -> {
                            long timeoutSeconds = plugin.getConfig().getLong("death-timeout-seconds", 60);
                            player.setGameMode(GameMode.SPECTATOR);
                            plugin.sendMessage(player, "§cDu bist für " + timeoutSeconds + " Sekunden gesperrt.");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (player.isOnline()) {
                                        player.setGameMode(GameMode.SURVIVAL);
                                        plugin.sendMessage(player, "§aDeine Sperre ist vorbei, du bist wieder im Spiel!");
                                    }
                                }
                            }.runTaskLater(plugin, timeoutSeconds * 20L);
                        }
                        default -> {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                player.sendMessage("§4Du hast alle deine Leben verloren und bist jetzt raus.");
                                player.setGameMode(GameMode.SPECTATOR);
                            }, 2L);
                        }
                    }
                } else {
                    plugin.setLives(uuid, lives);
                    plugin.sendMessage(player,"§cDu hast ein Leben im Kampf verloren. Verbleibende Leben: " + lives);
                }
            }
        } else {
            // Duell beendet automatisch
            UUID opponentUuid = getDuelOpponent(uuid);
            endDuel(uuid);
            if (opponentUuid != null) {
                Player opponent = Bukkit.getPlayer(opponentUuid);
                if (opponent != null && opponent.isOnline()) {
                    sendMessage(opponent,"§aDu hast das Duell gewonnen, da dein Gegner gestorben ist!");
                }
            }
            sendMessage(player,"§eDu bist im Duell gestorben, daher verlierst du kein Leben.");
        }

        // Inventar behalten / Verlust berechnen
        ItemStack[] inventory = player.getInventory().getContents();
        List<Integer> occupied = new ArrayList<>();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].getType() != Material.AIR) occupied.add(i);
        }
        int keepCount = (int) Math.round(occupied.size() * keepPct);
        Collections.shuffle(occupied);
        List<Integer> keepSlots = occupied.subList(0, Math.min(keepCount, occupied.size()));
        ItemStack[] kept = new ItemStack[inventory.length];
        for (int i = 0; i < inventory.length; i++) {
            if (keepSlots.contains(i)) kept[i] = inventory[i];
        }
        for (Integer slot : keepSlots) player.getInventory().setItem(slot, null);
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().setContents(kept);
            plugin.sendMessage(player,"§6Ein Teil deines Inventars wurde behalten.");
        });

        if (inCombat) {
            combatTimestamps.remove(uuid);
            lastCombatOpponent.remove(uuid);
        }

        // Death Animation
        int frames = plugin.getConfig().getInt("death-animation-frames", 10);
        new BukkitRunnable() {
            int currentFrame = 1;
            @Override
            public void run() {
                if (currentFrame > frames) { cancel(); return; }
                String frameFile = String.format("%03d.png", currentFrame);
                currentFrame++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // ---------------- Combat Utilities ----------------
    public boolean isInCombat(UUID uuid) {
        return (System.currentTimeMillis() - combatTimestamps.getOrDefault(uuid, 0L)) <= combatDurationMs;
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

    public int getCombatTimeLeft(UUID uuid) {
        long now = System.currentTimeMillis();
        long combatTimestamp = combatTimestamps.getOrDefault(uuid, 0L);
        long elapsed = now - combatTimestamp;

        int timeLeft = (int) ((combatDurationMs - elapsed) / 1000);
        return Math.max(timeLeft, 0);
    }
}
