package de.scholle.minecraftheroes.commands;

import de.scholle.minecraftheroes.CombatManager;
import de.scholle.minecraftheroes.CombatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelCommand implements CommandExecutor {

    private final CombatPlugin plugin;
    private final CombatManager combatManager;
    private final Map<UUID, UUID> duelRequests = new HashMap<>();

    public DuelCommand(CombatPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguage().getMessage("duel.only_players"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("accept")) {
            UUID requesterUuid = duelRequests.remove(player.getUniqueId());
            if (requesterUuid == null) {
                player.sendMessage(plugin.getLanguage().getMessage("duel.no_request"));
                return true;
            }

            Player requester = Bukkit.getPlayer(requesterUuid);
            if (requester == null || !requester.isOnline()) {
                player.sendMessage(plugin.getLanguage().getMessage("duel.player_offline"));
                return true;
            }

            combatManager.startDuel(requester, player);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(plugin.getLanguage().getMessage("duel.target_not_found"));
                return true;
            }
            if (player.equals(target)) {
                player.sendMessage(plugin.getLanguage().getMessage("duel.self_duel"));
                return true;
            }

            duelRequests.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(plugin.getLanguage().getMessage("duel.request_sent").replace("{target}", target.getName()));
            target.sendMessage(plugin.getLanguage().getMessage("duel.request_receive").replace("{player}", player.getName()));
            return true;
        }

        player.sendMessage(plugin.getLanguage().getMessage("duel.usage"));
        return true;
    }
}
