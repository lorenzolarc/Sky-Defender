package fr.lliksel.skydefender.commands;

import fr.lliksel.skydefender.manager.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoinTeam implements CommandExecutor {

    private final TeamManager teamManager;

    public CommandJoinTeam(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /jointeam <nom>");
            return true;
        }

        Player player = (Player) sender;
        String teamName = args[0];

        teamManager.addPlayerToTeam(player, teamName, false);
        return true;
    }
}
