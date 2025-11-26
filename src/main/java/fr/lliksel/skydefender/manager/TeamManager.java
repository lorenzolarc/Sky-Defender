package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.World;
import java.util.Random;

public class TeamManager {
    private final ConfigManager configManager;
    private final List<GameTeam> teams;
    private final Scoreboard scoreboard;
    private final Random random = new Random();
    private final Map<UUID, String> previousTeams = new HashMap<>();
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    public TeamManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.teams = new ArrayList<>();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void applyTeamsToScoreboard(Scoreboard sb) {
        for (GameTeam gameTeam : this.teams) {
            org.bukkit.scoreboard.Team t = sb.getTeam(gameTeam.getName());
            if (t == null) {
                t = sb.registerNewTeam(gameTeam.getName());
            }
            t.setColor(gameTeam.getColor());
            t.setNameTagVisibility(NameTagVisibility.ALWAYS);

            for (UUID uuid : gameTeam.getPlayers()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    t.addEntry(p.getName());
                }
            }
        }
    }

    public Optional<GameTeam> getTeamByName(String name) {
        return teams.stream()
            .filter(team -> team.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public List<GameTeam> getTeams() {
        return new ArrayList<>(teams);
    }

    public boolean createTeam(String name, ChatColor color, int maxPlayers) {
        if (name.length() > 16) return false;
        if (getTeamByName(name).isPresent()) return false;

        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(name);
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam(name);
        }

        scoreboardTeam.setColor(color);
        // scoreboardTeam.setPrefix("[" + name + "] ");

        scoreboardTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);

        GameTeam newTeam = new GameTeam(name, color, maxPlayers);

        if (name.equalsIgnoreCase("Defenseurs")) {
            Location loc = configManager.getLocation("locations.defenseurs_spawn");
            if (loc != null) {
                newTeam.setSpawnLocation(loc);
            }
        }
        
        this.teams.add(newTeam);
        return true;
    }
    
    public void saveDefenderSpawn(Location loc) {
        getTeamByName("Defenseurs").ifPresent(t -> {
            t.setSpawnLocation(loc);
            configManager.setLocation("locations.defenseurs_spawn", loc);
        });
    }

    public boolean addPlayerToTeam(Player player, String teamName, boolean force) {
        Optional<GameTeam> teamOpt = getTeamByName(teamName);
        if (!teamOpt.isPresent()) {
            player.sendMessage(ChatColor.RED + "Cette équipe n'existe pas.");
            return false;
        }

        GameTeam team = teamOpt.get();

        if (!force && team.getPlayers().size() >= team.getMaxPlayer()) {
            player.sendMessage(ChatColor.RED + "Cette équipe est pleine.");
            return false;
        }

        removePlayerFromCurrentTeam(player);

        team.addPlayer(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Vous avez rejoint l'équipe " + team.getColor() + team.getName());

        updatePlayerDisplayName(player);

        return true;
    }

    public void removePlayerFromCurrentTeam(Player player) {
        getPlayerTeam(player).ifPresent(team -> {
            team.removePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Vous avez quitté l'équipe " + team.getColor() + team.getName());
        });
    }

    public Optional<GameTeam> getPlayerTeam(Player player) {
        return teams.stream()
            .filter(team -> team.getPlayers().contains(player.getUniqueId()))
            .findFirst();
    }

    public void updatePlayerDisplayName(Player player) {
        Optional<GameTeam> teamOpt = getPlayerTeam(player);

        for (org.bukkit.scoreboard.Team t : scoreboard.getTeams()) {
            if (t.hasEntry(player.getName())) {
                t.removeEntry(player.getName());
            }
        }

        if (teamOpt.isPresent()) {
            GameTeam gameTeam = teamOpt.get();
            org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(gameTeam.getName());

            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.registerNewTeam(gameTeam.getName());
                scoreboardTeam.setColor(gameTeam.getColor());
                scoreboardTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
            }

            scoreboardTeam.addEntry(player.getName());
            player.setScoreboard(this.scoreboard);

            String newName = gameTeam.getColor() + player.getName() + ChatColor.RESET;
            player.setDisplayName(newName);
            player.setPlayerListName(newName);
        } else {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());

            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public boolean teleportPlayers() {
        int min = configManager.getConfig().getInt("game.random_tp.min", -500);
        int max = configManager.getConfig().getInt("game.random_tp.max", 500);

        World world = Bukkit.getWorld("world");
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        for (GameTeam team : this.teams) {
            Location teamLocation;

            if (team.getSpawnLocation() != null) {
                teamLocation = team.getSpawnLocation();
            } else {
                int x = random.nextInt(max - min) + min;
                int z = random.nextInt(max - min) + min;

                int y = world.getHighestBlockYAt(x, z);
                teamLocation = new Location(world, x + 0.5, y + 1, z + 0.5);
            }

            for (UUID id : team.getPlayers()) {
                Player player = Bukkit.getPlayer(id);
                if (player != null && player.isOnline()) {
                    player.teleport(teamLocation);
                }
            }
        }
        return true;
    }

    public void removePlayerFromTeamWhenDeath(Player player) {
        getPlayerTeam(player).ifPresent(team -> {
            previousTeams.put(player.getUniqueId(), team.getName());
            deathLocations.put(player.getUniqueId(), player.getLocation());
            team.removePlayer(player.getUniqueId());
        });
    }

    public String getPreviousTeam(Player player) {
        return previousTeams.get(player.getUniqueId());
    }

    public Location getDeathLocation(Player player) {
        return deathLocations.get(player.getUniqueId());
    }

    public int getAlivePlayersCount() {
        return teams.stream()
            .filter(t -> !t.getName().equalsIgnoreCase("Spectateur"))
            .mapToInt(t -> t.getPlayers().size())
            .sum();
    }

    public int getAttackerCount() {
        return teams.stream()
            .filter(t -> !t.getName().equalsIgnoreCase("Spectateur") && !t.getName().equalsIgnoreCase("Defenseurs"))
            .mapToInt(t -> t.getPlayers().size())
            .sum();
    }

    public int getAttackerTeamsCount() {
        return (int) teams.stream()
            .filter(t -> !isSystemTeam(t.getName()))
            .count();
    }

    public boolean isSystemTeam(String name) {
        return name.equalsIgnoreCase("Defenseurs") || name.equalsIgnoreCase("Spectateur");
    }

    public boolean deleteTeam(String name) {
        if (isSystemTeam(name)) return false;

        Optional<GameTeam> teamOpt = getTeamByName(name);
        if (!teamOpt.isPresent()) return false;

        if (getAttackerTeamsCount() <= 1) return false;

        GameTeam team = teamOpt.get();
        
        // Kick players or move to spectators
        for (UUID uuid : new ArrayList<>(team.getPlayers())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                addPlayerToTeam(p, "Spectateur", true);
                p.sendMessage(ChatColor.RED + "Votre équipe a été supprimée.");
            }
        }

        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.getTeam(team.getName());
        if (scoreboardTeam != null) {
            scoreboardTeam.unregister();
        }

        this.teams.remove(team);
        return true;
    }
}