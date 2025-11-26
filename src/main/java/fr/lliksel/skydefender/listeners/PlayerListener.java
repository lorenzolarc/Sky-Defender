package fr.lliksel.skydefender.listeners;

import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final TeamManager teamManager;

    public PlayerListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(player.getDisplayName() + " vient de rejoindre ! " + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");

        teamManager.updatePlayerDisplayName(player);
        
        player.getInventory().clear();
        ItemStack compass = new ItemBuilder(Material.COMPASS)
                .setName("§e§lChoisir une équipe")
                .setLore("§7Faites un clic droit", "§7pour choisir votre équipe.")
                .toItemStack();
        player.getInventory().setItem(4, compass);

        if (player.isOp()) {
            ItemStack adminItem = new ItemBuilder(Material.ENDER_EYE)
                .setName("§c§lAdmin Config")
                .setLore("§7Gérer la partie et les équipes.")
                .toItemStack();
            player.getInventory().setItem(8, adminItem);
        }
    }
}
