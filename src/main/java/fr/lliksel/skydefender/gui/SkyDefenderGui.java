package fr.lliksel.skydefender.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public abstract class SkyDefenderGui implements InventoryHolder {
    public abstract void onInventoryClick(InventoryClickEvent event);
}
