package com.openwar.openwarfaction.handler;
import com.openwar.openwarfaction.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class MenuHandler implements Listener {

    private final Main plugin;

    public MenuHandler(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        if (view.getTitle().contains("§b§lFaction Menu§f - §3")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
    }
}