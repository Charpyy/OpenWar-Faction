package com.openwar.openwarfaction.factions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class FactionGUI {

    private final FactionManager factionManager;

    public FactionGUI(FactionManager factionManager) {
        this.factionManager = factionManager;
    }


    private void setMenuBackground(Inventory menu) {
        ItemStack glassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 0, " ");
        for (int i = 0; i < 54; i++) {
            menu.setItem(i, glassPane);
        }

        ItemStack borderGlassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 15, " ");
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
        lore.add("§eProgression: " + getProgressBar(progress, 10) + " §6" + String.format("%.2f", percentage) + "%");

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
        lore.add("§7Members Online: §f" + faction.getOnlineMembers().size() +"§7/§f"+ faction.getMembers().size());
        FactionManager fm = new FactionManager();
        lore.add("§7Claims: §f"+fm.getClaimedChunks(faction).size());
        if (faction.getHomeLocation() != null) {
            lore.add("§7Home Location: \u00A78X: \u00A77" + (int) faction.getHomeLocation().getX() + " \u00A78Y: \u00A77" + (int) faction.getHomeLocation().getY() + " \u00A78Z: \u00A77" + (int) faction.getHomeLocation().getZ());
        } else {
            lore.add("§7Home Location: \u00A7fNot Set.");
        }
        lore.add("§7Members Name: ");
        for (Map.Entry<UUID, Rank> entry : faction.getMembers().entrySet()) {
            UUID memberUUID = entry.getKey();
            Player member = Bukkit.getPlayer(memberUUID);
            lore.add(" §7- §b"+member.getName());
        }

        meta.setLore(lore);
        infoItem.setItemMeta(meta);
        return infoItem;
    }
    private ItemStack createFpermItem() {
        ItemStack fperm = new ItemStack(Material.ANVIL);
        setItemLore(fperm, "§3§lFaction Perms", "§7Click here to open", null);
        return fperm;
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


    private static boolean isBorderSlot(int slot) {
        return slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8;
    }

    private ItemStack createCustomItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

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

    //========================================================= MAIN MENU =====================================
    public void openFactionMenu(Player player) {
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Menu§f - §3" + faction.getName());
        setMenuBackground(menu);

        ItemStack factionLevelItem = createFactionLevelItem(faction);
        ItemStack infoItem = createFactionInfoItem(faction);
        ItemStack upgradeItem = createUpgradeItem();
        ItemStack leaderHead = getLeaderHead(Bukkit.getOfflinePlayer(faction.getLeaderUUID()).getName());
        ItemStack fperm = createFpermItem();

        menu.setItem(24, factionLevelItem);
        menu.setItem(30, infoItem);
        menu.setItem(32, upgradeItem);
        menu.setItem(20, leaderHead);
        menu.setItem(22, fperm);

        player.openInventory(menu);
    }

    //============================================================ UPGRADE MENU =========================
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

        ItemStack factionChest = createCustomItem(Material.CHEST, "§f§lFaction Chest", getLoreForItem(factionLevel, "chest"));
        ItemStack factionShop = createCustomItem(Material.GRAY_SHULKER_BOX, "§8§lShop Faction", getLoreForItem(factionLevel, "shop"));
        ItemStack xpBoost = createCustomItem(Material.DRAGONS_BREATH, "§5§lXP Boost", getLoreForItem(factionLevel, "xp"));
        ItemStack factionClaims = createCustomItem(Material.GRASS, "§2§lClaims", getLoreForItem(factionLevel, "claims"));
        ItemStack farmpaper = createCustomItem(Material.WHEAT, "§e§lFaction Farm", getLoreForItem(factionLevel, "farm"));

        factionLevelMenu.setItem(20, factionChest);
        factionLevelMenu.setItem(22, factionShop);
        factionLevelMenu.setItem(24, xpBoost);
        factionLevelMenu.setItem(30, factionClaims);
        factionLevelMenu.setItem(32, farmpaper);

        player.openInventory(factionLevelMenu);
    }
    //=============================================== FPERMS MENU =======================
    public void openFactionPermMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Perms §f- §3Page 1");
        setMenuBackground(menu);
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);

        int startCol = 2;
        for (int i = 0; i < Permission.values().length; i++) {
            if (startCol >= 8) break;

            Permission perm = Permission.values()[i];

            ItemStack permInfo = new ItemStack(Material.PAPER);
            ItemMeta permMeta = permInfo.getItemMeta();
            permMeta.setDisplayName("§r§8" + perm.name());
            permInfo.setItemMeta(permMeta);
            menu.setItem(startCol, permInfo);

            for (int j = 0; j < PermRank.values().length; j++) {
                PermRank rank = PermRank.values()[j];
                boolean hasPerm = faction.hasPermission(rank, perm);

                ItemStack permPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (hasPerm ? 5 : 14));
                ItemMeta meta = permPane.getItemMeta();
                meta.setDisplayName(hasPerm ? "§aYES" : "§4NO");
                permPane.setItemMeta(meta);

                int index = (j + 1) * 9 + startCol;
                if (index < 54) {
                    menu.setItem(index, permPane);
                }
            }

            startCol++;
        }

        int slot = 9;
        for (PermRank rank : PermRank.values()) {
            ItemStack rankPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
            ItemMeta meta = rankPane.getItemMeta();
            meta.setDisplayName("§3"+rank.getAbr());
            rankPane.setItemMeta(meta);
            menu.setItem(slot, rankPane);
            slot += 9;
        }

        int gray = 10;
        for (int i = 0; i < 6; i++) {
            ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta meta = grayPane.getItemMeta();
            meta.setDisplayName("§8->");
            grayPane.setItemMeta(meta);
            if (gray < 54) {
                menu.setItem(gray, grayPane);
            }
            gray += 9;
        }
        // Next page
        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        ItemMeta meta = limePane.getItemMeta();
        meta.setDisplayName("§8» §aNext Page");
        limePane.setItemMeta(meta);
        menu.setItem(0, limePane);
        gray = 17;
        for (int i = 0; i < 6; i++) {
            ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta metaa = grayPane.getItemMeta();
            metaa.setDisplayName(" ");
            grayPane.setItemMeta(metaa);
            if (gray < 54) {
                menu.setItem(gray, grayPane);
            }
            gray += 9;
        }
        ItemStack black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta metaData = black.getItemMeta();
        metaData.setDisplayName(" ");
        black.setItemMeta(metaData);
        menu.setItem(8, black);

        player.openInventory(menu);
    }

    public void openFactionPermPage2(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Perms §f- §3Page 2");
        setMenuBackground(menu);
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);

        int startCol = 2;
        for (int i = 6; i < Permission.values().length; i++) {
            if (startCol >= 9) break;

            Permission perm = Permission.values()[i];

            ItemStack permInfo = new ItemStack(Material.PAPER);
            ItemMeta permMeta = permInfo.getItemMeta();
            permMeta.setDisplayName("§r§8" + perm.name());
            permInfo.setItemMeta(permMeta);
            menu.setItem(startCol, permInfo);

            for (int j = 0; j < PermRank.values().length; j++) {
                PermRank rank = PermRank.values()[j];
                boolean hasPerm = faction.hasPermission(rank, perm);

                ItemStack permPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) (hasPerm ? 5 : 14));
                ItemMeta meta = permPane.getItemMeta();
                meta.setDisplayName(hasPerm ? "§aYES" : "§4NO");
                permPane.setItemMeta(meta);

                int index = (j + 1) * 9 + startCol;
                if (index < 54) {
                    menu.setItem(index, permPane);
                }
            }

            startCol++;
        }

        int slot = 9;
        for (PermRank rank : PermRank.values()) {
            ItemStack rankPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
            ItemMeta meta = rankPane.getItemMeta();
            meta.setDisplayName("§3"+rank.getAbr());
            rankPane.setItemMeta(meta);
            menu.setItem(slot, rankPane);
            slot += 9;
        }

        int gray = 10;
        for (int i = 0; i < 6; i++) {
            ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta meta = grayPane.getItemMeta();
            meta.setDisplayName("§8->");
            grayPane.setItemMeta(meta);
            if (gray < 54) {
                menu.setItem(gray, grayPane);
            }
            gray += 9;
        }
        gray = 16;
        for (int i = 0; i < 6; i++) {
            ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta meta = grayPane.getItemMeta();
            meta.setDisplayName(" ");
            grayPane.setItemMeta(meta);
            if (gray < 54) {
                menu.setItem(gray, grayPane);
            }
            gray += 9;
        }
        // Previous page
        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta meta = limePane.getItemMeta();
        meta.setDisplayName("§8« §cPrevious Page");
        limePane.setItemMeta(meta);
        menu.setItem(0, limePane);

        player.openInventory(menu);
    }

    private List<String> getLoreForItem(int factionLevel, String itemName) {
        List<String> lore = new ArrayList<>();
        switch (itemName) {
            case "chest":
                if (factionLevel < 4) {
                    lore.add("§8» §aUnlocked §7Chest Level: §f1");
                    lore.add("§3Next Upgrade level: §f4");
                } else if (factionLevel < 8) {
                    lore.add("§8» §aUnlocked §7Chest Level: §f2");
                    lore.add("§3Next Upgrade level: §f8");
                } else if (factionLevel < 12) {
                    lore.add("§8» §aUnlocked §7Chest Level: §f3");
                    lore.add("§3Next Upgrade level: §f12");
                } else if (factionLevel < 14) {
                    lore.add("§8» §aUnlocked §7Chest Level: §f4");
                    lore.add("§3Next Upgrade level: §f14");
                } else {
                    lore.add("§8» §4Max Level");
                    lore.add("§cChest Level §45");
                }
                break;
            case "shop":
                if (factionLevel < 3) {
                    lore.add("§8» §4Locked");
                    lore.add("§cUnlock at level: §f3");
                } else if (factionLevel < 6) {
                    lore.add("§8» §aUnlocked §fShop Lvl 1");
                    lore.add("§3Next Upgrade level: §f6");
                } else if (factionLevel < 8) {
                    lore.add("§8» §aUnlocked §fShop Lvl 2");
                    lore.add("§3Next Upgrade level: §f8");
                } else if (factionLevel < 10) {
                    lore.add("§8» §aUnlocked §fShop Lvl 3");
                    lore.add("§3Next Upgrade level: §f10");
                } else if (factionLevel < 12) {
                    lore.add("§8» §aUnlocked §fShop Lvl 4");
                    lore.add("§3Next Upgrade level: §f12");
                } else if (factionLevel > 12) {
                    lore.add("§8» §4Max Level");
                    lore.add("§cFaction Shop Lvl 5");
                }
                break;
            case "xp":
                if (factionLevel < 3) {
                    lore.add("§8» §4Locked");
                    lore.add("§cUnlock at level: §f3");
                } else if (factionLevel < 6) {
                    lore.add("§8» §aUnlocked §f+15% XP");
                    lore.add("§3Next Upgrade level: §f6");
                } else if (factionLevel < 10) {
                    lore.add("§8» §aUnlocked §f+35% XP");
                    lore.add("§3Next Upgrade level: §f10");
                } else if (factionLevel < 14) {
                    lore.add("§8» §aUnlocked §f+55% XP");
                    lore.add("§3Next Upgrade level: §f14");
                } else if (factionLevel < 18) {
                    lore.add("§8» §aUnlocked §f+75% XP");
                    lore.add("§3Next Upgrade level: §f18");
                } else if (factionLevel < 19) {
                    lore.add("§8» §aUnlocked §f+90% XP");
                    lore.add("§3Next Upgrade level: §f19");
                } else if (factionLevel == 20) {
                    lore.add("§8» §4Max Level");
                    lore.add("§c+100% XP");
                }
                break;
            case "claims":
                for (int i = 1; i <= 20; i += 2) {
                    if (factionLevel < i) {
                        lore.add("§8» §aUnlocked §f" + (i + 2) + " CHUNKS");
                        lore.add("§3Next Upgrade level: §f" + i);
                        break;
                    }
                }
                if (factionLevel == 20) {
                    lore.add("§8» §4Max Level");
                    lore.add("§c20 CHUNKS");
                }
                break;
            case "farm":
                if (factionLevel >= 10) {
                    lore.add("§8» §4Locked");
                    lore.add("§cUnlock at level: §f10");
                } else {
                    lore.add("§aUnlocked");
                }
                break;
        }
        return lore;
    }
}