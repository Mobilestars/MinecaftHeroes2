package de.scholle.minecraftheroes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class PunchEnchantBlocker implements Listener {

    private final CombatPlugin plugin;

    public PunchEnchantBlocker(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        if (allowPunch) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.getType().toString().contains("BOW")) return;

        if (event.getEnchantsToAdd().containsKey(Enchantment.PUNCH)) {
            event.getEnchantsToAdd().remove(Enchantment.PUNCH);
            if (event.getEnchanter() != null) {
                event.getEnchanter().sendMessage(plugin.getLanguage().getMessage("punch.disabled"));
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        if (allowPunch) return;

        AnvilInventory inventory = event.getInventory();
        ItemStack first = inventory.getItem(0);
        ItemStack second = inventory.getItem(1);

        if (first == null || second == null) return;
        if (!first.getType().toString().contains("BOW")) return;

        if (second.containsEnchantment(Enchantment.PUNCH)) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        if (allowPunch) return;

        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getRawSlot() != 2) return;

        ItemStack result = event.getCurrentItem();
        if (result == null) return;
        if (!result.getType().toString().contains("BOW")) return;

        if (result.containsEnchantment(Enchantment.PUNCH)) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                plugin.sendMessage(player, plugin.getLanguage().getMessage("punch.disabled"));
            }
        }
    }
}
