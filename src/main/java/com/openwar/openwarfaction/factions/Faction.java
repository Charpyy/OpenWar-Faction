package com.openwar.openwarfaction.factions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.openwar.openwarfaction.factions.Permission;
import com.openwar.openwarfaction.factions.PermRank;

import java.util.*;

public class Faction {


    private UUID factionUUID;
    private String name;
    private UUID leaderUUID;
    private Map<UUID, Rank> members;
    private Set<UUID> allys;
    private Location homeLocation;
    private int level;
    private int exp;
    private int raidPoint;
    private int maxRaidPoint;
    private int[] permissions;
    private String logo = "\u00A78» \u00A7bFaction \u00A78« \u00A77";

    private static final int[] levelRequirements = {
            564, 1470, 3625, 7030, 11684, 17587,
            24739, 33141, 42791, 53692, 65841,
            79239, 93887, 109784, 126931, 145326,
            164971, 185865, 208009, 231401
    };

    public Faction(String name, UUID leaderUUID, UUID factionUUID) {
        this.factionUUID = factionUUID;
        this.name = name;
        this.level = 0;
        this.exp = 0;
        this.raidPoint = 5;
        this.leaderUUID = leaderUUID;
        this.members = new HashMap<>();
        this.members.put(leaderUUID, Rank.LEADER);
        this.permissions=new int[]{0b0111111011110,0b0010111000010,0b0000110000010,0b0000100000010,0b000000000000};
        this.maxRaidPoint = maxRaidPoint();
    }
    public UUID getFactionUUID() {
        return factionUUID;
    }
    public int getMaxRaidPoint() {
        return this.maxRaidPoint;
    }
    public int maxRaidPoint() {
        int max = 2;
        int lvl = this.level;
        if (lvl >= 3) {
            max += 1;
        }
        if (lvl >= 9) {
            max += 1;
        }
        if (lvl >= 12) {
            max += 2;
        }
        if (lvl >= 15) {
            max += 3;
        }
        if (lvl >= 20) {
            max += 1;
        }
        if (this.getMembers().size() >= 3) {
            max += 3;
        }
        if (this.getMembers().size() >= 5) {
            max += 3;
        }
      return max;
    }
    public int getExperienceNeededForNextLevel() {
        if (level < levelRequirements.length) {
            return levelRequirements[level];
        }
        return 0;
    }
    public void addExp(int amount) {
        this.exp += amount;
        while (level < levelRequirements.length && this.exp >= levelRequirements[level]) {
            levelUp();
        }
    }
    public void setRaidPoint(int pa) {
        this.raidPoint = pa;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    public void setLevel(int lvl) {
        level = lvl;
    }

    private void levelUp() {
        level++;
        this.exp = 0;
        this.maxRaidPoint = maxRaidPoint();
        for (UUID memberUUID : members.keySet()) {
            Player player = Bukkit.getPlayer(memberUUID);
            if (player != null && player.isOnline()) {
                player.sendMessage(logo+"§3§k§l!!!§r §fYour faction is now level §6§l"+ level +" §3§k§l!!!");
            }
        }
    }

    public int getRaidPoint() {return raidPoint;}
    public void removeFactionPoint(int ra) {
        this.raidPoint -= ra;
    }
    public int getLevel() {
        return level;
    }
    public int getExp() {
        return exp;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLeaderUUID() {
        return leaderUUID;
    }

    public Map<UUID, Rank> getMembers() {
        return members;
    }
    public boolean isMember(UUID playerUUID){
        return members.containsKey(playerUUID);
    }
    public boolean isAlly(UUID allyTeam){
        return allys.contains(allyTeam);
    }
    public void addMember(UUID playerUUID) {
        this.maxRaidPoint = maxRaidPoint();
        members.put(playerUUID, Rank.RECRUE);
    }
    public void addMemberRank(UUID memberUUID, Rank rank) {
        this.maxRaidPoint = maxRaidPoint();
        members.put(memberUUID, rank);
    }
    public void addAlly(UUID allyTeam){
        allys.add(allyTeam);
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<>();
        for (UUID memberUUID : members.keySet()) {
            Player player = Bukkit.getPlayer(memberUUID);
            if (player != null && player.isOnline()) {
                onlineMembers.add(player);
            }
        }
        return onlineMembers;
    }
    public void removeMember(UUID playerUUID) {
        this.maxRaidPoint = maxRaidPoint();
        members.remove(playerUUID);
    }
    public void removeAlly(UUID allyTeam){
        allys.remove(allyTeam);
    }
    public void setRank(UUID playerUUID, Rank rank) {
        if (members.containsKey(playerUUID)) {
            members.put(playerUUID, rank);
        }
    }

    public Rank getRank(UUID playerUUID) {
        return members.get(playerUUID);
    }


    public void promoteMember(UUID target) {
        Rank currentRank = members.get(target);
        if (currentRank == Rank.RECRUE) {
            members.put(target, Rank.MEMBER);
        } else if (currentRank == Rank.MEMBER) {
            members.put(target, Rank.OFFICER);
        } else if (currentRank == Rank.OFFICER) {
            UUID uuid = getLeaderUUID();
            members.put(target, Rank.LEADER);
            members.put(uuid, Rank.OFFICER);
            this.leaderUUID = target;
        }
    }


    public void demoteMember(UUID playerUUID) {
        Rank currentRank = members.get(playerUUID);
        if (currentRank == Rank.LEADER) {
            members.put(playerUUID, Rank.OFFICER);
        } else if (currentRank == Rank.OFFICER) {
            members.put(playerUUID, Rank.MEMBER);
        } else if (currentRank == Rank.MEMBER) {
            members.put(playerUUID, Rank.RECRUE);
        }
    }
    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }
    public void removeHomeLocation() {
        this.homeLocation = null;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }
    public boolean hasPermission(PermRank rank,Permission perm){
        return (this.permissions[rank.getOrder()] & perm.getFlag()) !=0;
    }
    public void setPermission(PermRank rank,Permission perm,boolean set){
        if(set){
            this.permissions[rank.getOrder()]|=perm.getFlag();
        }else{
            this.permissions[rank.getOrder()]&=~perm.getFlag();
        }
    }
    public int[] getPermissions(){
        return this.permissions;
    }
    public void setPermissions(int[] permissions){
        this.permissions=permissions;
    }
}
