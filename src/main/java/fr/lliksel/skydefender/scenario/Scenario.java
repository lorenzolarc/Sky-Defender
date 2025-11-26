package fr.lliksel.skydefender.scenario;

import fr.lliksel.skydefender.SkyDefender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class Scenario implements Listener {

    private final JavaPlugin plugin;
    private final String name;
    private final Material icon;
    private final List<String> description;
    private boolean active;

    public Scenario(JavaPlugin plugin, String name, Material icon, List<String> description) {
        this.plugin = plugin;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.active = false;
    }

    public void toggle() {
        this.active = !this.active;
        if (this.active) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            onEnable();
        } else {
            HandlerList.unregisterAll(this);
            onDisable();
        }
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            toggle();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public String getName() { return name; }
    public Material getIcon() { return icon; }
    public List<String> getDescription() { return description; }
    public boolean isActive() { return active; }
}
