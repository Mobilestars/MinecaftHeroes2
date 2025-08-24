package de.scholle.minecraftheroes;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderPearlManager {

    private final CombatPlugin plugin;
    private final Map<UUID, Integer> usedPearls = new HashMap<>();
    private final int maxPearls;

    public EnderPearlManager(CombatPlugin plugin) {
        this.plugin = plugin;
        this.maxPearls = plugin.getConfig().getInt("max-enderpearls-per-combat", 32);
    }

    public boolean canUseEnderPearl(Player player) {
        if (!plugin.getCombatManager().isInCombat(player.getUniqueId())) return true;
        int used = usedPearls.getOrDefault(player.getUniqueId(), 0);
        return used < maxPearls;
    }

    public void recordEnderPearlUse(Player player) {
        if (!plugin.getCombatManager().isInCombat(player.getUniqueId())) return;
        UUID uuid = player.getUniqueId();
        usedPearls.put(uuid, usedPearls.getOrDefault(uuid, 0) + 1);
    }

    public void resetEnderPearls(UUID uuid) {
        usedPearls.remove(uuid);
    }

    public int getUsedPearls(Player player) {
        return usedPearls.getOrDefault(player.getUniqueId(), 0);
    }

    public int getMaxPearls() {
        return maxPearls;
    }
}
