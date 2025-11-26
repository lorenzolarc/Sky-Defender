package fr.lliksel.skydefender.gui;

import fr.lliksel.skydefender.SkyDefender;
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

public class TeamCreatorGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final Inventory inventory;
    
    // Temporary state
    private String name = "NouvelleEquipe";
    private ChatColor color = ChatColor.WHITE;
    private int maxPlayers = 3;

    public TeamCreatorGui(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.inventory = Bukkit.createInventory(this, 27, "Créer une équipe");
        updateInventory();
    }
    
    // Constructor for re-opening with state
    public TeamCreatorGui(TeamManager teamManager, String name, ChatColor color, int maxPlayers) {
        this.teamManager = teamManager;
        this.name = name;
        this.color = color;
        this.maxPlayers = maxPlayers;
        this.inventory = Bukkit.createInventory(this, 27, "Créer une équipe");
        updateInventory();
    }

    private void updateInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        // Nom
        inventory.setItem(10, new ItemBuilder(Material.NAME_TAG)
                .setName(ChatColor.YELLOW + "Nom de l'équipe")
                .setLore(ChatColor.WHITE + name, "", ChatColor.GRAY + "Clique pour changer")
                .toItemStack());

        // Couleur
        inventory.setItem(12, new ItemBuilder(getWoolColor(color))
                .setName(ChatColor.YELLOW + "Couleur")
                .setLore(color + color.name(), "", ChatColor.GRAY + "Clique pour changer")
                .toItemStack());

        // Taille
        inventory.setItem(14, new ItemBuilder(Material.PLAYER_HEAD)
                .setName(ChatColor.YELLOW + "Taille Max")
                .setLore(ChatColor.WHITE + "" + maxPlayers, "", ChatColor.GRAY + "Clic gauche: +1", ChatColor.GRAY + "Clic droit: -1")
                .toItemStack());
        
        // Valider
        inventory.setItem(22, new ItemBuilder(Material.LIME_CONCRETE)
                .setName(ChatColor.GREEN + "Créer l'équipe")
                .toItemStack());
                
        // Annuler
        inventory.setItem(26, new ItemBuilder(Material.RED_CONCRETE)
                .setName(ChatColor.RED + "Annuler")
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

        if (slot == 10) { // Change Name
            player.sendMessage(ChatColor.GREEN + "Entrez le nom de l'équipe dans le chat:");
            JavaPlugin.getPlugin(SkyDefender.class).getChatInputManager().requestInput(player, (input) -> {
                if (input.length() > 16) {
                    player.sendMessage(ChatColor.RED + "Nom trop long (max 16).");
                    new TeamCreatorGui(teamManager, name, color, maxPlayers).open(player);
                } else {
                    new TeamCreatorGui(teamManager, input, color, maxPlayers).open(player);
                }
            });
        } else if (slot == 12) { // Change Color
            this.color = getNextColor(this.color);
            updateInventory();
        } else if (slot == 14) { // Change Size
            if (event.getClick().isLeftClick()) {
                maxPlayers++;
            } else if (event.getClick().isRightClick()) {
                if (maxPlayers > 1) maxPlayers--;
            }
            updateInventory();
        } else if (slot == 22) { // Confirm
            if (teamManager.createTeam(name, color, maxPlayers)) {
                player.sendMessage(ChatColor.GREEN + "Équipe créée !");
                new AdminTeamsGui(teamManager).open(player);
            } else {
                player.sendMessage(ChatColor.RED + "Erreur: Nom pris ou invalide.");
                player.closeInventory();
            }
        } else if (slot == 26) { // Cancel
            new AdminTeamsGui(teamManager).open(player);
        }
    }

    private ChatColor getNextColor(ChatColor current) {
        ChatColor[] colors = {
            ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN,
            ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE,
            ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLACK
        };
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == current) {
                return colors[(i + 1) % colors.length];
            }
        }
        return ChatColor.RED;
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
