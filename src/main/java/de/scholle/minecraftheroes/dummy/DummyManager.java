package de.scholle.minecraftheroes.dummy;

import de.scholle.minecraftheroes.CombatPlugin;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DummyManager {

    private static final HashMap<UUID, DummyData> dummies = new HashMap<>();
    private static final CombatPlugin plugin = CombatPlugin.getInstance();
    private static final HashMap<UUID, BukkitRunnable> countdowns = new HashMap<>();

    public static void spawnDummy(Player player) {
        Location loc = player.getLocation();

        ArmorStand dummy = loc.getWorld().spawn(loc, ArmorStand.class);
        dummy.setCustomNameVisible(true);
        dummy.setCustomName("§a" + player.getName());
        dummy.setVisible(true);
        dummy.setGravity(true);
        dummy.setMarker(false);
        dummy.setSmall(false);
        dummy.setCollidable(true);
        dummy.setInvulnerable(false);
        dummy.setCanPickupItems(false);
        dummy.setBasePlate(false);
        dummy.setArms(true);

        dummy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        dummy.setHealth(player.getHealth());

        DummyData data = new DummyData(player.getUniqueId(), dummy, player.getHealth());
        dummies.put(player.getUniqueId(), data);
        startCountdown(data);
    }

    public static boolean hasDummy(UUID uuid) {
        return dummies.containsKey(uuid);
    }

    public static DummyData getDummy(UUID uuid) {
        return dummies.get(uuid);
    }

    public static void despawnDummy(UUID uuid) {
        DummyData data = dummies.remove(uuid);
        if (data != null && data.getDummy().isValid()) {
            data.getDummy().remove();
        }
        if (countdowns.containsKey(uuid)) {
            countdowns.get(uuid).cancel();
            countdowns.remove(uuid);
        }
    }

    public static void killDummy(UUID uuid) {
        DummyData data = dummies.remove(uuid);
        if (data != null && data.getDummy().isValid()) {
            data.getDummy().remove();
            CombatPlugin.getInstance().setLives(uuid, CombatPlugin.getInstance().getLives(uuid) - 1);
            CombatPlugin.getInstance().getCombatManager().resetCombat(uuid);
        }
        if (countdowns.containsKey(uuid)) {
            countdowns.get(uuid).cancel();
            countdowns.remove(uuid);
        }
    }

    private static void startCountdown(DummyData data) {
        UUID uuid = data.getOwner();
        if (countdowns.containsKey(uuid)) {
            countdowns.get(uuid).cancel();
        }

        BukkitRunnable task = new BukkitRunnable() {
            int seconds = plugin.getConfig().getInt("dummy.despawn-time");

            @Override
            public void run() {
                if (!dummies.containsKey(uuid)) {
                    cancel();
                    return;
                }

                DummyData current = dummies.get(uuid);
                if (current.getDummy().isValid()) {
                    current.getDummy().setCustomName("§eNoch " + seconds + "s");
                }

                if (seconds <= 0) {
                    despawnDummy(uuid);
                    cancel();
                }

                seconds--;
            }
        };

        countdowns.put(uuid, task);
        task.runTaskTimer(plugin, 0L, 20L);
    }

    public static void resetDespawnTimer(UUID uuid) {
        DummyData data = dummies.get(uuid);
        if (data == null) return;
        startCountdown(data);
    }

    public static void onPlayerJoin(UUID uuid, Player player) {
        if (!dummies.containsKey(uuid)) return;

        DummyData data = dummies.get(uuid);
        double lost = data.getInitialHealth() - data.getDummy().getHealth();
        double newHealth = Math.max(1, player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - lost);
        player.setHealth(newHealth);

        despawnDummy(uuid);
    }

    public static UUID getOwner(Entity entity) {
        for (DummyData data : dummies.values()) {
            if (data.getDummy().getUniqueId().equals(entity.getUniqueId())) return data.getOwner();
        }

        return null;
    }

    public static Map<UUID, DummyData> getAllDummys() {
        return new HashMap<>(dummies);
    }
}
