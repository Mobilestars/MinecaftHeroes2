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
        plugin.getLogger().info("[DEBUG] EnchantItemEvent triggered");

        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        plugin.getLogger().info("[DEBUG] allow-punch setting: " + allowPunch);
        if (allowPunch) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.getType().toString().contains("BOW")) return;

        if (event.getEnchantsToAdd().containsKey(Enchantment.PUNCH)) {
            plugin.getLogger().info("[DEBUG] Punch enchantment found. Blocking it.");
            event.getEnchantsToAdd().remove(Enchantment.PUNCH);
            if (event.getEnchanter() != null) {
                event.getEnchanter().sendMessage("§cDie Verzauberung Punch ist auf Bögen deaktiviert.");
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        if (allowPunch) return;

        AnvilInventory inventory = event.getInventory();
        ItemStack first = inventory.getItem(0); // Bogen
        ItemStack second = inventory.getItem(1); // Buch

        if (first == null || second == null) return;
        if (!first.getType().toString().contains("BOW")) return;

        if (second.containsEnchantment(Enchantment.PUNCH)) {
            plugin.getLogger().info("[DEBUG] Punch in Anvil detected. Blocking result.");
            event.setResult(null);
        }
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        boolean allowPunch = plugin.getConfig().getBoolean("allow-punch", false);
        if (allowPunch) return;

        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getRawSlot() != 2) return; // Ergebnis-Slot im Amboss

        ItemStack result = event.getCurrentItem();
        if (result == null) return;
        if (!result.getType().toString().contains("BOW")) return;

        if (result.containsEnchantment(Enchantment.PUNCH)) {
            plugin.getLogger().info("[DEBUG] Punch-Enchantment im Anvil-Ergebnis gefunden. Klick blockiert.");
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                plugin.sendMessage(player,"§cPunch ist auf Bögen deaktiviert.");
            }
        }
    }
}
