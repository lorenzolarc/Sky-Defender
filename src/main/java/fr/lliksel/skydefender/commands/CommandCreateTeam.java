package fr.lliksel.skydefender.commands;

import fr.lliksel.skydefender.manager.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreateTeam implements CommandExecutor {

    private final TeamManager teamManager;

    public CommandCreateTeam(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /createteam <nom> <couleur> <maxJoueurs>");
            return true;
        }

        String name = args[0];
        String colorName = args[1].toUpperCase();
        int maxPlayers;

        try {
            maxPlayers = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Le nombre maximum de joueurs doit être un nombre entier.");
            return true;
        }

        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Couleur invalide. Utilisez une des valeurs de ChatColor (ex: RED, BLUE, GREEN).");
            return true;
        }

        if (teamManager.createTeam(name, color, maxPlayers)) {
            sender.sendMessage(ChatColor.GREEN + "L'équipe " + color + name + ChatColor.GREEN + " a été créée avec succès !");
        } else {
            sender.sendMessage(ChatColor.RED + "La création de l'équipe a échoué. Le nom est-il trop long (max 16) ou existe-t-il déjà ?");
        }
        return true;
    }
}
