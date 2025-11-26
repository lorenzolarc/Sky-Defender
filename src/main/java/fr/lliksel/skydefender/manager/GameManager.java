package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.model.GameState;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class GameManager {

    private final SkyDefender plugin;
    private final TeamManager team;
    private final ConfigManager configManager;
    private GameState gameState;
    private Location bannerLocation;
    private final Map<UUID, Integer> kills = new HashMap<>();
    private long gameStartTime;

    public GameManager(SkyDefender plugin, TeamManager team, ConfigManager configManager) {
        this.plugin = plugin;
        this.team = team;
        this.configManager = configManager;
        this.gameState = GameState.WAITING;
        this.bannerLocation = configManager.getLocation("locations.banner");
    }

    public void setBannerLocation(Location bannerLocation) {
        this.bannerLocation = bannerLocation;
        configManager.setLocation("locations.banner", bannerLocation);
    }

    public Location getBannerLocation() {
        return bannerLocation;
    }

    public void addKill(Player player) {
        kills.put(player.getUniqueId(), getKills(player) + 1);
    }

    public int getKills(Player player) {
        return kills.getOrDefault(player.getUniqueId(), 0);
    }

    public String getGameTime() {
        if (gameState != GameState.PLAYING) return "00:00";
        long duration = (System.currentTimeMillis() - gameStartTime) / 1000;
        long minutes = duration / 60;
        long seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setGameState(GameState gameState) {
        if (this.gameState == gameState) return;

        this.gameState = gameState;

        // Logique déclenchée lors du CHANGEMENT d'état
        switch (this.gameState) {
            case WAITING:
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[Sky Defender] En attente de joueurs...");
                break;

            case STARTING:
                Bukkit.broadcastMessage(ChatColor.GOLD + "[Sky Defender] Le jeu va démarrer !");
                new org.bukkit.scheduler.BukkitRunnable() {
                    int countdown = 10;

                    @Override
                    public void run() {
                        if (countdown == 0) {
                            setGameState(GameState.PLAYING);
                            cancel();
                            return;
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendTitle(
                                ChatColor.GOLD + "Démarrage dans",
                                ChatColor.YELLOW + String.valueOf(countdown),
                                10, 20, 10
                            );
                        }
                        countdown--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                break;

            case PLAYING:
                this.gameStartTime = System.currentTimeMillis();
                Bukkit.broadcastMessage(ChatColor.GREEN + "[Sky Defender] La partie commence ! Bonne chance.");
                startGameLogic();
                break;

            case FINISH:
                Bukkit.broadcastMessage(ChatColor.RED + "[Sky Defender] La partie est terminée !");
                // TODO: Téléporter les joueurs au lobby, arrêter les tâches, etc.
                break;
        }
    }

    /**
     * Vérifie si le jeu est dans un état spécifique.
     * Utile pour les Listeners (ex: interdire le PvP si on est en WAITING).
     */
    public boolean isState(GameState state) {
        return this.gameState == state;
    }

    public GameState getGameState() {
        return gameState;
    }

    private void startGameLogic() {
        for (World world : this.plugin.getServer().getWorlds()) {
            world.setPVP(true);
            world.setTime(0);
            world.setDifficulty(Difficulty.NORMAL);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            
            java.util.Optional<fr.lliksel.skydefender.model.GameTeam> teamOpt = team.getPlayerTeam(player);
            if (teamOpt.isPresent() && teamOpt.get().getName().equalsIgnoreCase("Spectateur")) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        if (!this.team.teleportPlayers()) {
            getLogger().severe(ChatColor.RED + " [Sky Defender] Une erreur lors de la téléportation des joueurs est arrivé.");
        }
    }
}