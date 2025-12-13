package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CutCleanScenario extends Scenario {

    public CutCleanScenario(SkyDefender plugin) {
        super(plugin, "CutClean", Material.IRON_INGOT, Arrays.asList(
            "§7Les minerais et la nourriture",
            "§7sont cuits automatiquement."
        ));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        ItemStack drop = null;
        int exp = 0;

        if (type == Material.IRON_ORE) {
            drop = new ItemStack(Material.IRON_INGOT);
            exp = 2;
        } else if (type == Material.GOLD_ORE) {
            drop = new ItemStack(Material.GOLD_INGOT);
            exp = 3;
        }

        if (drop != null) {
            event.setDropItems(false);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            
            if (exp > 0) {
                ExperienceOrb orb = (ExperienceOrb) event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(exp);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType type = event.getEntityType();

        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.BEEF) item.setType(Material.COOKED_BEEF);
            if (item.getType() == Material.CHICKEN) item.setType(Material.COOKED_CHICKEN);
            if (item.getType() == Material.PORKCHOP) item.setType(Material.COOKED_PORKCHOP);
            if (item.getType() == Material.MUTTON) item.setType(Material.COOKED_MUTTON);
            if (item.getType() == Material.RABBIT) item.setType(Material.COOKED_RABBIT);
        }
    }
}
