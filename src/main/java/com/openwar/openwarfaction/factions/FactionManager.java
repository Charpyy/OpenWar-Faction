package com.openwar.openwarfaction.factions;

import com.openwar.openwarcore.Utils.ChunkKey;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.util.*;

public class FactionManager {
    private Map<UUID, Faction> factions;
    private Map<String, UUID> factionUUIDs;
    private Map<UUID, UUID> playerFactions;
    private Map<ChunkKey, UUID> claimedLand;
    private Map<UUID, UUID> invitations;
    private Map<UUID, Inventory> factionChests;

    public FactionManager(Map<ChunkKey, UUID> claimedLand, Map<UUID, Inventory> factionChests, Map<UUID, Faction> factions, Map<String, UUID> factionUUIDs, Map<UUID, UUID> playerFactions) {
        this.factions = factions;
        this.factionUUIDs = factionUUIDs;
        this.playerFactions = playerFactions;
        this.claimedLand = claimedLand;
        this.invitations = new HashMap<>();
        this.factionChests = factionChests;
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
        claimedLand.entrySet().removeIf(entry -> entry.getValue().equals(factionId));
    }

    public boolean factionExists(String name) {
        return factionUUIDs.containsKey(name);
    }

    public Inventory getFactionChest(Faction faction) {
        UUID factionUUID = faction.getFactionUUID();
        return factionChests.computeIfAbsent(factionUUID, key -> Bukkit.createInventory(null, 45, "§cFaction §f- §cChest"));
    }

    public Faction getFactionByName(String name) {
        for (Faction faction : factions.values()) {
            if (faction.getName().equalsIgnoreCase(name)) {
                return faction;
            }
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

    public void deleteInventoryFromConfig(UUID factionUUID) {
        File file = new File("plugins/OpenWar-Faction/faction_chests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("chests." + factionUUID.toString(), null);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFactionLeader(UUID playerUUID) {
        Faction faction = getFactionByPlayer(playerUUID);
        return faction != null && faction.getLeaderUUID().equals(playerUUID);
    }

    public List<Faction> getAllFactions() {
        return new ArrayList<>(factions.values());
    }

    public void promoteMember(UUID targetUUID, Faction faction) {
        faction.promoteMember(targetUUID);
    }

    public boolean canPromote(UUID target, Faction faction, UUID playerUUID) {
        Rank currentRank = faction.getRank(target);
        Rank playerRank = faction.getRank(playerUUID);
        return playerRank.isAbove(currentRank) && hasPermissionInFaction(playerUUID, faction, Permission.RANKUP);
    }

    public void demoteMember(UUID targetUUID, Faction faction) {
        faction.demoteMember(targetUUID);
    }

    public void claimLand(Chunk chunk, Faction faction) {
        ChunkKey chunkKey = new ChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (!isLandClaimed(chunk)) {
            claimedLand.put(chunkKey, faction.getFactionUUID());
        }
    }

    public String getFactionNameByUUID(UUID factionUUID) {
        Faction faction = factions.get(factionUUID);
        return faction != null ? faction.getName() : null;
    }

    public void invitePlayerToFaction(UUID playerUUID, UUID factionUUID) {
        invitations.put(playerUUID, factionUUID);
    }

    public UUID getInvitedFaction(UUID playerUUID) {
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

    public void addMemberToFaction(UUID playerUUID, UUID factionUUID, Rank rank) {
        Faction faction = factions.get(factionUUID);
        if (faction != null) {
            faction.addMemberRank(playerUUID, rank);
            playerFactions.put(playerUUID, factionUUID);
            invitations.remove(playerUUID);
        }
    }

    public int addAllyToFaction(UUID allyUUID, Faction faction) {
        if (faction.addAlly(allyUUID)) {
            return 0;
        }
        if (getFactionByUUID(allyUUID).askAlly(faction.getFactionUUID())) {
            return 1;
        }
        return 2;
    }

    public void removeAllyToFaction(UUID allyUUID, Faction faction) {
        faction.removeAlly(allyUUID);
    }

    public Faction getFactionByUUID(UUID factionUUID) {
        return factions.get(factionUUID);
    }


    public boolean isLandClaimed(Chunk chunk) {
        ChunkKey chunkKey = new ChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        return claimedLand.containsKey(chunkKey);
    }

    public Faction getFactionByChunk(Chunk chunk) {
        ChunkKey chunkKey = new ChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        UUID factionUUID = claimedLand.get(chunkKey);
        return factionUUID != null ? getFactionByUUID(factionUUID) : null;
    }

    public void unclaimLand(Chunk chunk) {
        ChunkKey chunkKey = new ChunkKey(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        claimedLand.remove(chunkKey);
    }

    public List<Chunk> getClaimedChunks(Faction faction) {
        List<Chunk> claimedChunks = new ArrayList<>();
        UUID factionUUID = faction.getFactionUUID();
        for (Map.Entry<ChunkKey, UUID> entry : claimedLand.entrySet()) {
            if (entry.getValue().equals(factionUUID)) {
                World world = Bukkit.getWorld(entry.getKey().getWorldName());
                if (world != null) {
                    claimedChunks.add(world.getChunkAt(entry.getKey().getChunkX(), entry.getKey().getChunkZ()));
                }
            }
        }
        return claimedChunks;
    }

    public void setName(Faction faction, String newName) {
        factionUUIDs.remove(faction.getName());
        faction.setName(newName);
        factionUUIDs.put(newName, faction.getFactionUUID());
    }

    public boolean hasPermissionInFaction(UUID playerUUID, Faction faction, Permission perm) {
        if (faction.getLeaderUUID().equals(playerUUID)) {
            return true;
        }
        if (faction.isMember(playerUUID)) {
            return faction.hasPermission(PermRank.getPermRank(faction.getRank(playerUUID)), perm);
        }
        if (isFactionMember(playerUUID)) {
            if (faction.isAlly(getFactionByPlayer(playerUUID).getFactionUUID())) {
                return faction.hasPermission(PermRank.ALLY, perm);
            }
        }
        return faction.hasPermission(PermRank.NEUTRAL, perm);
    }

    public boolean hasPermissionInFaction(UUID uniqueId) {
        return false;
    }
}
