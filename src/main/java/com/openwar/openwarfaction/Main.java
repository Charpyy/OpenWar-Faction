package com.openwar.openwarfaction;

import com.openwar.openwarfaction.commands.FactionCommand;
import com.openwar.openwarfaction.commands.FactionTabCompletion;
import com.openwar.openwarfaction.factions.FactionGUI;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.handler.ClaimChunk;
import com.openwar.openwarfaction.handler.FactionChat;
import com.openwar.openwarfaction.handler.MenuHandler;
import com.openwar.openwarfaction.handler.PlayerMove;
import com.openwar.openwarlevels.level.PlayerDataManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private PlayerDataManager pl;
    private FactionChat factionChat;
    private HashMap<UUID, Boolean> waitingPlayers = new HashMap<>();
    private Economy economy = null;
    private final String CSV_FILE_PATH = getDataFolder() + "/factions.csv";
    private final String claimsFilePath = getDataFolder() + "/claims.csv";
    FactionGUI factionGUI;
    FactionManager fm;

    public HashMap<UUID, Boolean> getWaitingPlayers() {
        return waitingPlayers;
    }

    private boolean setupDepend() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        RegisteredServiceProvider<PlayerDataManager> levelProvider = getServer().getServicesManager().getRegistration(PlayerDataManager.class);
        RegisteredServiceProvider<FactionManager> factionDataProvider = getServer().getServicesManager().getRegistration(FactionManager.class);
        if (rsp == null || levelProvider == null || factionDataProvider == null) {
            System.out.println("ERROR !!!!!!!!!!!!!!!!!!!!");
            return false;
        }
        economy = rsp.getProvider();
        pl = levelProvider.getProvider();
        fm = factionDataProvider.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        System.out.println("====================================");
        System.out.println(" ");
        System.out.println(" OpenWar - Faction loading...");

        if (!setupDepend()) {return;}

        this.factionChat = new FactionChat(fm);
        this.factionGUI = new FactionGUI(fm, pl,this);

        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new ClaimChunk(fm), this);
        getServer().getPluginManager().registerEvents(factionChat, this);
        getServer().getPluginManager().registerEvents(new MenuHandler(this, fm, economy, factionGUI),this);

        this.getCommand("f").setExecutor(new FactionCommand(factionChat, fm, getWaitingPlayers(), this, economy, factionGUI));
        getCommand("f").setTabCompleter(new FactionTabCompletion(fm));

        System.out.println(" ");
        System.out.println(" OpenWar - Faction loaded !");
        System.out.println(" ");
        System.out.println("====================================");

    }

    @Override
    public void onDisable() {
        System.out.println("====================================");
        System.out.println(" ");
        System.out.println(" OpenWar - Faction Saved !");
        System.out.println(" ");
        System.out.println("====================================");
    }
}
