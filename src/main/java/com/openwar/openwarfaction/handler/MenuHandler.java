package com.openwar.openwarfaction.handler;
import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class MenuHandler implements Listener {

    private final Main plugin;
    private final FactionManager factionManager;
    private final Economy economy;
    private final FactionGUI factionGUI;

    public MenuHandler(Main plugin, FactionManager factionManager, Economy economy, FactionGUI factionGUI) {
        this.factionManager = factionManager;
        this.plugin = plugin;
        this.economy = economy;
        this.factionGUI = factionGUI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();

        if (view.getTitle().contains("§7§k§l!!§r §c=== §8§l⟦§4§l")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();

            if (clickedSlot == 32) {
                factionGUI.openUpgradeInventory(player);
            }
            if (clickedSlot == 22) {
                factionGUI.openFactionPermMenu(player);
            }

            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }

        if (view.getTitle().contains("§c§lFaction §f- §c§lUpgrade")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            int clickedSlot = event.getSlot();
            UUID playerUUID = player.getUniqueId();
            Faction faction = factionManager.getFactionByPlayer(playerUUID);
            if (clickedSlot == 20) {
                if (factionManager.hasPermissionInFaction(playerUUID, faction, Permission.FACCHEST)) {
                    Inventory factionChest = factionManager.getFactionChest(faction);
                    factionChest = fillChestWithBarriers(factionChest, faction.getLevel());
                    player.openInventory(factionChest);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }
            if (clickedSlot == 22){
                if (factionManager.hasPermissionInFaction(playerUUID, faction, Permission.FACSHOP)) {
                    factionGUI.openFactionShopMain(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }

            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }
        if (view.getTitle().contains("§cFaction §f- §cChest")) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            if (clickedSlot < 0) {
                return;
            }
            ItemStack item = event.getClickedInventory().getItem(clickedSlot);
            if (item != null && item.getType() == Material.BARRIER) {
                event.setCancelled(true);
            }
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }


        if (view.getTitle().contains("§b§lFaction Perms §f- §3Page 1")) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            if (clickedSlot < 0) {
                return;
            }

            ItemStack item = event.getClickedInventory().getItem(clickedSlot);
            if (item == null || item.getType() != Material.STAINED_GLASS_PANE || item.getType() == Material.PAPER || item.getItemMeta().getDisplayName().equals(" ")) {
                event.setCancelled(true);
                return;
            }

            if (item.getType() == Material.STAINED_GLASS_PANE) {
                if (clickedSlot == 8) {
                    factionGUI.openFactionPermPage2(player);
                    event.setCancelled(true);
                    return;
                }

                Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
                Permission perm = null;
                PermRank rank = null;

                int col = clickedSlot / 9;
                int row = clickedSlot % 9;

                if (row >= 2 && row <= 8 && col >= 1 && col < 8) {
                    perm = Permission.values()[row - 2];
                    rank = PermRank.values()[col - 1];


                    boolean hasPerm = faction.hasPermission(rank, perm);
                    faction.setPermission(rank, perm, !hasPerm);

                    ItemStack newPermPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (hasPerm ? 14 : 5));
                    ItemMeta newMeta = newPermPane.getItemMeta();
                    newMeta.setDisplayName(hasPerm ? "§4NO" : "§aYES");
                    newPermPane.setItemMeta(newMeta);

                    event.getClickedInventory().setItem(clickedSlot, newPermPane);
                }

                event.setCancelled(true);
                Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
            }
        }

        if (view.getTitle().contains("§b§lFaction Perms §f- §3Page 2")) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            if (clickedSlot < 0) {
                return;
            }

            ItemStack item = event.getClickedInventory().getItem(clickedSlot);
            if (item == null || item.getType() != Material.STAINED_GLASS_PANE || item.getType() == Material.PAPER || item.getItemMeta().getDisplayName().equals(" ")) {
                event.setCancelled(true);
                return;
            }

            if (clickedSlot == 0) {
                factionGUI.openFactionPermMenu(player);
                event.setCancelled(true);
                return;
            }
            if (clickedSlot == 8) {
                factionGUI.openFactionPermPage3(player);
                event.setCancelled(true);
                return;
            }

            Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
            Permission perm = null;
            PermRank rank = null;


            int row = clickedSlot % 9;
            int col = clickedSlot / 9;

            if (row >= 2 && row <= 7 && col >= 1 && col <= 6) {
                perm = Permission.values()[row - 2 + 6];
                rank = PermRank.values()[col - 1];

                boolean hasPerm = faction.hasPermission(rank, perm);
                faction.setPermission(rank, perm, !hasPerm);

                ItemStack newPermPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (hasPerm ? 14 : 5));
                ItemMeta newMeta = newPermPane.getItemMeta();
                newMeta.setDisplayName(hasPerm ? "§4NO" : "§aYES");
                newPermPane.setItemMeta(newMeta);

                event.getClickedInventory().setItem(clickedSlot, newPermPane);
            }

            event.setCancelled(true);
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }

        if (view.getTitle().contains("§b§lFaction Perms §f- §3Page 3")) {
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            if (clickedSlot < 0) {
                return;
            }

            ItemStack item = event.getClickedInventory().getItem(clickedSlot);
            if (item == null || item.getType() != Material.STAINED_GLASS_PANE || item.getType() == Material.PAPER || item.getItemMeta().getDisplayName().equals(" ")) {
                event.setCancelled(true);
                return;
            }

            if (clickedSlot == 0) {
                factionGUI.openFactionPermPage2(player);
                event.setCancelled(true);
                return;
            }

            Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
            Permission perm = null;
            PermRank rank = null;


            int row = clickedSlot % 9;
            int col = clickedSlot / 9;

            if (row >= 2 && row <= 7 && col >= 1 && col <= 6) {
                perm = Permission.values()[row - 2 + 12];
                rank = PermRank.values()[col - 1];

                boolean hasPerm = faction.hasPermission(rank, perm);
                faction.setPermission(rank, perm, !hasPerm);

                ItemStack newPermPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (hasPerm ? 14 : 5));
                ItemMeta newMeta = newPermPane.getItemMeta();
                newMeta.setDisplayName(hasPerm ? "§4NO" : "§aYES");
                newPermPane.setItemMeta(newMeta);

                event.getClickedInventory().setItem(clickedSlot, newPermPane);
            }

            event.setCancelled(true);
            Bukkit.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
        }


        if (view.getTitle().contains("§b§lFaction Shops")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
            int level = faction.getLevel();
            if (clickedSlot < 0) {
                return;
            }
            if (clickedSlot == 12) {
                if (level >= 3) {
                    factionGUI.openFactionShopMCHELI(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }
        }
        if (view.getTitle().contains("§k§l!!!§r§3§l MCHELI SHOP §8§r§8§k§l!!!")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();
            if (clickedSlot < 0) {
                return;
            }
            Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
            int factionLevel = faction.getLevel();
            int shoplevel = 0;
            if (factionLevel < 6) {
                shoplevel = 1;
            } else if (factionLevel < 8) {
                shoplevel = 2;
            } else if (factionLevel < 10) {
                shoplevel = 3;
            } else if (factionLevel < 12) {
                shoplevel = 4;
            } else {
                shoplevel = 5;
            }
            if (clickedSlot == 15) {
                factionGUI.openFacShop(player, "antiair", "Anti - Air", shoplevel);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else if (clickedSlot == 13) {
                factionGUI.openFacShop(player, "heli", "Helicopter", shoplevel);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else if (clickedSlot == 11) {
                factionGUI.openFacShop(player, "plane", "Plane", shoplevel);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else if (clickedSlot == 21) {
                factionGUI.openFacShop(player, "tank", "Tank", shoplevel);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else if (clickedSlot == 23) {
                factionGUI.openFacShop(player, "bato", "Boat", shoplevel);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }

        if (view.getTitle().contains("§8§l⟪ §b")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getClickedInventory().getItem(event.getSlot());
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta.getDisplayName().contains("§8» §cBack")) {
                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    openFactionShopMainMenu(player);
                } else if (itemMeta.getDisplayName().contains("§8§l⟦ §b")) {
                    Player player = (Player) event.getWhoClicked();
                    List<String> lore = itemMeta.getLore();
                    if (lore != null) {
                        String[] priceSplit = lore.get(0).split("\\$");
                        int price = Integer.parseInt(priceSplit[1]);
                        double playerBalance = economy.getBalance(player);
                        if (playerBalance >= price) {
                            economy.withdrawPlayer(player, price);
                            ItemStack give = new ItemStack(clickedItem.getType());
                            if (player.getInventory().firstEmpty() != -1) {
                                player.getInventory().addItem(give);
                            } else {
                                player.getWorld().dropItemNaturally(player.getLocation(), give);
                            }
                            player.sendMessage("§8§l⟦§bFac-Shop§8§l⟧ §7You have purchased §f1 §8" + itemMeta.getDisplayName() + " §7for §6$" + price + " §7!");
                            player.closeInventory();
                        } else {
                            player.sendMessage("§8§l⟦§bFac-Shop§8§l⟧ §cYou do not have enough money!");
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }

    //fonction pour généré des inventaire dynamique en fonction de la catégorie et du niveau de shop



    private void openFactionShopMainMenu(Player player) {
        factionGUI.openFactionShopMain(player);
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