package fr.lliksel.skydefender.listeners;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.manager.ConfigManager;
import fr.lliksel.skydefender.model.GameState;
import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GameListener implements Listener {

    private final SkyDefender plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final ConfigManager configManager;
    private final Map<UUID, Long> tpCooldowns = new HashMap<>();

    public GameListener(SkyDefender plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.teamManager = plugin.getTeamManager();
        this.configManager = configManager;
    }

    @EventHandler
    public void onPressurePlateInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return;

        Player player = event.getPlayer();

        if (tpCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() < tpCooldowns.get(player.getUniqueId())) {
            event.setCancelled(true); // Prevent further interaction if on cooldown
            return;
        }

        Optional<GameTeam> teamOpt = teamManager.getPlayerTeam(player);
        if (!teamOpt.isPresent() || !teamOpt.get().getName().equalsIgnoreCase("Defenseurs")) return;

        Location clickedLoc = event.getClickedBlock().getLocation();
        Location highLoc = configManager.getLocation("locations.tp_plate.high");
        Location lowLoc = configManager.getLocation("locations.tp_plate.low");

        if (highLoc != null && locationsAreEqual(clickedLoc, highLoc)) {
            if (lowLoc != null) {
                player.teleport(lowLoc.clone().add(0.5, 0, 0.5)); // Teleport on the plate
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                tpCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 2000);
            }
        } else if (lowLoc != null && locationsAreEqual(clickedLoc, lowLoc)) {
            if (highLoc != null) {
                player.teleport(highLoc.clone().add(0.5, 0, 0.5));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                tpCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 2000);
            }
        }
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        org.bukkit.inventory.ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        
        String displayName = item.getItemMeta().getDisplayName();

        if (item.getType() == Material.COMPASS && displayName.contains("Choisir une équipe")) {
            event.setCancelled(true); // On annule seulement pour cet item
            new fr.lliksel.skydefender.gui.TeamSelectionGui(teamManager).open(player);
        } else if (item.getType() == Material.ENDER_EYE && displayName.contains("Admin Config")) {
            event.setCancelled(true); // On annule seulement pour cet item
            if (player.isOp()) {
                new fr.lliksel.skydefender.gui.AdminConfigGui(teamManager).open(player);
            } else {
                player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(org.bukkit.event.player.PlayerDropItemEvent event) {
        org.bukkit.inventory.ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem == null || !droppedItem.hasItemMeta() || !droppedItem.getItemMeta().hasDisplayName()) return;

        String displayName = droppedItem.getItemMeta().getDisplayName();
        if ((droppedItem.getType() == Material.COMPASS && displayName.contains("Choisir une équipe")) ||
            (droppedItem.getType() == Material.ENDER_EYE && displayName.contains("Admin Config"))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas jeter cet item !");
        }
    }
    
    private boolean locationsAreEqual(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
               loc1.getBlockX() == loc2.getBlockX() &&
               loc1.getBlockY() == loc2.getBlockY() &&
               loc1.getBlockZ() == loc2.getBlockZ();
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
        Bukkit.broadcastMessage(ChatColor.GOLD + "L'équipe \"" + playerTeamOpt.get().getColor() + playerTeamOpt.get().getName() + ChatColor.GOLD + "\" remporte la victoire !");
        gameManager.setGameState(GameState.FINISH);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameManager.isState(GameState.PLAYING)) {
            Player player = event.getEntity();
            Player killer = player.getKiller();

            if (killer != null) {
                gameManager.addKill(killer);
            }

            event.setDeathMessage(null);
            this.teamManager.removePlayerFromTeamWhenDeath(player);
            
            // Mise en spectateur
            this.teamManager.addPlayerToTeam(player, "Spectateur", true);
            player.setGameMode(GameMode.SPECTATOR);

            Bukkit.broadcastMessage("[Sky Defender] " + player.getDisplayName() + " est mort.");
            player.getWorld().strikeLightningEffect(player.getLocation().subtract(0, -5, 0));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
            }

            // Vérification de la victoire des défenseurs
            if (teamManager.getAttackerCount() == 0) {
                Optional<GameTeam> defenders = teamManager.getTeamByName("Defenseurs");
                if (defenders.isPresent() && !defenders.get().getPlayers().isEmpty()) {
                    Bukkit.broadcastMessage(ChatColor.BLUE + "Les Défenseurs ont éliminé tous les attaquants !");
                    Bukkit.broadcastMessage(ChatColor.BLUE + "Les Défenseurs remportent la victoire !");
                    gameManager.setGameState(GameState.FINISH);
                }
            }
        }
    }
}
