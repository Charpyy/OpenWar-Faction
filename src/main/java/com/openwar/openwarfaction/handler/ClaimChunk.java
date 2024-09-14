package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ClaimChunk implements Listener {

    private final FactionManager factionManager;
    private final Map<Player, Chunk> playerLastChunk;

    public ClaimChunk(FactionManager factionManager) {
        this.factionManager = factionManager;
        this.playerLastChunk = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("world")) {
            Chunk playerChunk = player.getLocation().getChunk();
            Faction faction = factionManager.getFactionByChunk(playerChunk);
            if (faction != null) {
                if (!playerLastChunk.containsKey(player) || !playerLastChunk.get(player).equals(playerChunk)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + faction.getName()+" §8«"));
                    playerLastChunk.put(player, playerChunk);
                }
            } else {
                if (playerLastChunk.containsKey(player)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou leaved faction claim of §c" + faction.getName()+" §8«"));
                    playerLastChunk.remove(player);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerBuild(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() != null) {
            Chunk blockChunk = event.getClickedBlock().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(blockChunk);
            Faction playerFaction = factionManager.getFactionByPlayer(player.getUniqueId());
            if (chunkOwner != null && !chunkOwner.equals(playerFaction)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou cannot interact with anything here."));
                event.setCancelled(true);
            }
        }
    }
}