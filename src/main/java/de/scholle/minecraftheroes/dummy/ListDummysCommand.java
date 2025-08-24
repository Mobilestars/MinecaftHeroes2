package de.scholle.minecraftheroes.dummy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.scholle.minecraftheroes.CombatPlugin;

import java.util.Map;
import java.util.UUID;

public class ListDummysCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("heroes.admin")) {
            sender.sendMessage(CombatPlugin.getInstance().getLanguage().getMessage("dummies.no_permission"));
            return true;
        }

        Map<UUID, DummyData> dummies = DummyManager.getAllDummys();

        if (dummies.isEmpty()) {
            sender.sendMessage(CombatPlugin.getInstance().getLanguage().getMessage("dummies.none_active"));
            return true;
        }

        sender.sendMessage(CombatPlugin.getInstance().getLanguage().getMessage("dummies.header"));
        for (Map.Entry<UUID, DummyData> entry : dummies.entrySet()) {
            UUID uuid = entry.getKey();
            DummyData data = entry.getValue();
            Location loc = data.getDummy().getLocation();

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = (player != null && player.getName() != null) ? player.getName() : "Unbekannt";

            sender.sendMessage(
                    CombatPlugin.getInstance().getLanguage().getMessage("dummies.entry")
                            .replace("{name}", name)
                            .replace("{uuid}", uuid.toString())
                            .replace("{world}", loc.getWorld().getName())
                            .replace("{x}", String.valueOf(loc.getBlockX()))
                            .replace("{y}", String.valueOf(loc.getBlockY()))
                            .replace("{z}", String.valueOf(loc.getBlockZ()))
            );
        }

        return true;
    }
}
