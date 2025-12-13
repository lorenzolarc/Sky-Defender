package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import java.util.Collections;

public class SuperKnockbackScenario extends Scenario {

    public SuperKnockbackScenario(SkyDefender plugin) {
        super(plugin, "Super Knockback", Material.STICK, Collections.singletonList(
            "§7Tout le monde prend 5x plus de knockback."
        ));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
        victim.setVelocity(direction.multiply(5).setY(0.5));
    }
}
