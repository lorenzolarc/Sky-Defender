package fr.lliksel.skydefender.gui;

import fr.lliksel.skydefender.manager.ScenarioManager;
import fr.lliksel.skydefender.scenario.Scenario;
import fr.lliksel.skydefender.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ScenariosListGui extends SkyDefenderGui {

    private final ScenarioManager scenarioManager;
    private final Inventory inventory;

    public ScenariosListGui(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
        this.inventory = Bukkit.createInventory(this, 27, "Scénarios Actifs");
        updateInventory();
    }

    private void updateInventory() {
        inventory.clear();
        
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        List<Scenario> scenarios = scenarioManager.getScenarios();
        int slot = 0;

        for (Scenario scenario : scenarios) {
            if (!scenario.isActive()) continue;
            if (slot >= inventory.getSize()) break;

            ItemBuilder builder = new ItemBuilder(scenario.getIcon())
                .setName(ChatColor.GREEN + scenario.getName())
                .setLore(scenario.getDescription());

            builder.addEnchant(Enchantment.DURABILITY, 1);
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            
            inventory.setItem(slot, builder.toItemStack());
            slot++;
        }
        
        if (slot == 0) {
            ItemStack noScenario = new ItemBuilder(Material.BARRIER)
                .setName(ChatColor.RED + "Aucun scénario actif")
                .toItemStack();
            inventory.setItem(13, noScenario);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        updateInventory();
        player.openInventory(inventory);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
