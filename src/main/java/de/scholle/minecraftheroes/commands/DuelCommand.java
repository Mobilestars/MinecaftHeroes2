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

    // Duelanfragen: SpielerUUID -> GegnerUUID
    private final Map<UUID, UUID> duelRequests = new HashMap<>();

    public DuelCommand(CombatPlugin plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können Duelle starten.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("accept")) {
            // /duel accept
            UUID requesterUuid = duelRequests.remove(player.getUniqueId());
            if (requesterUuid == null) {
                player.sendMessage("§cDu hast keine Duellanfrage zum Akzeptieren.");
                return true;
            }

            Player requester = Bukkit.getPlayer(requesterUuid);
            if (requester == null || !requester.isOnline()) {
                player.sendMessage("§cDer Spieler ist nicht mehr online.");
                return true;
            }

            // Duel starten
            combatManager.startDuel(requester, player);
            return true;
        }

        if (args.length == 1) {
            // /duel <player> -> Anfrage senden
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§cSpieler nicht gefunden oder offline.");
                return true;
            }
            if (player.equals(target)) {
                player.sendMessage("§cDu kannst kein Duell mit dir selbst starten.");
                return true;
            }

            // Anfrage senden
            duelRequests.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage("§aDuellanfrage an " + target.getName() + " gesendet!");
            target.sendMessage("§e" + player.getName() + " möchte ein Duell mit dir starten. Tippe §a/duel accept§e zum Akzeptieren.");
            return true;
        }

        player.sendMessage("§cBenutzung: /duel <Spieler> | /duel accept");
        return true;
    }
}