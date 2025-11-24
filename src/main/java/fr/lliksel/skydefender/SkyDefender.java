package fr.lliksel.skydefender;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyDefender extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // 1. Message dans la console (visible au démarrage du serveur)
        getLogger().info(ChatColor.GREEN + "========================================");
        getLogger().info(ChatColor.GREEN + "SkyDefender (Test Build) est chargé !");
        getLogger().info(ChatColor.YELLOW + "Version Java détectée : " + System.getProperty("java.version"));
        getLogger().info(ChatColor.GREEN + "========================================");

        // 2. Enregistrement des événements (nécessaire pour que onJoin fonctionne)
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "SkyDefender s'éteint.");
    }

    // TEST 1 : Événement
    // Ce code se déclenche quand un joueur se connecte
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.DARK_AQUA + "---------------------------------------------");
        player.sendMessage(ChatColor.AQUA + " Bienvenue sur le serveur de Dev SkyDefender !");
        player.sendMessage(ChatColor.GRAY + " Le plugin fonctionne. Tape " + ChatColor.YELLOW + "/sd test");
        player.sendMessage(ChatColor.DARK_AQUA + "---------------------------------------------");
    }

    // TEST 2 : Commande
    // Ce code gère la commande /sd
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sd")) {

            // Si le joueur tape juste /sd sans arguments
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /sd <test|infos>");
                return true;
            }

            // Commande : /sd test
            if (args[0].equalsIgnoreCase("test")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    p.sendMessage(ChatColor.GREEN + "✔ Test réussi !");
                    p.sendMessage(ChatColor.GRAY + "Ton UUID est : " + p.getUniqueId());
                    p.sendMessage(ChatColor.GOLD + "Je te donne 5 niveaux d'XP pour fêter ça.");
                    p.giveExpLevels(5);
                    p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                } else {
                    sender.sendMessage("Seul un joueur peut tester cette commande.");
                }
                return true;
            }

            // Commande : /sd infos
            if (args[0].equalsIgnoreCase("infos")) {
                sender.sendMessage(ChatColor.YELLOW + "Plugin version: " + getDescription().getVersion());
                sender.sendMessage(ChatColor.YELLOW + "Auteurs: " + getDescription().getAuthors());
                return true;
            }
        }
        return false;
    }
}