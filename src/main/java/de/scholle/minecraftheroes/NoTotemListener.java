package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class NoTotemListener implements Listener {

    private final CombatPlugin plugin;

    public NoTotemListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    // Verhindert, dass Totem den Spieler rettet (Totem wird deaktiviert)
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.isNoTotems()) return;

        // Prüfen ob der Schaden fatal ist
        if (player.getHealth() - event.getFinalDamage() <= 0) {

            // Prüfen, ob Totem im Offhand oder Mainhand ist
            if (player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                // Entferne das Totem (damit es nicht auslöst)
                player.getInventory().setItemInOffHand(null);
                plugin.sendMessage(player, "§cTotems sind deaktiviert, du stirbst trotzdem!");
            } else if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
                player.getInventory().setItemInMainHand(null);
                plugin.sendMessage(player, "§cTotems sind deaktiviert, du stirbst trotzdem!");
            }
        }
    }

    // Verhindert, dass Totems aufgesammelt werden können
    @EventHandler
    public void onPickup(org.bukkit.event.entity.EntityPickupItemEvent event) {
        if (!plugin.isNoTotems()) return;
        if (!(event.getEntity() instanceof Player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() == Material.TOTEM_OF_UNDYING) {
            // Totem kann nicht aufgehoben werden
            event.setCancelled(true);
        }
    }
}
