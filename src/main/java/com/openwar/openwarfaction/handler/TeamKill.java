package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class TeamKill implements Listener {

    private FactionManager fm;

    public TeamKill(FactionManager fm) {
        this.fm = fm;
    }

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                Faction fac1 = fm.getFactionByPlayer(victim.getUniqueId());
                Faction fac2 = fm.getFactionByPlayer(attacker.getUniqueId());
                if (fac1 == fac2 && !fm.getTk(fac1)) {
                    event.setCancelled(true);
                    attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7» §cYou can't attack your mate §4" + victim.getName() + " §7«"));
                }
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player) {
                    Player shooter = (Player) projectile.getShooter();
                    Player victim = (Player) event.getEntity();
                    Faction fac1 = fm.getFactionByPlayer(victim.getUniqueId());
                    Faction fac2 = fm.getFactionByPlayer(shooter.getUniqueId());
                    if (fac1 == fac2 && !fm.getTk(fac1)) {
                        event.setCancelled(true);
                        shooter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7» §cYou can't attack your mate §4" + victim.getName() + " §7«"));
                    }
                }
            }
        }
    }
}