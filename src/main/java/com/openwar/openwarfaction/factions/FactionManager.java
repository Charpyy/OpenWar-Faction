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

    private Map<String, Faction> factions;
    private Map<UUID, String> playerFactions;
    private Map<Chunk, Faction> claimedLand;
    private Map<UUID, String> invitations = new HashMap<>();

    public FactionManager() {
        this.factions = new HashMap<>();
        this.playerFactions = new HashMap<>();
        this.claimedLand = new HashMap<>();
    }

    public void saveFactionsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("factionName,leaderUUID,members,homeLocation,level,exp\n");
            for (Faction faction : factions.values()) {
                writer.append(faction.getName()).append(",")
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
            String line = reader.readLine(); // Ignore l'en-tête
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String factionName = data[0];
                UUID leaderUUID = UUID.fromString(data[1]);
                Map<UUID, Rank> members = new HashMap<>();
                String[] memberUUIDs = data[2].split(";");
                for (String memberUUID : memberUUIDs) {
                    if (!memberUUID.isEmpty()) {
                        members.put(UUID.fromString(memberUUID), Rank.MEMBER);
                    }
                }
                Location homeLocation = null;
                if (!data[3].equals("null")) {
                    World world = Bukkit.getWorld(data[3]);
                    double x = Double.parseDouble(data[4]);
                    double y = Double.parseDouble(data[5]);
                    double z = Double.parseDouble(data[6]);
                    homeLocation = new Location(world, x, y, z);
                }
                int level = Integer.parseInt(data[7]);
                int exp = Integer.parseInt(data[8]);
                Faction faction = new Faction(factionName, leaderUUID);
                faction.setHomeLocation(homeLocation);
                faction.setLevel(level);
                faction.setExp(exp);
                factions.put(factionName, faction);
                playerFactions.put(leaderUUID, factionName);
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
        factions.put(faction.getName(), faction);
        playerFactions.put(faction.getLeaderUUID(), faction.getName());
    }

    public void deleteFaction(Faction faction) {
        factions.remove(faction.getName());
        for (UUID memberUUID : faction.getMembers().keySet()) {
            playerFactions.remove(memberUUID);
        }
        claimedLand.entrySet().removeIf(entry -> entry.getValue().equals(faction));
    }

    public boolean factionExists(String name) {
        return factions.containsKey(name);
    }

    public Faction getFactionByName(String name) {
        return factions.get(name);
    }

    public Faction getFactionByPlayer(UUID playerUUID) {
        String factionName = playerFactions.get(playerUUID);
        if (factionName != null) {
            return factions.get(factionName);
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
        playerFactions.remove(playerUUID);
    }

    public void addMemberToFaction(UUID playerUUID, Faction faction) {
        faction.addMember(playerUUID);
        playerFactions.put(playerUUID, faction.getName());
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
}
