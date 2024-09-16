package com.openwar.openwarfaction.factions;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Faction {


    private UUID factionUUID;
    private String name;
    private UUID leaderUUID;
    private Map<UUID, Rank> members;
    private Location homeLocation;
    private int level;
    private int exp;

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

    public void addMember(UUID playerUUID) {
        members.put(playerUUID, Rank.RECRUE);
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

    public void promoteMember(UUID playerUUID) {
        Rank currentRank = members.get(playerUUID);
        if (currentRank == Rank.RECRUE) {
            members.put(playerUUID, Rank.MEMBER);
        } else if (currentRank == Rank.MEMBER) {
            members.put(playerUUID, Rank.OFFICER);
        } else if (currentRank == Rank.OFFICER) {
            members.put(playerUUID, Rank.LEADER);
            this.leaderUUID = playerUUID;
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
}
