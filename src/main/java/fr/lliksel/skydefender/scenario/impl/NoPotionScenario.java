package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player; // Import Player
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource; // Import ProjectileSource

import java.util.Collections;

public class NoPotionScenario extends Scenario {

    public NoPotionScenario(SkyDefender plugin) {
        super(plugin, "No Potion", Material.POTION, Collections.singletonList(
            "§7Les potions sont désactivés"
        ));
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            if (player.getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            if (player.getGameMode() != GameMode.CREATIVE)
                event.setCancelled(true);
        }
    }
}
