package com.openwar.openwarfaction.handler;
import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.FactionGUI;
import com.openwar.openwarfaction.factions.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;


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
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
        if (view.getTitle().contains("§a§lUpgrade Menu")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
    }
}