package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamManager {
    private final List<GameTeam> teams;
    private final Scoreboard scoreboard;

    public TeamManager() {
        this.teams = new ArrayList<>();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public Optional<GameTeam> getTeamByName(String name) {
        return teams.stream()
            .filter(team -> team.getName().equalsIgnoreCase(name))
            .findFirst();
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
        this.teams.add(newTeam);
        return true;
    }

    public boolean addPlayerToTeam(Player player, String teamName) {
        Optional<GameTeam> teamOpt = getTeamByName(teamName);
        if (!teamOpt.isPresent()) {
            player.sendMessage(ChatColor.RED + "Cette équipe n'existe pas.");
            return false;
        }

        GameTeam team = teamOpt.get();

        if (team.getPlayers().size() >= team.getMaxPlayer()) {
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
            // Reset
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            // On remet le scoreboard par défaut pour être propre
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}