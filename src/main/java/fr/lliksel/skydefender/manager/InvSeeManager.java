package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class InvSeeManager implements Listener {

    private final SkyDefender plugin;
    private final HashMap<UUID, UUID> viewers = new HashMap<>();

    public InvSeeManager(SkyDefender plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openInvSee(Player viewer, Player target) {
        Inventory gui = Bukkit.createInventory(null, 54, "Inv: " + target.getName());

        ItemStack separatorTop = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta metaTop = separatorTop.getItemMeta();
        if (metaTop != null) {
            metaTop.setDisplayName("§7⬅ Armure | Main Secondaire ➡");
            separatorTop.setItemMeta(metaTop);
        }

        ItemStack separatorBottom = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta metaBottom = separatorBottom.getItemMeta();
        if (metaBottom != null) {
            metaBottom.setDisplayName("§7⬆ Inventaire | Hotbar ⬇");
            separatorBottom.setItemMeta(metaBottom);
        }

        for(int i=4; i<8; i++) gui.setItem(i, separatorTop);
        for(int i=36; i<45; i++) gui.setItem(i, separatorBottom);

        ItemStack[] armor = target.getInventory().getArmorContents();
        gui.setItem(0, armor[3]); // Helmet
        gui.setItem(1, armor[2]); // Chest
        gui.setItem(2, armor[1]); // Leggings
        gui.setItem(3, armor[0]); // Boots

        gui.setItem(8, target.getInventory().getItemInOffHand());

        ItemStack[] contents = target.getInventory().getContents();

        for (int i = 9; i < 36; i++) {
            if (i < contents.length) gui.setItem(i, contents[i]);
        }

        for (int i = 0; i < 9; i++) {
            if (i < contents.length) gui.setItem(45 + i, contents[i]);
        }
        
        viewers.put(viewer.getUniqueId(), target.getUniqueId());
        viewer.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player admin = (Player) event.getWhoClicked();
        
        if (!viewers.containsKey(admin.getUniqueId())) return;

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
            int slot = event.getSlot();
            if ((slot >= 4 && slot < 8) || (slot >= 36 && slot < 45)) {
                event.setCancelled(true);
                return;
            }
        }
        
        Bukkit.getScheduler().runTask(plugin, () -> updateTargetInventory(admin, event.getInventory()));
    }
    
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
         if (!(event.getWhoClicked() instanceof Player)) return;
         Player admin = (Player) event.getWhoClicked();
         if (viewers.containsKey(admin.getUniqueId())) {
              Bukkit.getScheduler().runTask(plugin, () -> updateTargetInventory(admin, event.getInventory()));
         }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        viewers.remove(event.getPlayer().getUniqueId());
    }

    private void updateTargetInventory(Player admin, Inventory gui) {
        UUID targetUUID = viewers.get(admin.getUniqueId());
        Player target = Bukkit.getPlayer(targetUUID);
        
        if (target == null || !target.isOnline()) {
            return;
        }
        
        ItemStack[] armor = target.getInventory().getArmorContents();
        armor[3] = gui.getItem(0); 
        armor[2] = gui.getItem(1);
        armor[1] = gui.getItem(2);
        armor[0] = gui.getItem(3); 
        target.getInventory().setArmorContents(armor);
        
        target.getInventory().setItemInOffHand(gui.getItem(8));
        
        for (int i = 9; i < 36; i++) {
            target.getInventory().setItem(i, gui.getItem(i));
        }
        
        for (int i = 0; i < 9; i++) {
            target.getInventory().setItem(i, gui.getItem(45 + i));
        }
        
        target.updateInventory();
    }
}
