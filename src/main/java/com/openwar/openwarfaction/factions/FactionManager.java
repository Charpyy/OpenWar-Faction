package com.openwar.openwarfaction.factions;

import com.openwar.openwarfaction.handler.ClaimChunk;
import com.sun.media.jfxmedia.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

public class FactionManager {
    private Map<UUID, Faction> factions;
    private Map<String, UUID> factionUUIDs;
    private Map<UUID, UUID> playerFactions;
    private Map<Chunk, Faction> claimedLand;
    private Map<UUID, UUID> invitations;
    private final Map<UUID, Inventory> factionChests = new HashMap<>();

    public FactionManager() {
        this.factions = new HashMap<>();
        this.factionUUIDs = new HashMap<>();
        this.playerFactions = new HashMap<>();
        this.claimedLand = new HashMap<>();
        this.invitations = new HashMap<>();
    }

    public void saveFactionsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("factionUUID,factionName,leaderUUID,members,homeLocation,level,exp,raidingPoint\n");
            for (UUID factionUUID : factions.keySet()) {
                Faction faction = factions.get(factionUUID);
                writer.append(factionUUID.toString()).append(",")
                        .append(faction.getName()).append(",")
                        .append(faction.getLeaderUUID().toString()).append(",");

                StringBuilder members = new StringBuilder();
                for (Map.Entry<UUID, Rank> entry : faction.getMembers().entrySet()) {
                    members.append(entry.getKey().toString()).append(":").append(entry.getValue().toString()).append(";");
                }
                writer.append(members.toString()).append(",");

                Location home = faction.getHomeLocation();
                if (home != null) {
                    writer.append(home.getWorld() != null ? home.getWorld().getName() : "null").append(",")
                            .append(String.valueOf(home.getX())).append(",")
                            .append(String.valueOf(home.getY())).append(",")
                            .append(String.valueOf(home.getZ())).append(",");
                } else {
                    writer.append("null,null,null,null,");
                }

                writer.append(String.valueOf(faction.getLevel())).append(",")
                        .append(String.valueOf(faction.getExp())).append(",")
                        .append(String.valueOf(faction.getRaidPoint())).append(",");

                int[] perms = faction.getPermissions();
                for (int i = 0; i < perms.length; i++) {
                    if (i > 0) {
                        writer.append(";");
                    }
                    writer.append(String.valueOf(perms[i]));
                }
                writer.append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void loadFactionsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            System.out.println("Démarrage du chargement des factions depuis le fichier : " + filePath);
            while ((line = reader.readLine()) != null) {
                System.out.println("Chargement de la ligne : " + line);
                String[] data = line.split(",");
                UUID factionUUID = UUID.fromString(data[0]);
                String factionName = data[1];
                UUID leaderUUID = UUID.fromString(data[2]);
                Map<UUID, Rank> members = new HashMap<>();
                String[] memberUUIDs = data[3].split(";");
                for (String memberData : memberUUIDs) {
                    if (!memberData.isEmpty()) {
                        String[] parts = memberData.split(":");
                        UUID memberUUID = UUID.fromString(parts[0]);
                        Rank rank = parts.length > 1 ? Rank.valueOf(parts[1]) : Rank.RECRUE;
                        members.put(memberUUID, rank);
                        System.out.println("Membre ajouté : " + memberUUID + " avec le rôle : " + rank);
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
                int raidingPoint = Integer.parseInt(data[10]);
                String[] permsString = data[11].split(";");
                int[] perms = new int[permsString.length];
                for (int i = 0; i < permsString.length; i++) {
                    perms[i] = Integer.parseInt(permsString[i]);
                }
                Faction faction = new Faction(factionName, leaderUUID, factionUUID);
                faction.setHomeLocation(homeLocation);
                faction.setLevel(level);
                faction.setExp(exp);
                faction.setRaidPoint(raidingPoint);
                faction.setPermissions(perms);
                for (Map.Entry<UUID, Rank> entry : members.entrySet()) {
                    faction.addMemberRank(entry.getKey(), entry.getValue());
                }

                factions.put(factionUUID, faction);
                playerFactions.put(faction.getLeaderUUID(), factionUUID);
                for (UUID memberUUID : members.keySet()) {
                    playerFactions.put(memberUUID, factionUUID);
                }
                System.out.println("Faction chargée : " + factionUUID);
                System.out.println("  Nom : " + factionName);
                System.out.println("  Leader : " + leaderUUID);
                System.out.println("  Membres : " + members.keySet());
                if (homeLocation != null && !homeLocation.equals("null")) {
                    System.out.println("  Home Location : " + homeLocation.getWorld().getName() + " (" + homeLocation.getX() + ", " + homeLocation.getY() + ", " + homeLocation.getZ() + ")");
                } else {
                    System.out.println("  Home Location : none");
                }
                System.out.println("  Niveau : " + level);
                System.out.println("  Expérience : " + exp);
                System.out.println("  Points de Raid : " + raidingPoint);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveClaimsToCSV(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("world,chunkX,chunkZ,factionUUID\n");
            for (Map.Entry<Chunk, Faction> entry : claimedLand.entrySet()) {
                Chunk chunk = entry.getKey();
                Faction faction = entry.getValue();
                String worldName = chunk.getWorld().getName();
                writer.append(worldName + "," + chunk.getX() + "," + chunk.getZ() + "," + faction.getFactionUUID() + "\n");
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
                String worldName = data[0];
                System.out.println("World "+worldName);
                int chunkX = Integer.parseInt(data[1]);
                int chunkZ = Integer.parseInt(data[2]);
                UUID factionUUID = UUID.fromString(data[3]);

                World world = Bukkit.getServer().getWorld(worldName);
                System.out.println("World "+world);
                if (world != null) {
                    System.out.println(" World not null ");
                    Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                    Faction faction = getFactionByUUID(factionUUID);
                    System.out.println(" Faction "+faction);
                    if (faction != null) {
                        claimedLand.put(chunk, faction);
                        System.out.println("Claim : "+faction.getName()+" Chunk: "+chunk+" ");
                    }
                }
                System.out.println("Liste claims: "+ claimedLand);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Inventory getFactionChest(Faction faction) {
        UUID factionUUID = faction.getFactionUUID();
        return factionChests.computeIfAbsent(factionUUID, key -> Bukkit.createInventory(null, 45, "§cFaction §f- §cChest"));
    }

    public void saveFactionChests() {
        for (UUID factionUUID : factionChests.keySet()) {
            Inventory chest = factionChests.get(factionUUID);
            saveInventoryToConfig(factionUUID, chest);
        }
    }

    public void loadFactionChests() {
        for (Faction faction : getAllFactions()) {
            Inventory chest = loadInventoryFromConfig(faction.getFactionUUID());
            factionChests.put(faction.getFactionUUID(), chest);
        }
    }

    public void saveInventoryToConfig(UUID factionUUID, Inventory inventory) {
        File file = new File("plugins/OpenWar-Faction/faction_chests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<ItemStack> items = Arrays.asList(inventory.getContents());
        config.set("chests." + factionUUID.toString(), items);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Inventory loadInventoryFromConfig(UUID factionUUID) {
        File file = new File("plugins/OpenWar-Faction/faction_chests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Inventory inventory = Bukkit.createInventory(null, 45, "§cFaction §f- §cChest");
        List<?> items = config.getList("chests." + factionUUID.toString());
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                inventory.setItem(i, (ItemStack) items.get(i));
            }
        }
        return inventory;
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
    public boolean canPromote(UUID target,Faction faction, UUID playerUUID){
        Rank currentRank = faction.getRank(target);
        Rank playerRank = faction.getRank(playerUUID);
        return playerRank.isAbove(currentRank) && hasPermissionInFaction(playerUUID,faction,Permission.RANKUP);
    }

    public void demoteMember(UUID targetUUID, Faction faction) {
        faction.demoteMember(targetUUID);
    }

    public void claimLand(Chunk chunk, Faction faction) {
        if (!isLandClaimed(chunk)) {
            claimedLand.put(chunk, faction);
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
    public void addAllyToFaction(UUID allyUUID, Faction faction){
        faction.addAlly(allyUUID);
    }
    public void removeAllyToFaction(UUID allyUUID, Faction faction){
        faction.removeAlly(allyUUID);
    }
    public Faction getFactionByUUID(UUID factionUUID) {
        return factions.get(factionUUID);
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
            Chunk chunk = entry.getKey();
            Faction claimedFaction = entry.getValue();

            if (claimedFaction.equals(faction)) {
                claimedChunks.add(chunk);
            }
        }
        return claimedChunks;
    }
    public void setName(Faction faction, String newName){
        factionUUIDs.remove(faction.getName());
        faction.setName(newName);
        factionUUIDs.put(newName, faction.getFactionUUID());
    }
    public boolean hasPermissionInFaction(UUID playerUUID, Faction faction,Permission perm){
        if(faction.getLeaderUUID().equals(playerUUID)){
            return true;
        }
        if(faction.isMember(playerUUID)){
            return faction.hasPermission(PermRank.getPermRank(faction.getRank(playerUUID)),perm);
        }
        if(isFactionMember(playerUUID)) {
            if (faction.isAlly(getFactionByPlayer(playerUUID).getFactionUUID())) {//vérifier si le joueur est un allié
                return faction.hasPermission(PermRank.ALLY, perm);
            }
        }
        return faction.hasPermission(PermRank.NEUTRAL,perm);
    }

    public boolean hasPermissionInFaction(UUID uniqueId) {
        return false;
    }
}
