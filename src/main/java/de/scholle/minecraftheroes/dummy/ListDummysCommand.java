package de.scholle.minecraftheroes.dummy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class ListDummysCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("heroes.admin")) {
            sender.sendMessage("§cDu hast keine Berechtigung, diesen Befehl auszuführen.");
            return true;
        }

        Map<UUID, DummyData> dummies = DummyManager.getAllDummys();

        if (dummies.isEmpty()) {
            sender.sendMessage("§cEs sind derzeit keine Dummys aktiv.");
            return true;
        }

        sender.sendMessage("§eAktive Dummys:");
        for (Map.Entry<UUID, DummyData> entry : dummies.entrySet()) {
            UUID uuid = entry.getKey();
            DummyData data = entry.getValue();
            Location loc = data.getDummy().getLocation();

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = (player != null && player.getName() != null) ? player.getName() : "Unbekannt";

            sender.sendMessage("§7" + name + " §8(" + uuid + ") §7@ " +
                    "§f" + loc.getWorld().getName() +
                    " §8x:" + loc.getBlockX() +
                    " y:" + loc.getBlockY() +
                    " z:" + loc.getBlockZ()
            );
        }

        return true;
    }
}
