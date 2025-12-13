package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HasteyBoysScenario extends Scenario {

    public HasteyBoysScenario(SkyDefender plugin) {
        super(plugin, "Hastey Boys", Material.DIAMOND_PICKAXE, Arrays.asList(
            "§7Les outils craftés sont automatiquement",
            "§7enchantés efficacité 3 et solidité 3."
        ));
    }

    @EventHandler
    public void onToolCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();

        if (result == null || result.getType() == Material.AIR) return;

        String typeName = result.getType().name();

        if (typeName.endsWith("_PICKAXE") || typeName.endsWith("_AXE") || typeName.endsWith("_SHOVEL")) {
            ItemMeta meta = result.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.DIG_SPEED, 3, true);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                result.setItemMeta(meta);
                event.getInventory().setResult(result);
            }
        }
    }
}
