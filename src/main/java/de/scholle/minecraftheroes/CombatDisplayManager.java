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

    private static final String HEART_1 = "\uE003";
    private static final String HEART_2 = "\uE002";
    private static final String HEART_3 = "\uE001";

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

                        String heartDisplay;
                        switch (lives) {
                            case 1 -> heartDisplay = HEART_1;
                            case 2 -> heartDisplay = HEART_2;
                            case 3 -> heartDisplay = HEART_3;
                            default -> heartDisplay = ""; // keine Herzen
                        }

                        player.sendActionBar(Component.text(heartDisplay));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
