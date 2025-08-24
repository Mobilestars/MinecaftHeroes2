package de.scholle.minecraftheroes;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NoNetheriteListener implements Listener {

    private final CombatPlugin plugin;

    public NoNetheriteListener(CombatPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isNetheriteArmor(Material material) {
        return material == Material.NETHERITE_HELMET
                || material == Material.NETHERITE_CHESTPLATE
                || material == Material.NETHERITE_LEGGINGS
                || material == Material.NETHERITE_BOOTS;
    }

    private boolean isNetheriteMaterial(Material material) {
        return material == Material.NETHERITE_SWORD
                || material == Material.NETHERITE_AXE
                || material == Material.NETHERITE_PICKAXE
                || material == Material.NETHERITE_SHOVEL
                || material == Material.NETHERITE_HOE
                || isNetheriteArmor(material);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.isNoNetherite()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot >= 5 && rawSlot <= 8) {
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            if ((cursorItem != null && isNetheriteArmor(cursorItem.getType())) ||
                    (currentItem != null && isNetheriteArmor(currentItem.getType()))) {
                event.setCancelled(true);
                plugin.sendMessage(player, plugin.getLanguage().getMessage("nonetherite.armor.place"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.isNoNetherite()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && isNetheriteArmor(item.getType()) && event.getHand() == EquipmentSlot.HAND) {
            event.setCancelled(true);
            plugin.sendMessage(player, plugin.getLanguage().getMessage("nonetherite.armor.equip"));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.isNoNetherite()) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && isNetheriteMaterial(item.getType())) {
            event.setCancelled(true);
            plugin.sendMessage(player, plugin.getLanguage().getMessage("nonetherite.tool.use"));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!plugin.isNoNetherite()) return;
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && isNetheriteMaterial(item.getType())) {
            event.setCancelled(true);
            plugin.sendMessage(player, plugin.getLanguage().getMessage("nonetherite.weapon.use"));
        }
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (!plugin.isNoNetherite()) return;

        Entity target = event.getTargetEntity();
        if (target instanceof Player player) {
            ItemStack armor = event.getItem();
            if (armor != null && isNetheriteArmor(armor.getType())) {
                event.setCancelled(true);
                plugin.sendMessage(player, plugin.getLanguage().getMessage("nonetherite.armor.dispenser"));
            }
        }
    }
}
