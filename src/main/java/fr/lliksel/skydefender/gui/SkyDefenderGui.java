package fr.lliksel.skydefender.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public abstract class SkyDefenderGui implements InventoryHolder {
    public abstract void onInventoryClick(InventoryClickEvent event);
    
    public void onInventoryClose(InventoryCloseEvent event) {
        // Default implementation does nothing
    }
}
