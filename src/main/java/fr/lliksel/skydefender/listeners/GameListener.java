package fr.lliksel.skydefender.listeners; // Correction du package, la bonne orthographe est 'listeners' et non 'listerners'

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.model.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GameListener implements Listener {

    private final SkyDefender plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;

    public GameListener(SkyDefender plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.teamManager = plugin.getTeamManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameManager.isState(GameState.PLAYING)) {
            Player player = event.getEntity();
            event.setDeathMessage(null);
            this.teamManager.removePlayerFromTeamWhenDeath(player);
            Bukkit.broadcastMessage("[Sky Defender] " + player.getDisplayName() + " est mort.");
            player.getWorld().strikeLightningEffect(player.getLocation().subtract(0, -5, 0));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
            }
        }
    }
}
