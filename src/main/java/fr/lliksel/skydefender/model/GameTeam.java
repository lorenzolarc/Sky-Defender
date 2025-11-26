package fr.lliksel.skydefender.model;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class GameTeam {
    private final String name;
    private final ChatColor color;
    private Integer maxPlayer;
    private List<UUID> players;
    private Location spawnLocation;

    public GameTeam(String name, ChatColor color, Integer maxPlayer) {
        this.name = name;
        this.color = color;
        this.maxPlayer = maxPlayer;
        this.players = new java.util.ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public Integer getMaxPlayer() {
        return this.maxPlayer;
    }

    public void setMaxPlayer(Integer maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Player getPlayerByUUID(UUID uuid) {
        for (UUID search : this.players) {
            if (search.equals(uuid)) {
                return Bukkit.getPlayer(search);
            }
        }
        return null;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public void addPlayer(UUID player) {
        this.players.add(player);
    }

    public void removePlayer(UUID player) {
       this.players.remove(player);
    }
}
