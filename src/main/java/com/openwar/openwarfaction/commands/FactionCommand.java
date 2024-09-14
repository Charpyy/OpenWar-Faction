package com.openwar.openwarfaction.commands;

import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionCommand implements CommandExecutor {

    private Map<String, Boolean> confirmDisbanding = new HashMap<>();
    private FactionManager factionManager;
    private HashMap<UUID, Boolean> waitingPlayers;
    private String logo = "\u00A78» \u00A7bFaction \u00A78« \u00A77";
    private Main plugin;
    private Economy economy;

    public FactionCommand(FactionManager factionManager, HashMap<UUID, Boolean> waitingPlayers, Main plugin, Economy economy) {
        this.factionManager = factionManager;
        this.waitingPlayers = waitingPlayers;
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(logo + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage(logo + "Usage: /f <create|new|delete|claim|list|home|sethome|name|invite|promote>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(logo + "\u00A7cPlease provide a faction name.");
                    return true;
                }
                String factionName = args[1];

                if (factionName.length() > 12) {
                    player.sendMessage(logo + "\u00A7cThe faction name cannot be longer than 12 characters.");
                    return true;
                }

                if (!factionName.matches("^[a-zA-Z0-9_]+$")) {
                    player.sendMessage(logo + "\u00A7cThe faction name can only contain letters, numbers, and underscores.");
                    return true;
                }
                if (factionManager.getFactionByPlayer(playerUUID) != null) {
                    player.sendMessage(logo + "\u00A7cYou are already in a faction. Leave your current faction before creating a new one.");
                    return true;
                }

                if (factionManager.factionExists(factionName)) {
                    player.sendMessage(logo + "\u00A7cThis faction already exists.");
                    return true;
                }
                for (Faction faction : factionManager.getAllFactions()) {
                    if (faction.getName().equalsIgnoreCase(factionName)) {
                        player.sendMessage(logo + "\u00A7cA faction with that name already exists.");
                        return true;
                    }
                }

                int cost = 2000;
                if (economy != null) {
                    if (economy.getBalance(player) < cost) {
                        player.sendMessage(logo + "\u00A7cYou do not have enough money to create a faction. You need \u00A77" + cost + "\u00A7c.");
                        return true;
                    }
                }

                try {
                    economy.withdrawPlayer(player, cost);
                    Faction newFaction = new Faction(factionName, playerUUID);
                    factionManager.addFaction(newFaction);
                    Bukkit.broadcastMessage(logo + "\u00A7c" + player.getName() + " \u00A77created the faction \u00A7b" + factionName);
                    player.sendMessage("\u00A78» \u00A77You have been charged \u00A76" + cost + "\u00A77.");
                } catch (Exception e) {
                    player.sendMessage(logo + "\u00A7cAn error occurred while creating the faction. Please try again.");
                    e.printStackTrace();
                }
                break;
            case "disband":
                if (!factionManager.isFactionLeader(playerUUID)) {
                    player.sendMessage(logo + "\u00A7cYou must be the leader to disband the faction.");
                    return true;
                }
                if (confirmDisbanding.containsKey(playerUUID.toString()) && confirmDisbanding.get(playerUUID.toString())) {
                    Faction factionToDisband = factionManager.getFactionByPlayer(playerUUID);
                    factionManager.deleteFaction(factionToDisband);
                    Bukkit.broadcastMessage(logo + "\u00A7c" + player.getName() + " \u00A77has disbanded the faction \u00A7b" + factionToDisband.getName() + "\u00A77!");
                    player.sendMessage(logo + "\u00A7cYour faction has been deleted.");
                    confirmDisbanding.remove(playerUUID.toString());
                } else {
                    player.sendMessage(logo + "\u00A77Are you sure you want to disband your faction? \u00A77Type /f disband again to confirm.");
                    confirmDisbanding.put(playerUUID.toString(), true);
                }
                break;


            case "claim":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "\u00A7cYou need to be in a faction to claim land.");
                    return true;
                }
                Chunk chunkToClaim = player.getLocation().getChunk();
                if (factionManager.isLandClaimed(chunkToClaim)) {
                    player.sendMessage(logo + "\u00A7cThis land is already claimed by another faction.");
                    return true;
                }
                Faction playerFaction = factionManager.getFactionByPlayer(playerUUID);
                factionManager.claimLand(chunkToClaim, playerFaction);
                player.sendMessage(logo + "\u00A77Land claimed for your faction!");
                break;

            case "list":
                if (factionManager.getAllFactions() == null){
                    player.sendMessage(logo + "No faction found.");
                } else {
                    factionManager.getAllFactions().forEach(faction ->
                            player.sendMessage(logo + "Faction: " + faction.getName() + " - Members: " + faction.getMembers().size()));
                }
                break;

            case "home":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are not in a faction.");
                    return true;
                }
                if (waitingPlayers.containsKey(player.getUniqueId())) {
                    player.sendMessage(logo + "§cYou are already teleporting. Please wait and don't move.");
                    return true;
                }

                Faction faction = factionManager.getFactionByPlayer(playerUUID);
                Location home = faction.getHomeLocation();

                if (home != null) {
                    waitingPlayers.put(player.getUniqueId(), true);
                    new BukkitRunnable() {
                        int countdown = 5;

                        @Override
                        public void run() {
                            if (waitingPlayers.containsKey(player.getUniqueId()) && waitingPlayers.get(player.getUniqueId())) {
                                if (countdown > 0) {
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00A78» \u00A77Teleportation in \u00A7f" + countdown + " \u00A77seconds... \u00A78«"));
                                    countdown--;
                                } else {
                                    player.teleport(home);
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00A78» \u00A7bTeleported to your faction's home."));
                                    waitingPlayers.remove(player.getUniqueId());
                                    this.cancel();
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0, 20);
                } else {
                    player.sendMessage(logo + "\u00A7cYour faction does not have a home set.");
                }
                break;

            case "sethome":
                faction = factionManager.getFactionByPlayer(playerUUID);
                if (faction == null) {
                    player.sendMessage(logo + "§cYou must be in a faction to set a home.");
                    return true;
                }
                Chunk playerChunk = player.getLocation().getChunk();
                Faction chunkOwner = factionManager.getFactionByChunk(playerChunk);
                if (!faction.equals(chunkOwner)) {
                    player.sendMessage(logo + "§cYou can only set home in your faction's claimed land.");
                    return true;
                }

                faction.setHomeLocation(player.getLocation());
                player.sendMessage(logo + "§7Faction Home set §asuccessfully!");
                break;

            case "name":
                if (args.length < 2) {
                    player.sendMessage(logo + "\u00A7cPlease provide a new faction name.");
                    return true;
                }

                if (!factionManager.isFactionLeader(playerUUID)) {
                    player.sendMessage(logo + "\u00A7cOnly the leader can change the faction name.");
                    return true;
                }

                String newName = args[1];

                if (newName.length() > 12) {
                    player.sendMessage(logo + "\u00A7cThe faction name cannot be longer than 12 characters.");
                    return true;
                }

                if (!newName.matches("^[a-zA-Z0-9_]+$")) {
                    player.sendMessage(logo + "\u00A7cThe faction name can only contain letters, numbers, and underscores.");
                    return true;
                }

                for (Faction factions : factionManager.getAllFactions()) {
                    if (factions.getName().equalsIgnoreCase(newName)) {
                        player.sendMessage(logo + "\u00A7cA faction with that name already exists.");
                        return true;
                    }
                }

                faction = factionManager.getFactionByPlayer(playerUUID);
                String oldName = faction.getName();
                faction.setName(newName);
                Bukkit.broadcastMessage(logo + "\u00A77The faction \u00A7b" + oldName + " \u00A77has changed its name to \u00A7b" + newName + "\u00A77!");
                break;



            case "invite":
                if (args.length < 2) {
                    player.sendMessage(logo + "\u00A77Please provide the name of the player to invite.");
                    return true;
                }
                if (!factionManager.isFactionLeader(playerUUID)) {
                    player.sendMessage(logo + "\u00A7cOnly the leader can invite players.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(logo + "\u00A77That player is not online.");
                    return true;
                }
                faction = factionManager.getFactionByPlayer(playerUUID);
                factionManager.invitePlayerToFaction(target.getUniqueId(), factionManager.getFactionByPlayer(playerUUID));
                player.sendMessage(logo + "\u00A77Player \u00A7f" + target.getName() + " \u00A77invited to the faction.");
                target.sendMessage(logo+ "§7The faction §f"+faction.getName()+" §7has invited you, §f/f join");
                break;
            case "join":
                if (factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are already on a faction.");
                    return true;
                }
                String invitedFaction = factionManager.getInvitedFaction(playerUUID);
                if (invitedFaction == null) {
                    player.sendMessage(logo + "§cYou are not invited in any faction.");
                    return true;
                }
                faction = factionManager.getFactionByName(invitedFaction);
                if (faction == null) {
                    player.sendMessage(logo + "§cThis faction doesnt exist.");
                    return true;
                }
                factionManager.addMemberToFaction(playerUUID, faction);
                player.sendMessage(logo + "§7You joined: §c" + faction.getName());

                for (UUID memberUUID : faction.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(logo + "§a" + player.getName() + " §7joined your faction.");
                    }
                }
                break;
            case "promote":
                if (args.length < 2) {
                    player.sendMessage(logo + "Please provide the name of the player to promote.");
                    return true;
                }
                if (!factionManager.isFactionLeader(playerUUID)) {
                    player.sendMessage(logo + "Only the leader can promote players.");
                    return true;
                }
                Player promoteTarget = Bukkit.getPlayer(args[1]);
                if (promoteTarget == null) {
                    player.sendMessage(logo + "That player is not online.");
                    return true;
                }
                factionManager.promoteMember(promoteTarget.getUniqueId(), factionManager.getFactionByPlayer(playerUUID));
                player.sendMessage(logo + "Player " + promoteTarget.getName() + " promoted!");
                break;
            case "help":
                player.sendMessage(logo +"§fHelp Page:");
                player.sendMessage("\u00A78– \u00A77/f create|new <name> : §fCreate a new faction");
                player.sendMessage("\u00A78– \u00A77/f disband : §fDisband your faction");
                player.sendMessage("\u00A78– \u00A77/f invite <name> : §fInvite player to your faction");
                player.sendMessage("\u00A78– \u00A77/f join : §fJoin a faction");
                player.sendMessage("\u00A78– \u00A77/f promote : §fPromote a player");
                player.sendMessage("\u00A78– \u00A77/f leave : §fLeave your current faction");
                player.sendMessage("\u00A78– \u00A77/f f : §fSee information about your faction");
                player.sendMessage("\u00A78– \u00A77/f list : §fList of all factions");
                player.sendMessage("\u00A78– \u00A77/f name <name> : §fChange the name of your faction");
                player.sendMessage("\u00A78– \u00A77/f sethome : §fSet the faction home");
                player.sendMessage("\u00A78– \u00A77/f home : §fTeleport to the faction home");
                player.sendMessage("\u00A78– \u00A77/f claim : §fClaim a chunk");

            case "leave":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are not on a faction.");
                    return true;
                }

               faction = factionManager.getFactionByPlayer(playerUUID);

                if (faction.getLeaderUUID().equals(playerUUID)) {
                    player.sendMessage(logo + "§cYou are the leader of this faction. §fUse §b/f disband §fto disband your faction.");
                    faction.removeMember(playerUUID);
                    factionManager.removePlayerFromFaction(playerUUID);
                    player.sendMessage(logo + "§fYou leaved the faction §b" + faction.getName() + ".");
                    return true;
                }
                break;
            case "f":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "You are not in a faction.");
                    return true;
                }
                playerFaction = factionManager.getFactionByPlayer(playerUUID);
                player.sendMessage(logo+ "\u00A7fFaction Info:");
                player.sendMessage("\u00A78– \u00A77Faction Name: \u00A7f" + playerFaction.getName());
                player.sendMessage("\u00A78– \u00A77Members: \u00A7f" + playerFaction.getMembers().size());
                if (playerFaction.getHomeLocation() != null) {
                    player.sendMessage("\u00A78– \u00A77Home Location: \u00A78X: \u00A77" + (int) playerFaction.getHomeLocation().getX() + " \u00A78Y: \u00A77" + (int) playerFaction.getHomeLocation().getY() + " \u00A78Z: \u00A77" + (int) playerFaction.getHomeLocation().getZ());
                }
                else {
                    player.sendMessage("\u00A78– \u00A77Home: \u00A7fNot Set.");
                }
                break;
            default:
                player.sendMessage(logo + "Unknown command. Usage: /f help");
                break;
        }
        return true;
    }
}
