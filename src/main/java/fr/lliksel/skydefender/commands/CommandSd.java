package fr.lliksel.skydefender.commands;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.model.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSd implements CommandExecutor {

    private final SkyDefender plugin;
    private final GameManager gameManager;

    public CommandSd(SkyDefender plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /sd <test|infos|start>");
            return true;
        }

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

        if (args[0].equalsIgnoreCase("infos")) {
            sender.sendMessage(ChatColor.YELLOW + "Plugin version: " + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.YELLOW + "Auteurs: " + plugin.getDescription().getAuthors());
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            this.gameManager.setGameState(GameState.STARTING);
            return true;
        }

        return false;
    }
}
