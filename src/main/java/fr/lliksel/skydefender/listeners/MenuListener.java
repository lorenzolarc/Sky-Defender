package fr.lliksel.skydefender.listeners;

import fr.lliksel.skydefender.gui.SkyDefenderGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SkyDefenderGui) {
            SkyDefenderGui gui = (SkyDefenderGui) event.getInventory().getHolder();
            gui.onInventoryClick(event);
        }
    }
}
