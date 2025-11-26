package fr.lliksel.skydefender.gui;

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

public class GameSettingsGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final GameConfigManager gameConfigManager;
    private final Inventory inventory;

    public GameSettingsGui(TeamManager teamManager, GameConfigManager gameConfigManager) {
        this.teamManager = teamManager;
        this.gameConfigManager = gameConfigManager;
        this.inventory = Bukkit.createInventory(this, 27, "Paramètres de la partie");
        updateInventory();
    }

    private void updateInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        // Map Size
        inventory.setItem(10, new ItemBuilder(Material.MAP)
                .setName(ChatColor.YELLOW + "Taille de la Map")
                .setLore(
                        ChatColor.WHITE + "" + gameConfigManager.getMapSize() + "x" + gameConfigManager.getMapSize(),
                        "",
                        ChatColor.GRAY + "Clic Gauche: +100",
                        ChatColor.GRAY + "Clic Droit: -100"
                )
                .toItemStack());

        // TP Spread
        inventory.setItem(12, new ItemBuilder(Material.ENDER_PEARL)
                .setName(ChatColor.YELLOW + "Dispersion TP")
                .setLore(
                        ChatColor.WHITE + "" + gameConfigManager.getTeleportSpread(),
                        "",
                        ChatColor.GRAY + "Clic Gauche: +50",
                        ChatColor.GRAY + "Clic Droit: -50"
                )
                .toItemStack());

        // PvP Time
        inventory.setItem(14, new ItemBuilder(Material.IRON_SWORD)
                .setName(ChatColor.YELLOW + "Temps PvP")
                .setLore(
                        ChatColor.WHITE + "" + gameConfigManager.getPvpTimeMinutes() + " minutes",
                        "",
                        ChatColor.GRAY + "Clic Gauche: +1m",
                        ChatColor.GRAY + "Clic Droit: -1m"
                )
                .toItemStack());
                
        // Kit Attaquants
        inventory.setItem(20, new ItemBuilder(Material.IRON_CHESTPLATE)
                .setName(ChatColor.RED + "Kit Attaquants")
                .setLore(ChatColor.GRAY + "Cliquez pour modifier l'inventaire", ChatColor.GRAY + "de départ des attaquants.")
                .toItemStack());
                
        // Kit Défenseurs
        inventory.setItem(22, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .setName(ChatColor.BLUE + "Kit Défenseurs")
                .setLore(ChatColor.GRAY + "Cliquez pour modifier l'inventaire", ChatColor.GRAY + "de départ des défenseurs.")
                .toItemStack());

        // Retour
        inventory.setItem(26, new ItemBuilder(Material.ARROW)
                .setName(ChatColor.YELLOW + "Retour")
                .toItemStack());
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

        if (slot == 10) { // Map Size
            int current = gameConfigManager.getMapSize();
            int change = event.getClick().isLeftClick() ? 100 : -100;
            int newVal = Math.max(100, current + change);
            gameConfigManager.setMapSize(newVal);
            gameConfigManager.saveSettings();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            updateInventory();
        } else if (slot == 12) { // TP Spread
            int current = gameConfigManager.getTeleportSpread();
            int change = event.getClick().isLeftClick() ? 50 : -50;
            int newVal = Math.max(50, current + change);
            gameConfigManager.setTeleportSpread(newVal);
            gameConfigManager.saveSettings();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            updateInventory();
        } else if (slot == 14) { // PvP Time
            int current = gameConfigManager.getPvpTimeMinutes();
            int change = event.getClick().isLeftClick() ? 1 : -1;
            int newVal = Math.max(0, current + change);
            gameConfigManager.setPvpTimeMinutes(newVal);
            gameConfigManager.saveSettings();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            updateInventory();
        } else if (slot == 20) { // Kit Attaquant
             new KitEditorGui(teamManager, gameConfigManager, "Attaquants").open(player);
        } else if (slot == 22) { // Kit Defenseur
             new KitEditorGui(teamManager, gameConfigManager, "Defenseurs").open(player);
        } else if (slot == 26) { // Retour
            new AdminConfigGui(teamManager).open(player);
        }
    }
}