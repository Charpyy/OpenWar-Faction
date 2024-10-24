package com.openwar.openwarfaction.commands;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactionTabCompletion implements TabCompleter {

    private FactionManager fm;

    public FactionTabCompletion(FactionManager fm) {
        this.fm = fm;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("f")) {
            if (args.length == 1) {
                suggestions = Arrays.asList("ally","perm","menu","leave", "help", "chat", "create", "delete", "invite", "promote", "claim", "home", "sethome", "unclaim", "kick", "disband", "list", "f", "join", "demote", "info");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick") ||
                        args[0].equalsIgnoreCase("demote") || args[0].equalsIgnoreCase("promote")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        suggestions.add(player.getName());
                    }
                }
                if (args[0].equalsIgnoreCase("perm")) {
                    suggestions = Arrays.asList("show", "set");
                }
                if (args[0].equalsIgnoreCase("info")) {
                    List<Faction> flist = fm.getAllFactions();
                    for (Faction faction : flist) {
                        suggestions.add(faction.getName());
                    }
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("perm") && args[1].equalsIgnoreCase("set")) {
                suggestions = Arrays.asList("OFFICER", "MEMBER",  "RECRUE", "ALLY", "NEUTRAL");
            }
        }
        return filterSuggestions(suggestions, args[args.length - 1]);
    }

    private List<String> filterSuggestions(List<String> suggestions, String input) {
        List<String> filtered = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                filtered.add(suggestion);
            }
        }
        return filtered;
    }
}
