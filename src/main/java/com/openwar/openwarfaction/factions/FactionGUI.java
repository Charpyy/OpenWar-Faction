package com.openwar.openwarfaction.factions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FactionGUI {

    private final FactionManager factionManager;

    public FactionGUI(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public void openFactionMenu(Player player) {
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Menu§f - §3" + faction.getName());
        setMenuBackground(menu);

        ItemStack factionLevelItem = createFactionLevelItem(faction);
        ItemStack infoItem = createFactionInfoItem(faction);
        ItemStack upgradeItem = createUpgradeItem();
        ItemStack leaderHead = getLeaderHead(Bukkit.getOfflinePlayer(faction.getLeaderUUID()).getName());

        menu.setItem(24, factionLevelItem);
        menu.setItem(30, infoItem);
        menu.setItem(32, upgradeItem);
        menu.setItem(20, leaderHead);

        player.openInventory(menu);
    }

    private void setMenuBackground(Inventory menu) {
        ItemStack glassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 15, " ");
        for (int i = 0; i < 54; i++) {
            menu.setItem(i, glassPane);
        }

        ItemStack borderGlassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 0, " ");
        for (int i = 0; i < 54; i++) {
            if (isBorderSlot(i)) {
                menu.setItem(i, borderGlassPane);
            }
        }
    }

    private ItemStack createColoredGlassPane(Material material, short data, String name) {
        ItemStack glassPane = new ItemStack(material, 1, data);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName(name);
        glassPane.setItemMeta(meta);
        return glassPane;
    }

    private ItemStack createFactionLevelItem(Faction faction) {
        ItemStack factionLevelItem = new ItemStack(Material.EXP_BOTTLE);
        ItemMeta meta = factionLevelItem.getItemMeta();

        meta.setDisplayName("§6§lFaction Level");
        List<String> lore = new ArrayList<>();
        int factionLevel = faction.getLevel();
        int factionExp = faction.getExp();
        int expRequired = faction.getExperienceNeededForNextLevel();
        lore.add("§eLevel: §6" + factionLevel);
        lore.add("§eExperience: §6" + factionExp + "§e / §6" + expRequired);

        double percentage = (double) factionExp / expRequired * 100;
        int progress = (int) ((percentage / 100) * 27);
        lore.add("§eProgression: " + getProgressBar(progress, 27) + " §6" + String.format("%.2f", percentage) + "%");

        meta.setLore(lore);
        factionLevelItem.setItemMeta(meta);
        return factionLevelItem;
    }

    private ItemStack createFactionInfoItem(Faction faction) {
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta meta = infoItem.getItemMeta();

        meta.setDisplayName("§8§lInformations");
        List<String> lore = new ArrayList<>();
        lore.add("§7Name: §f" + faction.getName());
        lore.add("§7Members: §f" + faction.getMembers().size());

        if (faction.getHomeLocation() != null) {
            lore.add("§7Home Location: \u00A78X: \u00A77" + (int) faction.getHomeLocation().getX() + " \u00A78Y: \u00A77" + (int) faction.getHomeLocation().getY() + " \u00A78Z: \u00A77" + (int) faction.getHomeLocation().getZ());
        } else {
            lore.add("§7Home Location: \u00A7fNot Set.");
        }

        meta.setLore(lore);
        infoItem.setItemMeta(meta);
        return infoItem;
    }

    private ItemStack createUpgradeItem() {
        ItemStack upgrade = new ItemStack(Material.WHITE_SHULKER_BOX);
        setItemLore(upgrade, "§c§lFaction Upgrade", "§7Click here to open", null);
        return upgrade;
    }

    public ItemStack getLeaderHead(String leaderName) {
        ItemStack leaderHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) leaderHead.getItemMeta();

        if (meta != null) {
            OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderName);
            meta.setOwningPlayer(leader);
            meta.setDisplayName("§4§lFaction Leader");
            meta.setLore(Arrays.asList("§c" + leaderName));
            leaderHead.setItemMeta(meta);
        }

        return leaderHead;
    }

    public void setItemLore(ItemStack item, String name, String firstLine, String secondLine) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (secondLine != null) {
            meta.setLore(Arrays.asList(firstLine, secondLine));
        } else {
            meta.setLore(Arrays.asList(firstLine));
        }
        item.setItemMeta(meta);
    }

    public void openUpgradeInventory(Player player) {
        Inventory factionLevelMenu = Bukkit.createInventory(null, 54, "§c§lFaction §f- §c§lUpgrade");
        ItemStack blackStainedGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta blackMeta = blackStainedGlassPane.getItemMeta();
        blackMeta.setDisplayName(" ");
        blackStainedGlassPane.setItemMeta(blackMeta);
        ItemStack whiteStainedGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta whiteMeta = whiteStainedGlassPane.getItemMeta();
        whiteMeta.setDisplayName(" ");
        whiteStainedGlassPane.setItemMeta(whiteMeta);

        for (int i = 0; i < 54; i++) {
            if (isBorderSlot(i)) {
                factionLevelMenu.setItem(i, blackStainedGlassPane);
            } else {
                factionLevelMenu.setItem(i, whiteStainedGlassPane);
            }
        }

        Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
        int factionLevel = faction.getLevel();

        //TODO refaire ce putain de gui de merde.
       //ItemStack factionChest = createCustomItem(Material.CHEST, "§f§lFaction Chest", getLoreForItem(factionLevel, "Faction Chest", 1));
       //ItemStack factionShop = createCustomItem(Material.GRAY_SHULKER_BOX, "§8§lShop Faction", getLoreForItem(factionLevel, "Shop Faction", 2));
       //ItemStack xpBoost = createCustomItem(Material.DRAGONS_BREATH, "§5§lXP Boost", getLoreForItem(factionLevel, "XP Boost", 3));
       //ItemStack factionClaims = createCustomItem(Material.GRASS, "§2§lClaims", getLoreForItem(factionLevel, "Claims", 4));
       //ItemStack notificationPaper = createCustomItem(Material.WHEAT, "§e§lFaction Farm", getLoreForItem(factionLevel, "Faction Farm", 5));

        //factionLevelMenu.setItem(20, factionChest);
        //factionLevelMenu.setItem(22, factionShop);
        //factionLevelMenu.setItem(24, xpBoost);
        //factionLevelMenu.setItem(30, factionClaims);
        //factionLevelMenu.setItem(32, notificationPaper);

        player.openInventory(factionLevelMenu);
    }

    private String getLoreForItem(int factionLevel, String itemName, int requiredLevel) {
        if (factionLevel < requiredLevel) {
            return "§cNiveau de faction insuffisant ! Nécessite le niveau §e" + requiredLevel;
        }
        return "§aAvaible !";
    }

    private static boolean isBorderSlot(int slot) {
        return slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8;
    }

    private static ItemStack createCustomItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private String getProgressBar(int progress, int total) {
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < total; i++) {
            if (i < progress) {
                bar.append("§a█");
            } else {
                bar.append("§7█");
            }
        }
        return bar.toString();
    }
}
