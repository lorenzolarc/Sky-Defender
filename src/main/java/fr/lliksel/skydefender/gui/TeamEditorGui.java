package fr.lliksel.skydefender.gui;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.model.GameTeam;
import fr.lliksel.skydefender.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamEditorGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final GameTeam team;
    private final Inventory inventory;

    public TeamEditorGui(TeamManager teamManager, GameTeam team) {
        this.teamManager = teamManager;
        this.team = team;
        this.inventory = Bukkit.createInventory(this, 27, "Éditer: " + team.getName());
        updateInventory();
    }

    private void updateInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        boolean isSystem = teamManager.isSystemTeam(team.getName());

        inventory.setItem(10, new ItemBuilder(Material.NAME_TAG)
            .setName(ChatColor.YELLOW + "Nom: " + ChatColor.WHITE + team.getName())
            .setLore(isSystem ? ChatColor.RED + "Non modifiable" : ChatColor.GRAY + "Non modifiable (pour l'instant)")
            .toItemStack());

        inventory.setItem(12, new ItemBuilder(getWoolColor(team.getColor()))
            .setName(ChatColor.YELLOW + "Couleur: " + team.getColor() + team.getColor().name())
            .setLore(ChatColor.RED + "Non modifiable (Recréez l'équipe)")
            .toItemStack());

        inventory.setItem(14, new ItemBuilder(Material.PLAYER_HEAD)
            .setName(ChatColor.YELLOW + "Taille Max")
            .setLore(ChatColor.WHITE + "" + team.getMaxPlayer(), "", ChatColor.GRAY + "Clic gauche: +1", ChatColor.GRAY + "Clic droit: -1")
            .toItemStack());

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

        if (slot == 14) {
            int newSize = team.getMaxPlayer();
            if (event.getClick().isLeftClick()) {
                newSize++;
            } else if (event.getClick().isRightClick()) {
                if (newSize > 1) newSize--;
            }
            team.setMaxPlayer(newSize);
            updateInventory();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
        } else if (slot == 26) {
            new AdminTeamsGui(teamManager).open(player);
        }
    }

    private Material getWoolColor(ChatColor color) {
         switch (color) {
            case WHITE: return Material.WHITE_WOOL;
            case GOLD: return Material.ORANGE_WOOL;
            case LIGHT_PURPLE: return Material.MAGENTA_WOOL;
            case AQUA: return Material.LIGHT_BLUE_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            case GREEN: return Material.LIME_WOOL;
            case RED:
            case DARK_RED: return Material.RED_WOOL;
            case DARK_GRAY: return Material.GRAY_WOOL;
            case GRAY: return Material.LIGHT_GRAY_WOOL;
            case DARK_AQUA: return Material.CYAN_WOOL;
            case DARK_PURPLE: return Material.PURPLE_WOOL;
            case DARK_BLUE: 
            case BLUE: return Material.BLUE_WOOL;
            case DARK_GREEN: return Material.GREEN_WOOL;
            case BLACK: return Material.BLACK_WOOL;
            default: return Material.WHITE_WOOL;
        }
    }
}
