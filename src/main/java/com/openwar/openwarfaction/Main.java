package com.openwar.openwarfaction;

import com.openwar.openwarfaction.commands.FactionCommand;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.handler.ClaimChunk;
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
        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new ClaimChunk(factionManager), this);
        getServer().getPluginManager().registerEvents(new MenuHandler(this),this);
        setupEconomy();
        factionManager.loadFactionsFromCSV(CSV_FILE_PATH);
        this.getCommand("f").setExecutor(new FactionCommand(factionManager, getWaitingPlayers(), this, economy));
        factionManager.loadClaimsFromCSV(claimsFilePath);
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
    }

    public void startAutoSaveTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                factionManager.saveFactionsToCSV(CSV_FILE_PATH);
                factionManager.saveClaimsToCSV(claimsFilePath);
            }
        }, 0L, 6000L);
    }
}
