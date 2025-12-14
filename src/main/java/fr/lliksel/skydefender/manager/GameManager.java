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
    private final GameConfigManager gameConfigManager;
    private GameState gameState;
    private Location bannerLocation;
    private final Map<UUID, Integer> kills = new HashMap<>();
    private long gameStartTime;

    public GameManager(SkyDefender plugin, TeamManager team, ConfigManager configManager, GameConfigManager gameConfigManager) {
        this.plugin = plugin;
        this.team = team;
        this.configManager = configManager;
        this.gameConfigManager = gameConfigManager;
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

    public String getRemainingPvpTime() {
        if (gameState != GameState.PLAYING) return null;

        long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        long pvpTimeSeconds = gameConfigManager.getPvpTimeMinutes() * 60;
        long remaining = pvpTimeSeconds - elapsedSeconds;

        if (remaining <= 0) return null;

        long minutes = remaining / 60;
        long seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setGameState(GameState gameState) {
        if (this.gameState == gameState) return;

        this.gameState = gameState;
        String prefix = ChatColor.GOLD + "[Sky Defender] ";

        switch (this.gameState) {
            case WAITING:
                Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + "En attente de joueurs...");
                break;

            case STARTING:
                Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "Le jeu va démarrer !");
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
                                0, 20, 10
                            );
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        countdown--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
                break;

            case PLAYING:
                this.gameStartTime = System.currentTimeMillis();
                Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "La partie commence ! Bonne chance.");
                startGameLogic();
                
                // PvP Timer
                int pvpTime = gameConfigManager.getPvpTimeMinutes();
                if (pvpTime > 0) {
                    Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + "Le PvP sera activé dans " + pvpTime + " minutes.");
                    for (World world : Bukkit.getWorlds()) world.setPVP(false);
                    
                    new org.bukkit.scheduler.BukkitRunnable() {
                        @Override
                        public void run() {
                             if (isState(GameState.PLAYING)) {
                                 for (World world : Bukkit.getWorlds()) world.setPVP(true);
                                 Bukkit.broadcastMessage(prefix + ChatColor.RED + "Le PvP est maintenant ACTIF !");
                                 for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                             }
                        }
                    }.runTaskLater(plugin, pvpTime * 60 * 20L);
                } else {
                    for (World world : Bukkit.getWorlds()) world.setPVP(true);
                    Bukkit.broadcastMessage(prefix + ChatColor.RED + "Le PvP est ACTIF !");
                }
                break;

            case FINISH:
                Bukkit.broadcastMessage(prefix + ChatColor.RED + "La partie est terminée !");
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
        gameConfigManager.updateWorldBorder();
        boolean uhcMode = gameConfigManager.isUhcMode();
        
        for (World world : this.plugin.getServer().getWorlds()) {
            world.setTime(0);
            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRule(GameRule.NATURAL_REGENERATION, !uhcMode);
            world.setGameRule(GameRule.KEEP_INVENTORY, false);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            
            java.util.Optional<fr.lliksel.skydefender.model.GameTeam> teamOpt = team.getPlayerTeam(player);
            if (teamOpt.isPresent()) {
                fr.lliksel.skydefender.model.GameTeam gameTeam = teamOpt.get();
                if (gameTeam.getName().equalsIgnoreCase("Spectateur")) {
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);

                    org.bukkit.inventory.ItemStack[] kit;
                    if (gameTeam.getName().equalsIgnoreCase("Defenseurs")) {
                        kit = gameConfigManager.getKit("Defenseurs");
                    } else {
                        kit = gameConfigManager.getKit("Attaquants");
                    }
                    
                    if (kit != null && kit.length > 0) {
                        if (kit.length > 41) {
                            kit = java.util.Arrays.copyOf(kit, 41);
                        }
                        player.getInventory().setContents(kit);
                    }
                }
            } else {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
        if (!this.team.teleportPlayers()) {
            getLogger().severe(ChatColor.RED + " [Sky Defender] Une erreur lors de la téléportation des joueurs est arrivé.");
        }
    }
}