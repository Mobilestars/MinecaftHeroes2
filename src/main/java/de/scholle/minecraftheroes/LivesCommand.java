package de.scholle.minecraftheroes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCommand implements CommandExecutor {

    private final CombatPlugin plugin;

    public LivesCommand(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguage().getMessage("lives.only_players"));
            return true;
        }

        // Berechtigungsprüfung
        if (!player.hasPermission("heroes.lives")) {
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.no_permission"));
            return true;
        }

        if (args.length == 0) {
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.usage"));
            return false;
        }

        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("check")) {
            if (args.length < 2) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.check.usage"));
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.player_not_online")
                        .replace("%player%", targetPlayerName));
                return true;
            }

            int lives = plugin.getLives(target.getUniqueId());
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.check.result")
                    .replace("%player%", targetPlayerName)
                    .replace("%lives%", String.valueOf(lives)));
        } else if (subCommand.equals("add")) {
            if (args.length < 3) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.add.usage"));
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.player_not_online")
                        .replace("%player%", targetPlayerName));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.amount_not_number"));
                return true;
            }

            int newLives = plugin.getLives(target.getUniqueId()) + amount;
            plugin.setLives(target.getUniqueId(), newLives);
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.add.result")
                    .replace("%player%", targetPlayerName)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%lives%", String.valueOf(newLives)));
        } else if (subCommand.equals("set")) {
            if (args.length < 3) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.set.usage"));
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.player_not_online")
                        .replace("%player%", targetPlayerName));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.amount_not_number"));
                return true;
            }

            plugin.setLives(target.getUniqueId(), amount);
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.set.result")
                    .replace("%player%", targetPlayerName)
                    .replace("%lives%", String.valueOf(amount)));
        } else {
            plugin.sendMessage(player, plugin.getLanguage().getMessage("lives.invalid_subcommand"));
        }

        return true;
    }
}
