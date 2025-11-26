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

public class TeamSelectionGui extends SkyDefenderGui {

    private final TeamManager teamManager;
    private final Map<Integer, String> slotToTeamName = new HashMap<>();
    private final Inventory inventory;

    public TeamSelectionGui(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.inventory = Bukkit.createInventory(this, 27, "Choisir une équipe");
        setupInventory();
    }

    private void setupInventory() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        int slot = 10;
        for (GameTeam team : teamManager.getTeams()) {
            Material woolType = getWoolColor(team.getColor());
            
            ItemStack item = new ItemBuilder(woolType)
                    .setName(team.getColor() + team.getName())
                    .setLore(
                            ChatColor.GRAY + "Joueurs: " + ChatColor.YELLOW + team.getPlayers().size() + "/" + team.getMaxPlayer(),
                            "",
                            ChatColor.GREEN + ">> Clique pour rejoindre"
                    )
                    .toItemStack();

            inventory.setItem(slot, item);
            slotToTeamName.put(slot, team.getName());
            slot++;
            
            if (slot == 17) slot = 19;
            if (slot >= 26) break;
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
        if (!slotToTeamName.containsKey(slot)) return;
        
        String teamName = slotToTeamName.get(slot);
        if (teamManager.addPlayerToTeam(player, teamName, false)) {
            player.closeInventory();
        } else {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
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

