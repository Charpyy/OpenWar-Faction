package com.openwar.openwarfaction.commands;

import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.handler.FactionChat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AdminCommand implements CommandExecutor {
    private FactionManager factionManager;
    private Main plugin;


    public AdminCommand(FactionManager factionManager, Main plugin) {
        this.factionManager = factionManager;
        this.plugin = plugin;
    }


    private String admin = "§8» §cAdmin §8« §f";
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("openwar.op")) {
            player.sendMessage(admin+" §cYou don't have the required permission to do that.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "xp":
                if (args[1] != null) {
                    int exp = Integer.parseInt(args[1]);
                    Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
                    if (faction != null) {
                        faction.addExp(exp);
                        player.sendMessage(admin+"§6Experience set to §e"+exp+" §6to faction §e"+faction.getName());
                    }
                }
                break;
            case "lvl":
                if (args[1] != null) {
                    int lvl = Integer.parseInt(args[1]);
                    Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
                    if (faction != null) {
                        faction.setLevel(lvl);
                        player.sendMessage(admin+"§6Level set to §e"+lvl+" §6to faction §e"+faction.getName());
                    }
                }
                break;
        }
        return true;
    }
}