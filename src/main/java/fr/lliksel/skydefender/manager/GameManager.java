package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.model.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GameManager {

    private final SkyDefender plugin;
    private GameState gameState;

    public GameManager(SkyDefender plugin) {
        this.plugin = plugin;
        this.gameState = GameState.WAITING;
    }

    public void setGameState(GameState gameState) {
        if (this.gameState == gameState) return;

        this.gameState = gameState;

        // Logique déclenchée lors du CHANGEMENT d'état
        switch (this.gameState) {
            case WAITING:
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[SkyDefender] En attente de joueurs...");
                break;

            case STARTING:
                Bukkit.broadcastMessage(ChatColor.GOLD + "[SkyDefender] Le jeu va démarrer !");
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
                Bukkit.broadcastMessage(ChatColor.GREEN + "[SkyDefender] La partie commence ! Bonne chance.");
                startGameLogic();
                break;

            case FINISH:
                Bukkit.broadcastMessage(ChatColor.RED + "[SkyDefender] La partie est terminée !");
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
        // Préparation basique des joueurs
        for (World world : this.plugin.getServer().getWorlds()) {
            world.setPVP(true);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            
            // TODO: Utiliser le TeamManager pour téléporter les joueurs à leur point de spawn
        }
    }
}