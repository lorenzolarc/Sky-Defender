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

        ItemStack teamSettings = new ItemBuilder(Material.WHITE_BANNER)
            .setName(ChatColor.GOLD + "Paramètres équipes")
            .setLore(ChatColor.GRAY + "Gérer les équipes, les tailles", ChatColor.GRAY + "et les joueurs.")
            .toItemStack();

        inventory.setItem(11, teamSettings);


        ItemStack gameSettings = new ItemBuilder(Material.COMPARATOR)
            .setName(ChatColor.GOLD + "Paramètres de la partie")
            .setLore(ChatColor.GRAY + "Taille map, temps PvP, etc.")
            .toItemStack();
        inventory.setItem(13, gameSettings);


        ItemStack scenariosItem = new ItemBuilder(Material.BOOK)
            .setName(ChatColor.GOLD + "Scénarios")
            .setLore(ChatColor.GRAY + "Activer/Désactiver les modules", ChatColor.GRAY + "(CutClean, NoFall, etc.)")
            .toItemStack();
        inventory.setItem(15, scenariosItem);
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
        } else if (slot == 15) {
             fr.lliksel.skydefender.manager.ScenarioManager scenarioManager = JavaPlugin.getPlugin(SkyDefender.class).getScenarioManager();
             new ScenarioGui(scenarioManager).open(player);
        }
    }
}