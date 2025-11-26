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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenarioGui extends SkyDefenderGui {

    private final ScenarioManager scenarioManager;
    private final Inventory inventory;
    private final Map<Integer, Scenario> slotToScenario = new HashMap<>();

    public ScenarioGui(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
        this.inventory = Bukkit.createInventory(this, 54, "Scénarios");
        updateInventory();
    }

    private void updateInventory() {
        inventory.clear();
        
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < 9; i++) inventory.setItem(i, glass);
        for (int i = 45; i < 54; i++) inventory.setItem(i, glass);

        ItemStack back = new ItemBuilder(Material.ARROW).setName(ChatColor.YELLOW + "Retour").toItemStack();
        inventory.setItem(49, back);

        List<Scenario> scenarios = scenarioManager.getScenarios();
        int slot = 9;

        for (Scenario scenario : scenarios) {
            if (slot >= 45) break;

            ItemBuilder builder = new ItemBuilder(scenario.getIcon())
                .setName((scenario.isActive() ? ChatColor.GREEN : ChatColor.RED) + scenario.getName())
                .setLore(scenario.getDescription());

            if (scenario.isActive()) {
                builder.addEnchant(Enchantment.DURABILITY, 1);
                builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                builder.setLore("", ChatColor.GREEN + "▶ ACTIVÉ");
            } else {
                builder.setLore("", ChatColor.RED + "▶ DÉSACTIVÉ");
            }

            inventory.setItem(slot, builder.toItemStack());
            slotToScenario.put(slot, scenario);
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

        if (slot == 49) {
            player.closeInventory();
            return;
        }

        if (slotToScenario.containsKey(slot)) {
            Scenario scenario = slotToScenario.get(slot);
            scenario.toggle();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1, 1);
            updateInventory();
        }
    }
}
