package com.openwar.openwarfaction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private String admin = "§8» §cAdmin §8« §f";
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("openwar.op")) {
            player.sendMessage(admin+" §cYou don't have the required permission to do that.");
            return true;
        }

        return true;
    }
}