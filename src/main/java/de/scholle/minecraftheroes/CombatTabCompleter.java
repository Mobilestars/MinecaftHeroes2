package de.scholle.minecraftheroes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CombatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        if (args.length == 1) {
            switch (cmd) {
                case "lives":
                    completions.add("check");
                    completions.add("add");
                    completions.add("set");
                    break;

                case "linkheart":
                    completions.add("generate");
                    break;

                case "heart":
                    for (Player p : player.getServer().getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                    break;

                case "glowing":
                    completions.add("enable");
                    completions.add("disable");
                    break;

                case "nextglowing":
                    // Kein Argument – keine Completion nötig
                    break;
            }
        } else if (args.length == 2) {
            if (cmd.equals("lives") || cmd.equals("heart")) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3) {
            if (cmd.equals("lives") &&
                    (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set"))) {
                completions.add("<amount>");
            }
        }

        return completions;
    }
}
