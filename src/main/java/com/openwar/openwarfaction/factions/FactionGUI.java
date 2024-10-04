package com.openwar.openwarfaction.factions;

import com.openwar.openwarfaction.Main;
import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarlevels.level.PlayerLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FactionGUI {

    private final FactionManager factionManager;
    private final PlayerDataManager pl;
    private final Main main;
    private final Map<UUID, ItemStack> leaderHeadCache = new HashMap<>();

    public FactionGUI(FactionManager factionManager, PlayerDataManager pl, Main main) {
        this.factionManager = factionManager;
        this.pl = pl;
        this.main = main;
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

    private void setMenuBackground27(Inventory menu) {
        ItemStack glassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 0, " ");
        for (int i = 0; i < 27; i++) {
            menu.setItem(i, glassPane);
        }

        ItemStack borderGlassPane = createColoredGlassPane(Material.STAINED_GLASS_PANE, (short) 15, " ");
        for (int i = 0; i < 27; i++) {
            if (isBorderSlot27(i)) {
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
        int progress = (int) ((percentage / 100) * 10);
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
        lore.add("§7Members Online: §f" + faction.getOnlineMembers().size() + "§7/§f" + faction.getMembers().size());
        FactionManager fm = new FactionManager();
        lore.add("§7Claims: §f" + fm.getClaimedChunks(faction).size());
        if (faction.getHomeLocation() != null) {
            lore.add("§7Home Location: \u00A78X: \u00A77" + (int) faction.getHomeLocation().getX() + " \u00A78Y: \u00A77" + (int) faction.getHomeLocation().getY() + " \u00A78Z: \u00A77" + (int) faction.getHomeLocation().getZ());
        } else {
            lore.add("§7Home Location: \u00A7fNot Set.");
        }
        lore.add("§7Members Name: ");
        for (Map.Entry<UUID, Rank> entry : faction.getMembers().entrySet()) {
            UUID memberUUID = entry.getKey();
            OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
            lore.add(" §7- §b" + member.getName());
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
            PlayerLevel playerLevel = pl.loadPlayerData(leader.getUniqueId(), factionManager);
            int level = playerLevel.getLevel();
            meta.setOwningPlayer(leader);
            meta.setDisplayName("§4§lFaction Leader");
            meta.setLore(Arrays.asList("§c" + leaderName+" §8(§cLevel: §4"+level+"§8)"));
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


    private static boolean isBorderSlot27(int slot) {
        return slot < 9 || slot >= 18 || slot % 9 == 0 || slot % 9 == 8;
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
        Inventory menu = Bukkit.createInventory(null, 54, "§7§k§l!!§r §c=== §8§l⟦§4§l" + faction.getName()+"§r§8§l⟧ §c=== §7§k§l!!");

        setMenuBackground(menu);
        ItemStack factionLevelItem = createFactionLevelItem(faction);
        ItemStack infoItem = createFactionInfoItem(faction);
        ItemStack upgradeItem = createUpgradeItem();
        ItemStack fperm = createFpermItem();
        ItemStack load = createCustomItem(Material.SKULL_ITEM, "§7Loading data ...", null);
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            ItemStack leaderHead = getLeaderHead(Bukkit.getOfflinePlayer(faction.getLeaderUUID()).getName());
            Bukkit.getScheduler().runTask(main, () -> {
                menu.setItem(20, leaderHead);
                player.updateInventory();
            });
        });
        menu.setItem(24, factionLevelItem);
        menu.setItem(30, infoItem);
        menu.setItem(32, upgradeItem);
        menu.setItem(22, fperm);
        menu.setItem(20, load);

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
            meta.setDisplayName("§3" + rank.getAbr());
            rankPane.setItemMeta(meta);
            menu.setItem(slot, rankPane);
            slot += 9;
        }

        slotGris(menu, 10);
        slotGris(menu, 17);
        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        ItemMeta meta = limePane.getItemMeta();
        meta.setDisplayName("§8» §aNext Page");
        limePane.setItemMeta(meta);
        menu.setItem(8, limePane);

        player.openInventory(menu);
    }

    public void openFactionPermPage2(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Perms §f- §3Page 2");
        setMenuBackground(menu);
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);

        int startCol = 2;
        for (int i = 6; i < Permission.values().length; i++) {
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
            meta.setDisplayName("§3" + rank.getAbr());
            rankPane.setItemMeta(meta);
            menu.setItem(slot, rankPane);
            slot += 9;
        }
        slotGris(menu, 10);
        slotGris(menu, 17);
        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        ItemMeta meta = limePane.getItemMeta();
        meta.setDisplayName("§8» §aNext Page");
        limePane.setItemMeta(meta);
        menu.setItem(8, limePane);

        ItemStack redPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta redmeta = limePane.getItemMeta();
        redmeta.setDisplayName("§8« §cPrevious Page");
        redPane.setItemMeta(redmeta);
        menu.setItem(0, redPane);

        player.openInventory(menu);
    }

    public void openFactionPermPage3(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§b§lFaction Perms §f- §3Page 3");
        setMenuBackground(menu);
        UUID playerUUID = player.getUniqueId();
        Faction faction = factionManager.getFactionByPlayer(playerUUID);

        int startCol = 2;
        for (int i = 12; i < Permission.values().length; i++) {
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
            meta.setDisplayName("§3" + rank.getAbr());
            rankPane.setItemMeta(meta);
            menu.setItem(slot, rankPane);
            slot += 9;
        }

        slotGris(menu, 16);
        slotGris(menu, 15);
        slotGris(menu, 14);
        slotGris(menu, 13);
        slotGris(menu, 10);
        // Previous page
        ItemStack limePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta meta = limePane.getItemMeta();
        meta.setDisplayName("§8« §cPrevious Page");
        limePane.setItemMeta(meta);
        menu.setItem(0, limePane);

        player.openInventory(menu);
    }
//===============================FACTION SHOP MENU MAIN==========================================================================
    public void openFactionShopMain(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§b§lFaction Shops");
        setMenuBackground27(menu);

        List<String> lore1 = new ArrayList<>();
        lore1.add("§bAll vehicles from MCHELI");

        List<String> lore2 = new ArrayList<>();
        lore2.add("§aUsefull machine from HBM");

        ItemStack mcheli = createCustomItem(Material.matchMaterial("mcheli:ah64"), "§3MCHeli", lore1);
        //TODO FAIRE HBM DU COUP MAINTENANT LOL
        ItemStack hbm = createCustomItem(Material.matchMaterial("hbm:machine_rtg_grey"), "§2HBM", lore2);

        menu.setItem(12, mcheli);
        menu.setItem(14, hbm);

        player.openInventory(menu);
    }

    public void openFactionShopMCHELI(Player player) {
        Inventory mcheliShop = Bukkit.createInventory(null, 36, "§k§l!!!§r§3§l MCHELI SHOP §8§r§8§k§l!!!");
        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 15);
        ItemMeta blackMeta = blackGlass.getItemMeta();
        if (blackMeta != null) {
            blackMeta.setDisplayName(" ");
            blackGlass.setItemMeta(blackMeta);
        }
        for (int i = 0; i < mcheliShop.getSize(); i++) {
            mcheliShop.setItem(i, blackGlass);
        }
        ItemStack antiAir = new ItemStack(Material.matchMaterial("mcheli:mim-23") != null ? Material.matchMaterial("mcheli:mim-23") : Material.BARRIER);
        ItemMeta antiAirMeta = antiAir.getItemMeta();
        if (antiAirMeta != null) {
            antiAirMeta.setDisplayName(ChatColor.GRAY + "Anti - Air");
            antiAir.setItemMeta(antiAirMeta);
        }
        ItemStack helicopters = new ItemStack(Material.matchMaterial("mcheli:ch47") != null ? Material.matchMaterial("mcheli:ch47") : Material.BARRIER);
        ItemMeta helicoptersMeta = helicopters.getItemMeta();
        if (helicoptersMeta != null) {
            helicoptersMeta.setDisplayName(ChatColor.GRAY + "Helicopter");
            helicopters.setItemMeta(helicoptersMeta);
        }
        ItemStack planes = new ItemStack(Material.matchMaterial("mcheli:f22a") != null ? Material.matchMaterial("mcheli:f22a") : Material.BARRIER);
        ItemMeta planesMeta = planes.getItemMeta();
        if (planesMeta != null) {
            planesMeta.setDisplayName(ChatColor.GRAY + "Plane");
            planes.setItemMeta(planesMeta);
        }
        ItemStack tanks = new ItemStack(Material.matchMaterial("mcheli:merkava_mk4") != null ? Material.matchMaterial("mcheli:merkava_mk4") : Material.BARRIER);
        ItemMeta tanksMeta = tanks.getItemMeta();
        if (tanksMeta != null) {
            tanksMeta.setDisplayName(ChatColor.GRAY + "Tank");
            tanks.setItemMeta(tanksMeta);
        }
        ItemStack boats = new ItemStack(Material.matchMaterial("mcheli:zodiac") != null ? Material.matchMaterial("mcheli:zodiac") : Material.BARRIER);
        ItemMeta boatsMeta = boats.getItemMeta();
        if (boatsMeta != null) {
            boatsMeta.setDisplayName(ChatColor.GRAY + "Boats");
            boats.setItemMeta(boatsMeta);
        }
        mcheliShop.setItem(15, antiAir);
        mcheliShop.setItem(13, helicopters);
        mcheliShop.setItem(11, planes);
        mcheliShop.setItem(21, tanks);
        mcheliShop.setItem(23, boats);
        ItemStack whiteGlass = new ItemStack(Material.STAINED_GLASS_PANE,1 ,(short) 0);
        ItemMeta whiteMeta = whiteGlass.getItemMeta();
        if (whiteMeta != null) {
            whiteMeta.setDisplayName(" ");
            whiteGlass.setItemMeta(whiteMeta);
        }
        int[] whiteSlots = {10, 12, 14, 16, 19, 20, 22, 24, 25};
        for (int slot : whiteSlots) {
            mcheliShop.setItem(slot, whiteGlass);
        }
        player.openInventory(mcheliShop);
    }

    //================================================== FACTION SHOP MCHELI MENU =====================================================================
    public void openFacShop(Player player, String category, String categoryName, int level) {
        List<String> factionShopItems;

        //categorie
        switch (category.toLowerCase()) {
            case "heli":
                factionShopItems = getFactionShopHeliItems();
                break;
            case "tank":
                factionShopItems = getFactionShopTankItems();
                break;
            case "antiair":
                factionShopItems = getFactionShopAntiAirItems();
                break;
            case "bato":
                factionShopItems = getFactionShopBatoItems();
                break;
            case "plane":
                factionShopItems = getFactionShopPlaneItems();
                break;
            default:
                factionShopItems = new ArrayList<>();
                break;
        }

        List<ItemStack> items = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> prices = new ArrayList<>();

        for (String itemData : factionShopItems) {
            String[] parts = itemData.split("\\$");
            int requiredLevel = Integer.parseInt(parts[0]);

            if (requiredLevel <= level) {
                names.add(parts[2]);
                prices.add(parts[1]);
                items.add(parseItem(parts[2]));
            }
        }

        int totalItems = items.size();

        int rows = (int) Math.ceil(totalItems / 7.0) + 2;
        if (rows < 3) rows = 3;

        Inventory inv = Bukkit.createInventory(null, rows * 9, "§8§l⟪ §b" + categoryName + " §8§l⟫");


        inv.setItem(4, createGlassPane("§8» §cBack", Material.STAINED_GLASS_PANE, (short) 14));
        int slot = 10;
        for (int i = 0; i < totalItems; i++) {
            ItemStack item = items.get(i);
            String name = names.get(i);
            String price = prices.get(i);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§8§l⟦ §b" + name + " §8§l⟧");
            meta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + "▶ " + ChatColor.GRAY + "Buy for: " + ChatColor.GOLD + "$" + price));
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            if (slot == 16 || slot == 25 || slot == 34)
            {
                slot= slot+2;
            }
            slot++;
        }
        addBorders(inv, rows);
        inv.setItem(4, createGlassPane("§8» §cBack", Material.STAINED_GLASS_PANE, (short) 14));
        player.openInventory(inv);
    }

    private List<String> getFactionShopHeliItems() {
        return Arrays.asList(
                "1$4560$mcheli robinson r44",
                "1$3100$mcheli bell47g",
                "1$3200$mcheli bell47gf",
                "1$6400$mcheli mh6",
                "1$6900$mcheli bell206l",
                "1$2310$mcheli fl282",
                "1$4300$mcheli robinson r44f",
                "1$8490$mcheli nh90",
                "2$8200$mcheli mi24ps",
                "2$8800$mcheli sh3",
                "2$19850$mcheli bell207",
                "3$11340$mcheli ch47",
                "3$13450$mcheli w3",
                "3$18100$mcheli mh53e",
                "3$20840$mcheli ah6",
                "4$19450$mcheli sh60",
                "4$21400$mcheli oh1",
                "4$23040$mcheli penetrator",
                "4$24500$mcheli ec665",
                "5$18210$mcheli uh1c",
                "5$34560$mcheli mi24",
                "5$36780$mcheli mi28",
                "5$32450$mcheli mh60l dap",
                "5$40510$mcheli mh60g",
                "5$41350$mcheli ah64",
                "5$35900$mcheli ka50n",
                "5$36100$mcheli ka52",
                "5$37810$mcheli ah1z"
        );
    }

    private List<String> getFactionShopTankItems() {
        return Arrays.asList(
                "1$1450$mcheli growler",
                "2$2400$mcheli mxtmv",
                "2$9450$mcheli m1129",
                "2$9800$mcheli kurganets25",
                "3$12310$mcheli kv2",
                "3$23400$mcheli m26",
                "4$35670$mcheli merkava mk4",
                "4$37200$mcheli m1a2",
                "5$45120$mcheli t84",
                "5$46900$mcheli t90"
        );
    }

    private List<String> getFactionShopAntiAirItems() {
        return Arrays.asList(
                "2$5600$mcheli bofors40mml60",
                "2$6700$mcheli 25mmaamg",
                "2$3400$mcheli fgm148",
                "3$5900$mcheli fim92",
                "3$21340$mcheli mk15",
                "3$25300$mcheli mim23",
                "5$39000$mcheli s75"
        );
    }

    private List<String> getFactionShopBatoItems() {
        return Arrays.asList(
                "1$750$mcheli zodiac",
                "2$4530$mcheli mark5",
                "4$14340$mcheli cb90",
                "5$27890$mcheli project1204"
        );
    }

    private List<String> getFactionShopPlaneItems() {
        return  Arrays.asList(
                "1$6120$mcheli p51d",
                "1$6670$mcheli a6m2",
                "1$4330$mcheli t4",
                "1$4160$mcheli an2",
                "2$8370$mcheli h8k",
                "2$7440$mcheli macchi mc72",
                "2$8900$mcheli n1k1",
                "2$7930$mcheli f1m",
                "2$9310$mcheli macchi m33",
                "2$7610$mcheli emb314",
                "2$6940$mcheli us2",
                "3$19330$mcheli mv22",
                "3$21890$mcheli b29",
                "4$23940$mcheli mig29",
                "4$24510$mcheli su33",
                "4$27600$mcheli a10",
                "4$25890$mcheli su47",
                "4$25910$mcheli su37",
                "5$34560$mcheli fa18f",
                "5$36780$mcheli f15s mtd",
                "5$58210$mcheli b2a",
                "5$38340$mcheli f35c",
                "5$39100$mcheli f22a",
                "5$39540$mcheli f35b",
                "5$45900$mcheli ac130",
                "5$40310$mcheli f35a"
        );
    }

    private ItemStack parseItem(String itemName) {
        Material material = Material.matchMaterial(itemName.replace("mcheli ", "mcheli:").replace(" ", "_"));
        return new ItemStack(material != null ? material : Material.BARRIER);
    }

    private ItemStack createGlassPane(String name, Material material, short color) {
        ItemStack pane = new ItemStack(material, 1, color);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    private void addBorders(Inventory inv, int rows) {
        int size = rows * 9;
        for (int i = 0; i < size; i++) {
            if (i < 9 || (i % 9 == 0) || (i % 9 == 8) || (i >= size - 9)) {
                ItemStack pane = createGlassPane(" ", Material.STAINED_GLASS_PANE, (short) 15);
                inv.setItem(i, pane);
            }
        }
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
                    lore.add("§8» §aUnlocked §7Shop Level: §f1");
                    lore.add("§3Next Upgrade level: §f6");
                } else if (factionLevel < 8) {
                    lore.add("§8» §aUnlocked §7Shop Level: §f2");
                    lore.add("§3Next Upgrade level: §f8");
                } else if (factionLevel < 10) {
                    lore.add("§8» §aUnlocked §7Shop Level: §f3");
                    lore.add("§3Next Upgrade level: §f10");
                } else if (factionLevel < 12) {
                    lore.add("§8» §aUnlocked §7Shop Level: §f4");
                    lore.add("§3Next Upgrade level: §f12");
                } else {
                    lore.add("§8» §4Max Level");
                    lore.add("§cFaction Shop Level §45");
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
                    int maxChunks = (i == 1) ? 4 : (i + 2);
                    if (factionLevel < i) {
                        lore.add("§8» §aUnlocked §f" + maxChunks + " §7Chunks");
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
                    lore.add("§8» §aUnlocked");
                } else {
                    lore.add("§8» §4Locked");
                    lore.add("§cUnlock at level: §f10");
                }
                break;
        }
        return lore;
    }

    private void slotGris(Inventory menu, int slot) {
        for (int i = 0; i < 6; i++) {
            ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            ItemMeta meta = grayPane.getItemMeta();
            meta.setDisplayName(" ");
            grayPane.setItemMeta(meta);
            if (slot < 54) {
                menu.setItem(slot, grayPane);
            }
            slot += 9;
        }
    }
}