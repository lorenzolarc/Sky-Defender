package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.model.GameState;
import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.Optional;

public class ScoreboardManager {

    private final SkyDefender plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;

    public ScoreboardManager(SkyDefender plugin, GameManager gameManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamManager = teamManager;
    }

    public void startScoreboardTask() {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
            if (!gameManager.isState(GameState.PLAYING)) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateScoreboard(Player player) {
        Scoreboard sb = player.getScoreboard();
        if (sb.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(sb);
        }

        teamManager.applyTeamsToScoreboard(sb);

        Objective objective = sb.getObjective("SkyDefender");
        if (objective == null) {
            objective = sb.registerNewObjective("SkyDefender", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "Sky Defender");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        setScore(objective, ChatColor.GRAY + "------------------", 15);
        setScore(objective, ChatColor.YELLOW + "Temps: " + ChatColor.WHITE + gameManager.getGameTime(), 14);

        String pvpTimer = gameManager.getRemainingPvpTime();
        if (pvpTimer != null) {
            setScore(objective, ChatColor.AQUA + "PvP: " + ChatColor.WHITE + pvpTimer, 13);
        } else {
            setScore(objective, " ", 13);
        }
        
        Optional<GameTeam> team = teamManager.getPlayerTeam(player);
        String teamName = team.map(gameTeam -> gameTeam.getColor() + gameTeam.getName()).orElse(ChatColor.GRAY + "Aucune");
        setScore(objective, ChatColor.YELLOW + "Équipe: " + teamName, 12);
        
        setScore(objective, "  ", 11);
        setScore(objective, ChatColor.YELLOW + "Joueurs: " + ChatColor.WHITE + getPlayersCount(), 10);
        setScore(objective, ChatColor.BLUE + " > Déf: " + ChatColor.WHITE + teamManager.getDefenderCount(), 9);
        setScore(objective, ChatColor.RED + " > Att: " + ChatColor.WHITE + teamManager.getAttackerCount(), 8);
        
        setScore(objective, ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + gameManager.getKills(player), 7);
        
        setScore(objective, "   ", 6);
        setScore(objective, getBannerDistance(player), 5);
        
        setScore(objective, ChatColor.GRAY + "-----------------", 4);
    }

    private void setScore(Objective objective, String text, int score) {
        org.bukkit.scoreboard.Score s = objective.getScore(text);
        s.setScore(score);

        for (String entry : objective.getScoreboard().getEntries()) {
            org.bukkit.scoreboard.Score oldScore = objective.getScore(entry);
            if (oldScore.getScore() == score && !entry.equals(text)) {
                objective.getScoreboard().resetScores(entry);
            }
        }
    }

    private int getPlayersCount() {
        return teamManager.getAlivePlayersCount();
    }

    private String getBannerDistance(Player player) {
        Location bannerLoc = gameManager.getBannerLocation();
        if (bannerLoc == null || player.getWorld() != bannerLoc.getWorld()) {
            return ChatColor.RED + "Bannière: ?";
        }
        
        int distance = (int) player.getLocation().distance(bannerLoc);
        String arrow = getArrowDirection(player, bannerLoc);
        
        return ChatColor.GOLD + "Bannière: " + ChatColor.WHITE + distance + "m " + arrow;
    }
    
    private String getArrowDirection(Player player, Location target) {
        Vector playerDir = player.getLocation().getDirection().setY(0).normalize();
        Vector targetDir = target.toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
        
        double angle = Math.toDegrees(Math.atan2(
                playerDir.getZ() * targetDir.getX() - playerDir.getX() * targetDir.getZ(),
                playerDir.getX() * targetDir.getX() + playerDir.getZ() * targetDir.getZ()
        ));

        
        if (angle > -45 && angle <= 45) {
            return "⬆";
        } else if (angle > 45 && angle <= 135) {
            return "⬅";
        } else if (angle > -135 && angle <= -45) {
            return "➡";
        } else {
            return "⬇";
        }
    }
}
