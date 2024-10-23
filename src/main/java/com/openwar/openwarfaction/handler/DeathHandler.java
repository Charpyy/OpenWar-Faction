package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeathHandler implements Listener {

    FactionManager fm;
    JavaPlugin main;
    Map<UUID, Integer> death = new HashMap<>();

    public DeathHandler(FactionManager fm, JavaPlugin main) {
        this.fm = fm;
        this.main = main;
    }



    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player attacker = player.getKiller();
        Faction fac = fm.getFactionByPlayer(player.getUniqueId());
        if (attacker != null) {
            Faction facattack = fm.getFactionByPlayer(attacker.getUniqueId());
            Location loc = player.getLocation();
            if (fm.getClaimedChunks(fac).contains(loc.getChunk())) {
                if (facattack != fac) {
                    death.put(player.getUniqueId(), death.getOrDefault(player.getUniqueId(), 0) + 1);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            death.remove(player.getUniqueId());
                        }
                    }.runTaskLater(main, 20 * 300);
                }
            }
        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (fm.getFactionByPlayer(event.getPlayer().getUniqueId()) != null) {
            Faction fac = fm.getFactionByPlayer(event.getPlayer().getUniqueId());
            if (fac.getHomeLocation() != null) {
                if (death.get(event.getPlayer().getUniqueId()) > 3) {
                    event.getPlayer().sendMessage("§c» §7You've died too many times in a row, you can't spawn at your faction home for now.!");
                    return;
                }
                event.getPlayer().teleport(fac.getHomeLocation());
            }
        }
    }
}
