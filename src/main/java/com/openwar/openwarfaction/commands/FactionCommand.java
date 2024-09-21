package com.openwar.openwarfaction.commands;

import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.*;
import com.openwar.openwarfaction.handler.FactionChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FactionCommand implements CommandExecutor {

    private Map<String, Boolean> confirmDisbanding = new HashMap<>();
    private FactionManager factionManager;
    private HashMap<UUID, Boolean> waitingPlayers;
    private String logo = "\u00A78» \u00A7bFaction \u00A78« \u00A77";
    private Main plugin;
    private Economy economy;
    private Set<UUID> factionChatPlayers = new HashSet<>();
    private final FactionChat factionChat;

    public FactionCommand(FactionChat factionChat, FactionManager factionManager, HashMap<UUID, Boolean> waitingPlayers, Main plugin, Economy economy) {
        this.factionManager = factionManager;
        this.waitingPlayers = waitingPlayers;
        this.plugin = plugin;
        this.economy = economy;
        this.factionChat = factionChat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(logo + "§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage(logo + "Usage: /f help");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "kick":
                if (args.length < 2) {
                    player.sendMessage(logo + "§cYou need to provide a player name.");
                    return true;
                }
                Faction faction1 = factionManager.getFactionByPlayer(playerUUID);
                if (args.length >= 3){
                    if (factionManager.factionExists(args[2])) {
                        faction1=factionManager.getFactionByName(args[2]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        faction1 = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou are not in a faction.");
                        return true;
                    }
                }
                if(!factionManager.hasPermissionInFaction(playerUUID,faction1,Permission.KICK)){
                    player.sendMessage(logo + "§cYou don't have permission to perform this action.");
                    return true;
                }
                //TODO : peut etre empécher que l'on puisse kick qqun au dessus de soit : dans ces conditions faire un truc a la FM.canPromote
                /*if (!factionManager.isFactionLeader(playerUUID)) {
                    player.sendMessage(logo + "§cYou need to be leader of the faction.");
                    return true;
                }*/
                Player target = Bukkit.getPlayer(args[1]);
                if(playerUUID==target.getUniqueId()){
                    player.sendMessage(logo + "§cUse /f leave to leave your faction, you can't kick yourself.");
                }
                if(factionManager.isFactionLeader(target.getUniqueId())){ //cas où d'autre que le leader peuvent kick
                    player.sendMessage(logo + "§cYou can't kick the leader.");
                }
                factionManager.removePlayerFromFaction(target.getUniqueId());
                target.sendMessage(logo+"§cYou have been kicked from your faction by §4"+player.getName());
                for (UUID memberUUID : faction1.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(logo + "§c"+target.getName()+" §7as been kicked from your faction by §c"+player.getName());
                    }
                }
                break;
            case "unclaim":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are not in a faction.");
                    return true;
                }
                Faction facPlayer = factionManager.getFactionByPlayer(playerUUID);
                Location ploc = player.getLocation();
                Chunk unclaim = ploc.getChunk();
                if (factionManager.hasPermissionInFaction(playerUUID, facPlayer, Permission.CLAIM) && factionManager.getFactionByChunk(unclaim) == facPlayer) {
                    factionManager.unclaimLand(unclaim);
                    player.sendMessage(logo + "§7Chunk UnClaimed.");
                    return true;
                }
                player.sendMessage(logo + "§cYou are not in a claimed land.");
                break;
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
                    UUID factionUUID = UUID.randomUUID();
                    Faction newFaction = new Faction(factionName, playerUUID, factionUUID);
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
                Faction faction2 = factionManager.getFactionByPlayer(playerUUID);
                List<Chunk> claimedChunks = factionManager.getClaimedChunks(faction2);
                if (!claimedChunks.isEmpty()) {
                    boolean isAdjacent = false;
                    for (Chunk claimedChunk : claimedChunks) {
                        if (isAdjacentChunk(claimedChunk, chunkToClaim)) {
                            isAdjacent = true;
                            break;
                        }
                    }

                    if (!isAdjacent) {
                        player.sendMessage(logo + "\u00A7cYou can only claim land adjacent to your existing claims.");
                        return true;
                    }
                }
                int claims = claimedChunks.size();
                int level = faction2.getLevel();
                for (int i = 1; i <= 20; i += 2) {
                    int maxClaims = (i == 1) ? 4 : (i + 2);
                    if (level < i && claims >= maxClaims) {
                        player.sendMessage("§cYou can't claim more than §4" + maxClaims + " §cchunks. §8(more faction level required)");
                        return true;
                    }
                }
                if (level == 20 && claims >= 20) {
                    player.sendMessage("§cYou can't claim more than 20 chunks.");
                    return true;
                }
                factionManager.claimLand(chunkToClaim, faction2);
                player.sendMessage(logo + "\u00A77Land claimed for your faction!");
                break;


            case "list":
                if (factionManager.getAllFactions() == null){
                    player.sendMessage(logo + "No faction found.");
                } else {
                    player.sendMessage(logo + "Factions list: ");
                    factionManager.getAllFactions().forEach(faction ->
                            player.sendMessage("§fFaction: §b" + faction.getName() + "§f - Members: §b" + faction.getMembers().size()));
                }
                break;

            case "home":
                if (waitingPlayers.containsKey(playerUUID)) {
                    player.sendMessage(logo + "§cYou are already teleporting. Please wait and don't move.");
                    return true;
                }
                Faction faction3;
                if (args.length >= 2){
                    if (factionManager.factionExists(args[1])) {
                        faction3=factionManager.getFactionByName(args[1]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        faction3 = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou are not in a faction.");
                        return true;
                    }
                }
                if(!factionManager.hasPermissionInFaction(playerUUID,faction3,Permission.HOME)){
                    player.sendMessage(logo + "§cYou don't have permission to perform this action.");
                    return true;
                }
                Location home = faction.getHomeLocation();
                if (home != null) {
                    Chunk chunk = home.getChunk();
                    FactionManager fm = new FactionManager();
                    if (!fm.isLandClaimed(chunk) && fm.getFactionByChunk(chunk) != faction) {
                        player.sendMessage(logo + "§cYour faction home as been removed since it isnt on your claimed land.");
                        faction.removeHomeLocation();
                        return true;
                    }
                }
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
                    player.sendMessage(logo + "\u00A7cThe faction does not have a home set.");
                }
                break;

            case "sethome":
                Faction faction4;
                if (args.length >= 2){
                    if (factionManager.factionExists(args[1])) {
                        faction4=factionManager.getFactionByName(args[1]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        faction4 = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou must be in a faction to set a home.");
                        return true;
                    }
                }
                if(!factionManager.hasPermissionInFaction(playerUUID,faction4,Permission.SETHOME)){
                    player.sendMessage(logo + "§cYou don't have permission to perform this action.");
                    return true;
                }
                Chunk playerChunk = player.getLocation().getChunk();
                Faction chunkOwner = factionManager.getFactionByChunk(playerChunk);
                if (!faction4.equals(chunkOwner)) {
                    player.sendMessage(logo + "§cYou can only set home in your faction's claimed land.");
                    return true;
                }

                faction4.setHomeLocation(player.getLocation());
                player.sendMessage(logo + "§7Faction Home set §asuccessfully!");
                break;

            case "name":
                if (args.length < 2) {
                    player.sendMessage(logo + "\u00A7cPlease provide a new faction name.");
                    return true;
                }
                Faction faction5;
                if (args.length >= 3){
                    if (factionManager.factionExists(args[2])) {
                        faction5=factionManager.getFactionByName(args[2]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        faction5 = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou must be in a faction to rename it.");
                        return true;
                    }
                }
                if(!factionManager.hasPermissionInFaction(playerUUID,faction5,Permission.RENAME)){
                    player.sendMessage(logo + "§cYou don't have permission to perform this action.");
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

                String oldName = faction5.getName();
                factionManager.setName(faction5, newName);
                Bukkit.broadcastMessage(logo + "\u00A77The faction \u00A7b" + oldName + " \u00A77has changed its name to \u00A7b" + newName + "\u00A77!");
                break;



            case "invite":
                if (args.length < 2) {
                    player.sendMessage(logo + "§7Please provide the name of the player to invite.");
                    return true;
                }
                Faction faction6;
                if (args.length >= 3){
                    if (factionManager.factionExists(args[2])) {
                        faction6=factionManager.getFactionByName(args[2]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        faction6 = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou are not in a faction.");
                        return true;
                    }
                }
                if(!factionManager.hasPermissionInFaction(playerUUID,faction6,Permission.INVITE)){
                    player.sendMessage(logo + "§cYou don't have permission to perform this action.");
                    return true;
                }
                Player targett = Bukkit.getPlayer(args[1]);
                Faction factionTarget1 = factionManager.getFactionByPlayer(targett.getUniqueId());
                if (targett == null) {
                    player.sendMessage(logo + "§7That player is not online.");
                    return true;
                }
                if (player == targett) {
                    player.sendMessage(logo+"§cYou can't invite yourself lmao");
                    return true;
                }
                if (factionTarget1 == faction6) {
                    player.sendMessage(logo + "§cThis player is already on your faction");
                    return true;
                }
                if (factionTarget1 != null) {
                    player.sendMessage(logo+"§cThis player is already on a faction");
                    return true;
                }
                UUID factionUUID = faction6.getFactionUUID();
                factionManager.invitePlayerToFaction(targett.getUniqueId(), factionUUID);
                player.sendMessage(logo + "§7Player §f" + targett.getName() + " §7invited to the faction.");
                targett.sendMessage(logo + "§7The faction §f" + faction6.getName() + " §7has invited you, §f/f join");
                break;


            case "join":
                if (factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are already in a faction.");
                    return true;
                }
                UUID invitedFactionUUID = factionManager.getInvitedFaction(playerUUID);
                if (invitedFactionUUID == null) {
                    player.sendMessage(logo + "§cYou are not invited to any faction.");
                    return true;
                }
                Faction faction7 = factionManager.getFactionByUUID(invitedFactionUUID);
                if (faction7 == null) {
                    player.sendMessage(logo + "§cThis faction doesn't exist. §7Debug: " + invitedFactionUUID);
                    return true;
                }
                factionManager.addMemberToFaction(playerUUID, invitedFactionUUID, Rank.RECRUE);
                player.sendMessage(logo + "§7You joined: §c" + faction7.getName());

                for (UUID memberUUID : faction7.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(logo + "§a" + player.getName() + " §7joined your faction.");
                    }
                }
                break;

            case "promote":
                Faction faction8 = factionManager.getFactionByPlayer(playerUUID);
                if (faction8 == null) {
                    player.sendMessage(logo + "§cYou are not in any faction.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(logo + "§cPlease provide the name of the player to promote.");
                    return true;
                }
                Player promoteTarget = Bukkit.getPlayer(args[1]);
                Faction factionTarget = factionManager.getFactionByPlayer(promoteTarget.getUniqueId());
                if (factionTarget != faction8) {
                    player.sendMessage(logo + "§cThat player is not on your faction.");
                    return true;
                }
                if (promoteTarget == null) {
                    player.sendMessage(logo + "§cThat player is not online.");
                    return true;
                }
                if (!factionManager.canPromote(promoteTarget.getUniqueId(),faction8,playerUUID)) {
                    player.sendMessage(logo + "§cYou don't have permission to demote this player.");
                    //DONE avec le /f perm pouvoir avoir la perm de promote ou demote suivant le rank
                    return true;
                }
                factionManager.promoteMember(promoteTarget.getUniqueId(), factionTarget, playerUUID);
                player.sendMessage(logo + "Player " + promoteTarget.getName() + " promoted!");
                for (UUID memberUUID : faction8.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(logo + "§a" + promoteTarget.getName() + " §7as been promoted to §f"+ faction8.getRank(promoteTarget.getUniqueId()));
                    }
                }
                break;

            case "demote":
                Faction faction9 = factionManager.getFactionByPlayer(playerUUID);
                if (faction9 == null) {
                    player.sendMessage(logo + "§cYou are not in any faction.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(logo + "§cPlease provide the name of the player to promote.");
                    return true;
                }
                Player demoteTarget = Bukkit.getPlayer(args[1]);
                if (demoteTarget == null) {
                    player.sendMessage(logo + "§cThat player is not online.");
                    return true;
                }
                Faction factionTargetdemote = factionManager.getFactionByPlayer(demoteTarget.getUniqueId());
                if (factionTargetdemote != faction9) {
                    player.sendMessage(logo + "§cThat player is not on your faction.");
                    return true;
                }
                if (player == demoteTarget) {
                    player.sendMessage(logo + "§cYou can't demote yourself, promote someone to leader instead.");
                    return true;
                }
                if (!factionManager.canPromote(demoteTarget.getUniqueId(),faction9,playerUUID)) {
                    player.sendMessage(logo + "§cYou don't have permission to demote this player.");
                    //DONE avec le /f perm pouvoir avoir la perm de promote ou demote suivant le rank
                    return true;
                }
                factionManager.demoteMember(demoteTarget.getUniqueId(), factionManager.getFactionByPlayer(playerUUID));
                player.sendMessage(logo + "Player " + demoteTarget.getName() + " demoted!");
                for (UUID memberUUID : faction9.getMembers().keySet()) {
                    Player member = Bukkit.getPlayer(memberUUID);
                    if (member != null) {
                        member.sendMessage(logo + "§c" + demoteTarget.getName() + " §7as been demoted to §f"+ faction9.getRank(demoteTarget.getUniqueId()));
                    }
                }
                break;
            case "chat":
                Faction fac = factionManager.getFactionByPlayer(playerUUID);
                if (fac != null) {
                    factionChat.toggleFactionChat(player);
                    return true;
                }
                else {
                    player.sendMessage(logo + "§cYou are not in a faction.");
                    return true;
                }
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
                player.sendMessage("\u00A78– \u00A77/f menu : §fFaction Menu");
                break;
            case "leave":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are not in a faction.");
                    return true;
                }

               Faction factionA = factionManager.getFactionByPlayer(playerUUID);

                if (factionA.getLeaderUUID().equals(playerUUID)) {
                    player.sendMessage(logo + "§cYou are the leader of this faction. §fUse §b/f disband §fto disband your faction.");
                } else {
                    factionManager.removePlayerFromFaction(playerUUID);
                    player.sendMessage(logo + "§fYou leaved the faction §b" + factionA.getName() + ".");
                    for (UUID memberUUID : factionA.getMembers().keySet()) {
                        Player member = Bukkit.getPlayer(memberUUID);
                        if (member != null) {
                            member.sendMessage(logo + "§c" + player.getName() + " §7left your faction.");
                        }
                    }
                    return true;
                }

                break;
            case "menu":
                if (!factionManager.isFactionMember(playerUUID)) {
                    player.sendMessage(logo + "§cYou are not in a faction.");
                    return true;
                }
                FactionGUI factionGUI = new FactionGUI(factionManager);
                factionGUI.openFactionMenu(player);
                break;
            case "f":
                Faction factionB;
                boolean detailed=false;
                if (args.length >= 2){
                    if (factionManager.factionExists(args[1])) {
                        factionB=factionManager.getFactionByName(args[1]);
                    }else{
                        player.sendMessage(logo + "§cThis faction don't exist.");
                        return true;
                    }
                }else{
                    if (factionManager.isFactionMember(playerUUID)) {
                        detailed=true;
                        factionB = factionManager.getFactionByPlayer(playerUUID);
                    }else{
                        player.sendMessage(logo + "§cYou are not in a faction.");
                        return true;
                    }
                }

                player.sendMessage(logo+ "\u00A7fFaction Info:");
                player.sendMessage("\u00A78- \u00A77Faction Name: \u00A7f" + factionB.getName());
                player.sendMessage("\u00A78- \u00A77Members Online: \u00A7f" + factionB.getOnlineMembers().size() +"§7/§f"+ factionB.getMembers().size());
                if(detailed){
                    player.sendMessage("\u00A78- \u00A77Faction Level: \u00A7f" + factionB.getLevel());
                    player.sendMessage("\u00A78- \u00A77Faction Exp: \u00A7f" + factionB.getExp());
                    if (factionB.getHomeLocation() != null) {
                        player.sendMessage("\u00A78- \u00A77Home Location: \u00A78X: \u00A77" + (int) factionB.getHomeLocation().getX() + " \u00A78Y: \u00A77" + (int) factionB.getHomeLocation().getY() + " \u00A78Z: \u00A77" + (int) factionB.getHomeLocation().getZ());
                    }
                    else {
                        player.sendMessage("\u00A78- \u00A77Home: \u00A7fNot Set.");
                    }
                    player.sendMessage("\u00A78- §7Progression: "+getProgressBar(factionB.getExp(), factionB.getExperienceNeededForNextLevel()));
                }
                player.sendMessage("\u00A78- §7Members list:");
                for (Map.Entry<UUID, Rank> entry : factionB.getMembers().entrySet()) {
                    UUID memberUUID = entry.getKey();
                    Player member = Bukkit.getPlayer(memberUUID);
                    player.sendMessage(" §7- §b"+member.getName());
                }
                break;
            case "perm":
                Faction faction = factionManager.getFactionByPlayer(playerUUID);
                if (faction == null) {
                    player.sendMessage(logo + "§cYou are not in any faction.");
                    return true;
                }
                if(args.length<2){
                    player.sendMessage(logo + "§cUsage: §f/f perm (set|show)");
                    return true;
                }
                switch (args[1].toLowerCase()){
                    case "show":
                        String message="";
                        for(PermRank rank : PermRank.values()){
                            message+=rank.getAbr()+" ";
                        }
                        player.sendMessage(message);
                        for(Permission perm : Permission.values()){
                            message="";
                            for (PermRank rank : PermRank.values()){
                                if(faction.hasPermission(rank,perm)){
                                    message+="§2YES ";
                                }else{
                                    message+="§4NO ";
                                }
                            }
                            message+="§r"+perm.name();
                            player.sendMessage(message);
                        }
                        break;
                    case "set":
                        if (!faction.getLeaderUUID().equals(playerUUID)) {
                            player.sendMessage(logo + "\u00A7cOnly the leader can change the permissions.");
                            return true;
                        }
                        if(args.length<5){
                            player.sendMessage(logo + "§cUsage: §f/f perm set §7(OFFICER|MEMBER|RECRUE|ALLY|NEUTRAL) [permission] §7(§aYes§7|§cNo§7)");
                            return true;
                        }
                        PermRank rank=PermRank.fromString(args[2]);
                        if(rank==null){
                            player.sendMessage(logo + "§cUnknown rank. §fRank must be OFFICER|MEMBER|RECRUE|ALLY|NEUTRAL.");
                            return true;
                        }
                        Permission perm=Permission.fromString(args[3]);
                        if(perm==null){
                            String permlist="";
                            for(Permission p : Permission.values()){
                                permlist+=p.name()+",";
                            }
                            player.sendMessage(logo + "Unknown permission. Permisson must be "+permlist.substring(0, permlist.length()-1)+".");
                            return true;
                        }
                        switch (args[4].toLowerCase()){
                            case "yes":
                                faction.setPermission(rank,perm,true);
                                player.sendMessage(logo+"§7Permission of §f"+rank+"§7 for §f"+perm+" §7as been updated to §aYES");
                                break;
                            case "no":
                                faction.setPermission(rank,perm,false);
                                player.sendMessage(logo+"§7Permission of §f"+rank+"§7 for §f"+perm+" §7as been updated to §cNO");
                                break;
                            default:
                                player.sendMessage(logo + "§cYou must set the permission to yes or no.");
                        }
                        break;
                    default:
                        player.sendMessage(logo + "§7Unknown subcommand. §cUsage: §f/f perm (set|show)");
                }
                break;
//            case "allfactions":
//                if (factionManager.getAllFactions().isEmpty()) {
//                    player.sendMessage(logo + "No factions available.");
//                    return true;
//                }
//                player.sendMessage(logo + "\u00A7fAll Factions Info:");
//                for (Faction factionn : factionManager.getAllFactions()) {
//                    UUID factionUUIDD = factionn.getFactionUUID();
//                    player.sendMessage("\u00A78- \u00A77Faction UUID: \u00A7f" + factionUUIDD.toString());
//
//                    String factionNamee = factionn.getName();
//                    if (factionNamee != null) {
//                        player.sendMessage("\u00A78- \u00A77Faction Name: \u00A7f" + factionNamee);
//                    } else {
//                        player.sendMessage("\u00A78- \u00A77Faction Name: \u00A7cNot Set.");
//                    }
//
//                    UUID leaderUUID = factionn.getLeaderUUID();
//                    if (leaderUUID != null) {
//                        player.sendMessage("\u00A78- \u00A77Faction Leader: \u00A7f" + leaderUUID.toString());
//                    } else {
//                        player.sendMessage("\u00A78- \u00A77Faction Leader: \u00A7cNot Set.");
//                    }
//
//                    Map<UUID, Rank> members = factionn.getMembers();
//                    if (members != null) {
//                        player.sendMessage("\u00A78- \u00A77Members Count: \u00A7f" + members.size());
//                        for (UUID memberUUID : members.keySet()) {
//                            player.sendMessage("\u00A78- \u00A77Member UUID: \u00A7f" + memberUUID.toString());
//                        }
//                    } else {
//                        player.sendMessage("\u00A78- \u00A77Members: \u00A7cNot Set.");
//                    }
//
//                    Location homeLocation = factionn.getHomeLocation();
//                    if (homeLocation != null) {
//                        player.sendMessage("\u00A78- \u00A77Home Location: \u00A78X: \u00A77" + (int) homeLocation.getX() + " \u00A78Y: \u00A77" + (int) homeLocation.getY() + " \u00A78Z: \u00A77" + (int) homeLocation.getZ());
//                    } else {
//                        player.sendMessage("\u00A78- \u00A77Home Location: \u00A7cNot Set.");
//                    }
//
//                    int level = factionn.getLevel();
//                    player.sendMessage("\u00A78- \u00A77Faction Level: \u00A7f" + level);
//
//                    int exp = factionn.getExp();
//                    player.sendMessage("\u00A78- \u00A77Faction Exp: \u00A7f" + exp);
//
//                    player.sendMessage("");
//                }
//                break;
            default:
                player.sendMessage(logo + "Unknown command. Usage: /f help");
                break;
        }
        return true;
    }
    private boolean isAdjacentChunk(Chunk chunk1, Chunk chunk2) {
        int xDiff = Math.abs(chunk1.getX() - chunk2.getX());
        int zDiff = Math.abs(chunk1.getZ() - chunk2.getZ());
        return (xDiff == 1 && zDiff == 0) || (xDiff == 0 && zDiff == 1);
    }
    public String getProgressBar(int current, int max) {
        StringBuilder progressBar = new StringBuilder("§7[§f");
        int progressLength = Math.min(current, max);
        for (int i = 0; i < progressLength; i++) {
            progressBar.append("§c|§f");
        }
        for (int i = progressLength; i < 27; i++) {
            progressBar.append("|§f");
        }
        progressBar.append("§7]");
        return progressBar.toString();
    }
}

