package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.factions.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FactionChat implements Listener {

    private final String logo = "\u00A78» \u00A7bFaction \u00A78« \u00A77";
    private final FactionManager factionManager;
    private final Map<UUID, Boolean> factionChatStatus = new HashMap<>();

    public FactionChat(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public void toggleFactionChat(Player player) {
        UUID playerUUID = player.getUniqueId();
        boolean isEnabled = factionChatStatus.compute(playerUUID, (uuid, status) -> status == null || !status);
        player.sendMessage(logo + "§7Faction Chat:" + (isEnabled ? " §aenabled" : " §cdisabled") + ".");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String rankPl = "null";
        if (factionChatStatus.getOrDefault(playerUUID, false)) {
            Faction faction = factionManager.getFactionByPlayer(playerUUID);
            if (faction != null) {
                Rank rank = faction.getRank(playerUUID);
                switch(rank.toString()) {
                    case "LEADER":
                        rankPl = "§c§lLEADER";
                        break;
                    case "OFFICER":
                        rankPl = "§6§lOFFICER";
                        break;
                    case "MEMBER":
                        rankPl = "§e§lMEMBER";
                        break;
                    case "RECRUE":
                        rankPl = "§7§lRECRUE";
                        break;
                }
                for (Player factionMember : faction.getOnlineMembers()) {
                    factionMember.sendMessage("§8‖§3" + faction.getName() + "§8‖ " + rankPl + " §f• " + player.getName() + ": §b" + event.getMessage());
                }
                event.setCancelled(true);
            } else {
                player.sendMessage("§cYou are not in a faction.");
            }
        }
    }
}

