package com.openwar.openwarfaction.factions;

import com.openwar.openwarfaction.handler.ClaimChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

public class FactionManager {
    private Map<UUID, Faction> factions;
    private Map<String, UUID> factionUUIDs;
    private Map<UUID, UUID> playerFactions;
    private Map<Chunk, Faction> claimedLand;
    private Map<UUID, String> invitations;

    public FactionManager() {
        this.factions = new HashMap<>();
        this.factionUUIDs = new HashMap<>();
        this.playerFactions = new HashMap<>();
        this.claimedLand = new HashMap<>();
        this.invitations = new HashMap<>();
    }

    public void saveFactionsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("factionUUID,factionName,leaderUUID,members,homeLocation,level,exp\n");
            for (UUID factionUUID : factions.keySet()) {
                Faction faction = factions.get(factionUUID);
                writer.append(factionUUID.toString()).append(",")
                        .append(faction.getName()).append(",")
                        .append(faction.getLeaderUUID().toString()).append(",");
                StringBuilder members = new StringBuilder();
                for (UUID memberUUID : faction.getMembers().keySet()) {
                    members.append(memberUUID.toString()).append(";");
                }
                writer.append(members.toString()).append(",");
                Location home = faction.getHomeLocation();
                if (home != null) {
                    writer.append(home.getWorld().getName()).append(",")
                            .append(Double.toString(home.getX())).append(",")
                            .append(Double.toString(home.getY())).append(",")
                            .append(Double.toString(home.getZ())).append(",");
                } else {
                    writer.append("null,null,null,null,");
                }
                writer.append(String.valueOf(faction.getLevel())).append(",")
                        .append(String.valueOf(faction.getExp())).append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFactionsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                UUID factionUUID = UUID.fromString(data[0]);
                String factionName = data[1];
                UUID leaderUUID = UUID.fromString(data[2]);
                Map<UUID, Rank> members = new HashMap<>();
                String[] memberUUIDs = data[3].split(";");
                for (String memberUUID : memberUUIDs) {
                    if (!memberUUID.isEmpty()) {
                        members.put(UUID.fromString(memberUUID), Rank.MEMBER);
                    }
                }
                Location homeLocation = null;
                if (!data[4].equals("null")) {
                    World world = Bukkit.getWorld(data[4]);
                    double x = Double.parseDouble(data[5]);
                    double y = Double.parseDouble(data[6]);
                    double z = Double.parseDouble(data[7]);
                    homeLocation = new Location(world, x, y, z);
                }
                int level = Integer.parseInt(data[8]);
                int exp = Integer.parseInt(data[9]);

                Faction faction = new Faction(factionName, leaderUUID);
                faction.setHomeLocation(homeLocation);
                faction.setLevel(level);
                faction.setExp(exp);
                factions.put(factionUUID, faction);
                playerFactions.put(faction.getLeaderUUID(), faction.getFactionUUID());
                for (UUID memberUUID : members.keySet()) {
                    faction.addMember(memberUUID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveClaimsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("chunkX,chunkZ,factionName\n");
            for (Map.Entry<Chunk, Faction> entry : claimedLand.entrySet()) {
                Chunk chunk = entry.getKey();
                Faction faction = entry.getValue();
                writer.append(chunk.getX() + "," + chunk.getZ() + "," + faction.getName() + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadClaimsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int chunkX = Integer.parseInt(data[0]);
                int chunkZ = Integer.parseInt(data[1]);
                String factionName = data[2];
                Chunk chunk = Bukkit.getWorld("world").getChunkAt(chunkX, chunkZ);
                Faction faction = getFactionByName(factionName);
                if (faction != null) {
                    claimedLand.put(chunk, faction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFaction(Faction faction) {
        UUID factionId = faction.getFactionUUID();
        factions.put(factionId, faction);
        factionUUIDs.put(faction.getName(), factionId);
        playerFactions.put(faction.getLeaderUUID(), factionId);
    }

    public void deleteFaction(Faction faction) {
        UUID factionId = faction.getFactionUUID();
        factions.remove(factionId);
        factionUUIDs.remove(faction.getName());
        for (UUID memberUUID : faction.getMembers().keySet()) {
            playerFactions.remove(memberUUID);
        }
        claimedLand.entrySet().removeIf(entry -> entry.getValue().equals(faction));
    }

    public boolean factionExists(String name) {
        return factionUUIDs.containsKey(name);
    }

    public Faction getFactionByName(String name) {
        UUID factionId = factionUUIDs.get(name);
        if (factionId != null) {
            return factions.get(factionId);
        }
        return null;
    }

    public Faction getFactionByPlayer(UUID playerUUID) {
        UUID factionUUID = playerFactions.get(playerUUID);
        if (factionUUID != null) {
            return factions.get(factionUUID);
        }
        return null;
    }

    public boolean isFactionMember(UUID playerUUID) {
        return playerFactions.containsKey(playerUUID);
    }

    public boolean isFactionLeader(UUID playerUUID) {
        Faction faction = getFactionByPlayer(playerUUID);
        return faction != null && faction.getLeaderUUID().equals(playerUUID);
    }

    public List<Faction> getAllFactions() {
        return new ArrayList<>(factions.values());
    }

    public void invitePlayerToFaction(UUID playerUUID, Faction faction) {
        invitations.put(playerUUID, faction.getName());
    }

    public void promoteMember(UUID playerUUID, Faction faction) {
        faction.promoteMember(playerUUID);
    }

    public void claimLand(Chunk chunk, Faction faction) {
        if (!isLandClaimed(chunk)) {
            claimedLand.put(chunk, faction);
        }
    }

    public String getInvitedFaction(UUID playerUUID) {
        return invitations.get(playerUUID);
    }

    public void removePlayerFromFaction(UUID playerUUID) {
        UUID factionUUID = playerFactions.remove(playerUUID);
        if (factionUUID != null) {
            Faction faction = factions.get(factionUUID);
            if (faction != null) {
                faction.removeMember(playerUUID);
            }
        }
    }

    public void addMemberToFaction(UUID playerUUID, Faction faction) {
        faction.addMember(playerUUID);
        playerFactions.put(playerUUID, faction.getFactionUUID());
        invitations.remove(playerUUID);
    }

    public boolean isLandClaimed(Chunk chunk) {
        return claimedLand.containsKey(chunk);
    }

    public Faction getFactionByChunk(Chunk chunk) {
        return claimedLand.get(chunk);
    }

    public void unclaimLand(Chunk chunk) {
        claimedLand.remove(chunk);
    }

    public List<Chunk> getClaimedChunks(Faction faction) {
        List<Chunk> claimedChunks = new ArrayList<>();
        for (Map.Entry<Chunk, Faction> entry : claimedLand.entrySet()) {
            if (entry.getValue().equals(faction)) {
                claimedChunks.add(entry.getKey());
            }
        }
        return claimedChunks;
    }
}
