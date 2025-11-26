package fr.lliksel.skydefender.gui;

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

import java.util.HashMap;
import java.util.Map;

public class AdminTeamsGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final Inventory inventory;
    private final Map<Integer, String> slotToTeamName = new HashMap<>();

    public AdminTeamsGui(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.inventory = Bukkit.createInventory(this, 54, "Gestion des Équipes"); // Plus grand pour afficher les teams
        setupInventory();
    }

    private void setupInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glass); // Header decoration
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glass); // Footer decoration
        }

        // Bouton Retour
        ItemStack back = new ItemBuilder(Material.ARROW).setName(ChatColor.YELLOW + "Retour").toItemStack();
        inventory.setItem(49, back);

        // Bouton Créer une team
        ItemStack create = new ItemBuilder(Material.STICK)
                .setName(ChatColor.GREEN + "Créer une équipe")
                .setLore(ChatColor.GRAY + "Cliquez pour créer une nouvelle équipe.")
                .toItemStack();
        inventory.setItem(4, create);
        
        // Liste des teams
        int slot = 9;
        for (GameTeam team : teamManager.getTeams()) {
            if (slot >= 45) break;

            ItemStack item = new ItemBuilder(getWoolColor(team.getColor()))
                    .setName(team.getColor() + team.getName())
                    .setLore(
                            ChatColor.GRAY + "Taille Max: " + ChatColor.WHITE + team.getMaxPlayer(),
                            ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + team.getPlayers().size(),
                            "",
                            ChatColor.YELLOW + ">> Clique gauche pour gérer",
                            ChatColor.RED + ">> Clique droit pour supprimer"
                    )
                    .toItemStack();
            
            inventory.setItem(slot, item);
            slotToTeamName.put(slot, team.getName());
            slot++;
        }
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

        if (slot == 49) { // Retour
            new AdminConfigGui(teamManager).open(player);
            return;
        }

        if (slot == 4) { // Créer
            player.closeInventory();
            player.sendMessage(ChatColor.GOLD + "Pour créer une équipe, utilisez la commande: /createteam <nom> <couleur> <taille>");
            // TODO: Faire un chat listener pour ça plus tard si demandé explicitement sans commande
            return;
        }

        if (slotToTeamName.containsKey(slot)) {
            String teamName = slotToTeamName.get(slot);
            if (event.getClick().isRightClick()) {
                // Suppression (à implémenter dans TeamManager si nécessaire, pour l'instant juste un message)
                 player.sendMessage(ChatColor.RED + "Fonction de suppression à venir.");
            } else {
                // Ouverture du menu d'édition spécifique (à faire)
                player.sendMessage(ChatColor.YELLOW + "Menu d'édition pour " + teamName + " à venir.");
                // new TeamEditorGui(teamManager, teamName).open(player);
            }
        }
    }
    
    private Material getWoolColor(ChatColor color) {
         switch (color) {
            case WHITE: return Material.WHITE_WOOL;
            case GOLD: return Material.ORANGE_WOOL;
            case LIGHT_PURPLE: return Material.MAGENTA_WOOL;
            case AQUA: return Material.LIGHT_BLUE_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            case GREEN: return Material.LIME_WOOL; // Green is usually dark green
            case RED: // Red or Dark Red
            case DARK_RED: return Material.RED_WOOL; // Pink? No red.
            case DARK_GRAY: return Material.GRAY_WOOL; // Gray
            case GRAY: return Material.LIGHT_GRAY_WOOL;
            case DARK_AQUA: return Material.CYAN_WOOL;
            case DARK_PURPLE: return Material.PURPLE_WOOL;
            case DARK_BLUE: 
            case BLUE: return Material.BLUE_WOOL;
            case DARK_GREEN: return Material.GREEN_WOOL; // Dark green
            case BLACK: return Material.BLACK_WOOL;
            default: return Material.WHITE_WOOL;
        }
    }
}
