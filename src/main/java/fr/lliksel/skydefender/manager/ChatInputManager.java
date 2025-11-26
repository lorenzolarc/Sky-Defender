package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {

    private final Map<UUID, Consumer<String>> pendingInputs = new HashMap<>();

    public ChatInputManager(SkyDefender plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void requestInput(Player player, Consumer<String> callback) {
        pendingInputs.put(player.getUniqueId(), callback);
        player.closeInventory();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (pendingInputs.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            Consumer<String> callback = pendingInputs.remove(player.getUniqueId());

            player.getServer().getScheduler().runTask(SkyDefender.getPlugin(SkyDefender.class), () -> {
                callback.accept(event.getMessage());
            });
        }
    }
}
