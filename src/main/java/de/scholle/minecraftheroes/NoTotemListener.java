package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class NoTotemListener implements Listener {

    private final CombatPlugin plugin;

    public NoTotemListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.isNoTotems()) return;

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            if (player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                player.getInventory().setItemInOffHand(null);
                plugin.sendMessage(player, plugin.getLanguage().getMessage("nototem.disabled"));
            } else if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
                player.getInventory().setItemInMainHand(null);
                plugin.sendMessage(player, plugin.getLanguage().getMessage("nototem.disabled"));
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!plugin.isNoTotems()) return;
        if (!(event.getEntity() instanceof Player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() == Material.TOTEM_OF_UNDYING) {
            event.setCancelled(true);
        }
    }
}
