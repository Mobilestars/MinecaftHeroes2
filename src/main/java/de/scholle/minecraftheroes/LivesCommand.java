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
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.sendMessage(player,"Verwendung: /lives <check|add|set> <player> [amount]");
            return false;
        }

        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("check")) {
            if (args.length < 2) {
                plugin.sendMessage(player,"Verwendung: /lives check <player>");
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player,"Spieler " + targetPlayerName + " ist nicht online oder existiert nicht.");
                return true;
            }

            int lives = plugin.getLives(target.getUniqueId());
            plugin.sendMessage(player,"Der Spieler " + targetPlayerName + " hat " + lives + " Leben.");
        } else if (subCommand.equals("add")) {
            if (args.length < 3) {
                plugin.sendMessage(player,"Verwendung: /lives add <player> <amount>");
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player,"Spieler " + targetPlayerName + " ist nicht online oder existiert nicht.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(player,"Die Menge muss eine Zahl sein.");
                return true;
            }

            int newLives = plugin.getLives(target.getUniqueId()) + amount;
            plugin.setLives(target.getUniqueId(), newLives);
            plugin.sendMessage(player,"Du hast " + amount + " Leben zu " + targetPlayerName + " hinzugefügt. Neuer Wert: " + newLives);
        } else if (subCommand.equals("set")) {
            if (args.length < 3) {
                plugin.sendMessage(player,"Verwendung: /lives set <player> <amount>");
                return false;
            }
            String targetPlayerName = args[1];
            Player target = Bukkit.getPlayerExact(targetPlayerName);

            if (target == null) {
                plugin.sendMessage(player,"Spieler " + targetPlayerName + " ist nicht online oder existiert nicht.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.sendMessage(player,"Die Menge muss eine Zahl sein.");
                return true;
            }

            plugin.setLives(target.getUniqueId(), amount);
            plugin.sendMessage(player,"Du hast die Leben von " + targetPlayerName + " auf " + amount + " gesetzt.");
        } else {
            plugin.sendMessage(player,"Ungültiger Sub-Befehl. Verfügbare Befehle: check, add, set.");
        }

        return true;
    }
}


