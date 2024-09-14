package com.openwar.openwarfaction.factions;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Faction {

    private String name;
    private UUID leaderUUID;
    private Map<UUID, Rank> members;
    private Location homeLocation;

    public Faction(String name, UUID leaderUUID) {
        this.name = name;
        this.leaderUUID = leaderUUID;
        this.members = new HashMap<>();
        this.members.put(leaderUUID, Rank.LEADER);
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
