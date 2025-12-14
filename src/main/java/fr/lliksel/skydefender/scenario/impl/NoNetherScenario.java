package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;

public class NoNetherScenario extends Scenario {

    public NoNetherScenario(SkyDefender plugin) {
        super(plugin, "No Nether", Material.OBSIDIAN, Collections.singletonList(
            "§7Le Nether est désactivé."
        ));
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (event.getTo() != null && event.getTo().getWorld() != null && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Le Nether est désactivé dans ce scénario !");
            }
        }
    }
}
