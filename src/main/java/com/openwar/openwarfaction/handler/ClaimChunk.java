package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimChunk implements Listener {

    private final FactionManager factionManager;
    private final Map<Player, Chunk> playerLastChunk;
    private List<Material> container;

    public ClaimChunk(FactionManager factionManager) {
        this.factionManager = factionManager;
        this.playerLastChunk = new HashMap<>();
        container = new ArrayList<>();
        loadContainer();
    }

    private void loadContainer() {
        container.add(Material.matchMaterial(""));
        container.add(Material.matchMaterial(""));
        container.add(Material.matchMaterial(""));
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        //TODO replace world to faction
        if (player.getWorld().getName().equals("world")) {
            Chunk fromChunk = event.getFrom().getChunk();
            Chunk toChunk = event.getTo().getChunk();

            if (!fromChunk.equals(toChunk)) {
                Faction fromFaction = factionManager.getFactionByChunk(fromChunk);
                Faction toFaction = factionManager.getFactionByChunk(toChunk);

                if (toFaction != null && (fromFaction == null || !fromFaction.getFactionUUID().equals(toFaction.getFactionUUID()))) {
                    if (!playerLastChunk.containsKey(player) || !playerLastChunk.get(player).equals(toChunk)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + toFaction.getName() + " §8«"));
                        playerLastChunk.put(player, toChunk);
                    }
                } else if (toFaction == null && fromFaction != null) {
                    if (playerLastChunk.containsKey(player)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou left faction claim of §c" + fromFaction.getName() + " §8«"));
                        playerLastChunk.remove(player);
                    }
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
            //TODO si le joueur possède un item qui permet de faire pété des trucs genre une grenade, dans sa main, il faut permettre l'interaction car sinon il pourront pas raid, et faut test si ça permet pas de usebug
            if (chunkOwner != null && !chunkOwner.equals(playerFaction) && isContainer(event.getClickedBlock())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cYou cannot interact with anything here."));
                event.setCancelled(true);
            }
        }
    }

    public boolean isContainer(Block bloc) {


    }
}