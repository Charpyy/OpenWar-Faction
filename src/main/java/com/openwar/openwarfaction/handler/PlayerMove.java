package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.Main;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import java.util.UUID;

public class PlayerMove implements Listener {
    private Main plugin;
    private String logo = "\u00A78» \u00A7bFaction \u00A78« \u00A77";


    public PlayerMove(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (plugin.getWaitingPlayers().containsKey(playerUUID)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                player.sendMessage(logo+"\u00A7cYou moved! \u00A77Teleportation cancelled.");
                plugin.getWaitingPlayers().remove(playerUUID);
            }
        }
    }
}
