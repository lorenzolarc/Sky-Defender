package fr.lliksel.skydefender.listeners; // Correction du package, la bonne orthographe est 'listeners' et non 'listerners'

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.model.GameState;
import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

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
    public void onBlockBreak(BlockBreakEvent event) {
        if (!gameManager.isState(GameState.PLAYING)) return;
        if (gameManager.getBannerLocation() == null) return;
        if (!event.getBlock().getLocation().equals(gameManager.getBannerLocation())) return;

        Player player = event.getPlayer();
        Optional<GameTeam> playerTeamOpt = teamManager.getPlayerTeam(player);

        if (playerTeamOpt.isPresent() && playerTeamOpt.get().getName().equalsIgnoreCase("Defenseurs")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas casser votre propre bannière !");
            return;
        }

        Optional<GameTeam> defendersOpt = teamManager.getTeamByName("Defenseurs");
        if (defendersOpt.isPresent() && !defendersOpt.get().getPlayers().isEmpty()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Il reste des défenseurs en vie !");
            return;
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "La bannière a été détruite par " + player.getDisplayName() + " !");
        Bukkit.broadcastMessage(ChatColor.GOLD + "Les attaquants remportent la victoire !");
        gameManager.setGameState(GameState.FINISH);
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
