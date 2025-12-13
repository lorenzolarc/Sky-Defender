package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Collections;

public class NoLavaScenario extends Scenario {

    public NoLavaScenario(SkyDefender plugin) {
        super(plugin, "No lava", Material.LAVA_BUCKET, Collections.singletonList(
            "§7Les joueurs ne peuvent pas poser de bloc de lave."
        ));
    }

    @EventHandler
    public void onLavaUse(PlayerBucketEmptyEvent event) {
        if (event.getBucket() == Material.LAVA_BUCKET) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }
}
