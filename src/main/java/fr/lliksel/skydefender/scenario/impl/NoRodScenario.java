package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class NoRodScenario extends Scenario {

    public NoRodScenario(SkyDefender plugin) {
        super(plugin, "No rod", Material.FISHING_ROD, Collections.singletonList(
            "§7La canne à pêche devient désactivé"
        ));
    }

    @EventHandler
    public void onRodUse(PlayerFishEvent event) {
        event.setCancelled(true);
    }
}
