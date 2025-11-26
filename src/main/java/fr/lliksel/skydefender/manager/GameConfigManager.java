package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.ArrayList;

public class GameConfigManager {

    private final ConfigManager configManager;

    private int mapSize = 1000;
    private int teleportSpread = 500;
    private int pvpTimeMinutes = 20;
    
    public GameConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
        loadSettings();
    }

    private void loadSettings() {
        FileConfiguration config = configManager.getConfig();
        this.mapSize = config.getInt("game_settings.map_size", 1000);
        this.teleportSpread = config.getInt("game_settings.teleport_spread", 500);
        this.pvpTimeMinutes = config.getInt("game_settings.pvp_time", 20);
    }

    public void saveSettings() {
        FileConfiguration config = configManager.getConfig();
        config.set("game_settings.map_size", mapSize);
        config.set("game_settings.teleport_spread", teleportSpread);
        config.set("game_settings.pvp_time", pvpTimeMinutes);
        configManager.saveConfig();
    }

    public void setKit(String kitName, ItemStack[] items) {
        configManager.getConfig().set("kits." + kitName, java.util.Arrays.asList(items));
        configManager.saveConfig();
    }

    public ItemStack[] getKit(String kitName) {
        List<?> list = configManager.getConfig().getList("kits." + kitName);
        if (list == null) return new ItemStack[0];
        
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof ItemStack) {
                items[i] = (ItemStack) item;
            } else {
                items[i] = null;
            }
        }
        return items;
    }

    public int getMapSize() { return mapSize; }
    public void setMapSize(int mapSize) { 
        this.mapSize = mapSize;
    }

    public int getTeleportSpread() { return teleportSpread; }
    public void setTeleportSpread(int spread) { this.teleportSpread = spread; }

    public int getPvpTimeMinutes() { return pvpTimeMinutes; }
    public void setPvpTimeMinutes(int minutes) { this.pvpTimeMinutes = minutes; }
    
    public void updateWorldBorder() {
        World world = Bukkit.getWorld("world");
        if (world == null && !Bukkit.getWorlds().isEmpty()) world = Bukkit.getWorlds().get(0);
        
        if (world != null) {
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(mapSize);
        }
    }
}
