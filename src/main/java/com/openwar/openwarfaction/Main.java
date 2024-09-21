package com.openwar.openwarfaction;

import com.openwar.openwarfaction.commands.AdminCommand;
import com.openwar.openwarfaction.commands.FactionCommand;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.handler.ClaimChunk;
import com.openwar.openwarfaction.handler.FactionChat;
import com.openwar.openwarfaction.handler.MenuHandler;
import com.openwar.openwarfaction.handler.PlayerMove;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private FactionManager factionManager;
    private FactionChat factionChat;
    private HashMap<UUID, Boolean> waitingPlayers = new HashMap<>();
    private Economy economy = null;
    private static final String CSV_FILE_PATH = "plugins/OpenWar-Faction/factions.csv";
    private final String claimsFilePath = getDataFolder() + "/claims.csv";

    public HashMap<UUID, Boolean> getWaitingPlayers() {
        return waitingPlayers;
    }
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public void onEnable() {
        System.out.println("########################");
        System.out.println(" ");
        System.out.println(" OpenWar - Faction loading...");
        this.factionManager = new FactionManager();
        this.factionChat = new FactionChat(factionManager);
        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new ClaimChunk(factionManager), this);
        getServer().getPluginManager().registerEvents(factionChat, this);
        getServer().getPluginManager().registerEvents(new MenuHandler(this, factionManager, economy),this);
        setupEconomy();
        factionManager.loadFactionsFromCSV(CSV_FILE_PATH);
        this.getCommand("f").setExecutor(new FactionCommand(factionChat, factionManager, getWaitingPlayers(), this, economy));
        this.getCommand("fadmin").setExecutor(new AdminCommand(factionManager, this));
        factionManager.loadClaimsFromCSV(claimsFilePath);
        factionManager.loadFactionChests();
        System.out.println(" ");
        System.out.println(" OpenWar - Faction loaded !");
        System.out.println(" ");
        System.out.println("########################");
        startAutoSaveTask();
    }

    @Override
    public void onDisable() {
        factionManager.saveFactionsToCSV(CSV_FILE_PATH);
        factionManager.saveClaimsToCSV(claimsFilePath);
        factionManager.saveFactionChests();
        System.out.println("########################");
        System.out.println(" ");
        System.out.println(" OpenWar - Faction Saved !");
        System.out.println(" ");
        System.out.println("########################");
    }

    public void startAutoSaveTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                factionManager.saveFactionsToCSV(CSV_FILE_PATH);
                factionManager.saveClaimsToCSV(claimsFilePath);
                factionManager.saveFactionChests();
            }
        }, 0L, 6000L);
    }
}
