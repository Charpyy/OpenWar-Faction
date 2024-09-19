package com.openwar.openwarfaction.factions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Faction {


    private UUID factionUUID;
    private String name;
    private UUID leaderUUID;
    private Map<UUID, Rank> members;
    private Location homeLocation;
    private int level;
    private int exp;
    private int[] permissions;

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
        this.leaderUUID = leaderUUID;
        this.members = new HashMap<>();
        this.members.put(leaderUUID, Rank.LEADER);
        this.permissions={0b1111011110,0b0111000010,0b0110000010,0b0100000010,0b0000000000};
    }
    public UUID getFactionUUID() {
        return factionUUID;
    }

    public int getExperienceNeededForNextLevel() {
        if (level < levelRequirements.length) {
            return levelRequirements[level];
        }
        return 0;
    }
    public void addExp(int amount) {
        exp += amount;
        while (level < levelRequirements.length && exp >= levelRequirements[level]) {
            levelUp();
        }
    }
    public void setExp(int exp) {
        exp = exp;
    }
    public void setLevel(int lvl) {
        level = lvl;
    }

    private void levelUp() {
        level++;
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
    public void addMember(UUID playerUUID) {
        members.put(playerUUID, Rank.RECRUE);
    }
    public void addMemberRank(UUID memberUUID, Rank rank) {
        members.put(memberUUID, rank);
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
        members.remove(playerUUID);
    }

    public void setRank(UUID playerUUID, Rank rank) {
        if (members.containsKey(playerUUID)) {
            members.put(playerUUID, rank);
        }
    }

    public Rank getRank(UUID playerUUID) {
        return members.get(playerUUID);
    }

    public void promoteMember(UUID target, UUID playerUUID) {
        Rank currentRank = members.get(target);
        Rank playerRank = members.get(playerUUID);

        if (playerRank == Rank.LEADER) {
            if (currentRank == Rank.RECRUE) {
                members.put(target, Rank.MEMBER);
            } else if (currentRank == Rank.MEMBER) {
                members.put(target, Rank.OFFICER);
            } else if (currentRank == Rank.OFFICER) {
                members.put(target, Rank.LEADER);
                members.put(playerUUID, Rank.OFFICER);
                this.leaderUUID = target;
            }
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

    public Location getHomeLocation() {
        return homeLocation;
    }
    public boolean hasPermission(PermRank rank,Permission perm){
        return this.permissions[rank.getOrder()]&perm.getFlag()!=0;
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
