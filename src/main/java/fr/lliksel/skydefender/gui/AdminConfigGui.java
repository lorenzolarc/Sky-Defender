package fr.lliksel.skydefender.gui;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameConfigManager;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminConfigGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final Inventory inventory;

    public AdminConfigGui(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.inventory = Bukkit.createInventory(this, 27, "Configuration Admin");
        setupInventory();
    }

    private void setupInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        // Catégorie 1: Paramètres équipes
        ItemStack teamSettings = new ItemBuilder(Material.WHITE_BANNER)
                .setName(ChatColor.GOLD + "Paramètres équipes")
                .setLore(ChatColor.GRAY + "Gérer les équipes, les tailles", ChatColor.GRAY + "et les joueurs.")
                .toItemStack();

        inventory.setItem(11, teamSettings);

        // Catégorie 2: Paramètres de la partie
        ItemStack gameSettings = new ItemBuilder(Material.COMPARATOR)
                .setName(ChatColor.GOLD + "Paramètres de la partie")
                .setLore(ChatColor.GRAY + "Taille map, temps PvP, etc.")
                .toItemStack();
        inventory.setItem(13, gameSettings);

        // Placeholder Catégorie 3
        ItemStack cat3 = new ItemBuilder(Material.BARRIER)
                .setName(ChatColor.RED + "À venir (Inventaires)")
                .toItemStack();
        inventory.setItem(15, cat3);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 11) {
            new AdminTeamsGui(teamManager).open(player);
        } else if (slot == 13) {
            GameConfigManager gameConfig = JavaPlugin.getPlugin(SkyDefender.class).getGameConfigManager();
            new GameSettingsGui(teamManager, gameConfig).open(player);
        }
    }
}