package fr.lliksel.skydefender.gui;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameConfigManager;
import fr.lliksel.skydefender.manager.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class KitEditorGui extends SkyDefenderGui {

    private final GameConfigManager gameConfigManager;
    private final TeamManager teamManager;
    private final String kitName;
    private final Inventory inventory;

    public KitEditorGui(TeamManager teamManager, GameConfigManager gameConfigManager, String kitName) {
        this.teamManager = teamManager;
        this.gameConfigManager = gameConfigManager;
        this.kitName = kitName;
        this.inventory = Bukkit.createInventory(this, 54, "Kit: " + kitName); // 54 slots (Double chest)
        loadKit();
    }

    private void loadKit() {
        ItemStack[] items = gameConfigManager.getKit(kitName);
        if (items != null) {
            inventory.setContents(items);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.sendMessage(ChatColor.GREEN + "Édition du kit " + kitName + ". Fermez l'inventaire pour sauvegarder.");
        player.openInventory(inventory);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }

    public void onInventoryClose(InventoryCloseEvent event) {
        gameConfigManager.setKit(kitName, inventory.getContents());
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Kit " + kitName + " sauvegardé !");

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(SkyDefender.class), () -> {
            new AdminConfigGui(teamManager).open((Player) event.getPlayer());
        }, 1L);
    }
}