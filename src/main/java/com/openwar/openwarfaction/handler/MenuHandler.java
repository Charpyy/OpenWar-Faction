package com.openwar.openwarfaction.handler;
import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionGUI;
import com.openwar.openwarfaction.factions.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;


public class MenuHandler implements Listener {

    private final Main plugin;
    private final FactionManager factionManager;


    public MenuHandler(Main plugin, FactionManager factionManager) {
        this.factionManager = factionManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (view.getTitle().contains("§b§lFaction Menu§f - §3")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();

            if (clickedSlot == 32) {
                FactionGUI factionGUI = new FactionGUI(factionManager);
                factionGUI.openUpgradeInventory(player);
            }
            if (clickedSlot == 22) {
                FactionGUI factionGUI = new FactionGUI(factionManager);
                factionGUI.openFactionPermMenu(player);
            }

            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }

        if (view.getTitle().contains("§c§lFaction §f- §c§lUpgrade")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            int clickedSlot = event.getSlot();

            if (clickedSlot == 20) {
                UUID playerUUID = player.getUniqueId();
                Faction faction = factionManager.getFactionByPlayer(playerUUID);
                Inventory factionChest = factionManager.getFactionChest(faction);
                factionChest = fillChestWithBarriers(factionChest, faction.getLevel());
                player.openInventory(factionChest);
            }

            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
        if (view.getTitle().contains("§cFaction §f- §cChest")) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            ItemStack item = event.getClickedInventory().getItem(clickedSlot);
            if (item != null && item.getType() == Material.BARRIER) {
                event.setCancelled(true);
            }
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
    }

    public static Inventory fillChestWithBarriers(Inventory inventory, int factionLevel) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            if (currentItem != null && currentItem.getType() == Material.BARRIER) {
                inventory.setItem(i, null);
            }
        }
        int chestLevel = getChestLevel(factionLevel);
        int totalSlots = inventory.getSize();
        int emptySlots = 0;

        switch (chestLevel) {
            case 1:
                emptySlots = (int) (totalSlots * 0.15);
                break;
            case 2:
                emptySlots = (int) (totalSlots * 0.35);
                break;
            case 3:
                emptySlots = (int) (totalSlots * 0.55);
                break;
            case 4:
                emptySlots = (int) (totalSlots * 0.75);
                break;
            case 5:
            default:
                emptySlots = totalSlots;
                break;
        }

        ItemStack barrier = createBarrierBlock();
        int slotsFilled = 0;

        for (int i = 0; i < totalSlots; i++) {
            if (slotsFilled < (totalSlots - emptySlots)) {
                inventory.setItem(i, barrier);
                slotsFilled++;
            }
        }

        return inventory;
    }
    private static ItemStack createBarrierBlock() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cLocked");
            barrier.setItemMeta(meta);
        }
        return barrier;
    }

    public static int getChestLevel(int factionLevel) {
        if (factionLevel < 4) {
            return 1;
        }
        if (factionLevel < 8) {
            return 2;
        }
        if (factionLevel < 12) {
            return 3;
        }
        if (factionLevel < 14) {
            return 4;
        }
        return 5;
    }
}