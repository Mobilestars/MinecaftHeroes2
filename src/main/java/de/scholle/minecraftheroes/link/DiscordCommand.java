package de.scholle.minecraftheroes.link;

import de.scholle.minecraftheroes.CombatPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiscordCommand implements CommandExecutor {

    private final CombatPlugin plugin;

    public DiscordCommand(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String link = plugin.getConfig().getString("discord-link", "https://discord.gg/deinserver");

        sender.sendMessage(ChatColor.AQUA + "Discord: " + ChatColor.BLUE + link);
        return true;
    }
}
