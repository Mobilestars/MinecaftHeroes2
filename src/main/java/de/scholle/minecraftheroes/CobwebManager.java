package de.scholle.minecraftheroes;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CobwebManager {

    private final CombatPlugin plugin;
    private final Map<UUID, Integer> placedCobwebs = new HashMap<>();
    private final int maxCobwebs;

    public CobwebManager(CombatPlugin plugin) {
        this.plugin = plugin;
        this.maxCobwebs = plugin.getConfig().getInt("max-cobwebs-per-combat", 64);
    }

    public boolean canPlaceCobweb(Player player) {
        if (!plugin.getCombatManager().isInCombat(player.getUniqueId())) return true; // außerhalb von Combat unbegrenzt
        int placed = placedCobwebs.getOrDefault(player.getUniqueId(), 0);
        return placed < maxCobwebs;
    }

    public void recordCobwebPlacement(Player player) {
        if (!plugin.getCombatManager().isInCombat(player.getUniqueId())) return;
        UUID uuid = player.getUniqueId();
        placedCobwebs.put(uuid, placedCobwebs.getOrDefault(uuid, 0) + 1);
    }

    public void resetCobwebs(UUID uuid) {
        placedCobwebs.remove(uuid);
    }

    public int getPlacedCobwebs(Player player) {
        return placedCobwebs.getOrDefault(player.getUniqueId(), 0);
    }

    public int getMaxCobwebs() {
        return maxCobwebs;
    }
}
