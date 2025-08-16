package de.scholle.minecraftheroes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CombatDisplayManager {

    private final CombatPlugin plugin;
    private final CombatManager combatManager;

    private static final String FULL_HEART = "\uE000";
    private static final String EMPTY_HEART = "\uE001";

    public CombatDisplayManager(CombatPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        startDisplayTask();
    }

    private void startDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.sendActionBar(Component.empty());
                        continue;
                    }

                    UUID uuid = player.getUniqueId();

                    if (combatManager.isInCombat(uuid)) {
                        int secondsLeft = combatManager.getCombatTimeLeft(uuid);
                        TextComponent combatTimeText = Component.text()
                                .append(Component.text("Combat: ").color(NamedTextColor.RED))
                                .append(Component.text(secondsLeft + "s").color(NamedTextColor.GOLD))
                                .build();
                        player.sendActionBar(combatTimeText);
                    } else {
                        int lives = plugin.getLives(uuid);

                        if (lives == 0) {
                            player.sendActionBar(Component.empty());
                        } else {
                            StringBuilder heartsStr = new StringBuilder();
                            for (int i = 0; i < 3; i++) {
                                heartsStr.append(i < lives ? FULL_HEART : EMPTY_HEART);
                            }
                            player.sendActionBar(Component.text(heartsStr.toString()));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}