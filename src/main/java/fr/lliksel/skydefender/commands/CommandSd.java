package fr.lliksel.skydefender.commands;

import fr.lliksel.skydefender.gui.ScenariosListGui;
import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.model.GameState;
import fr.lliksel.skydefender.model.GameTeam;
import org.bukkit.ChatColor;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.Banner;
import java.util.Optional;

public class CommandSd implements CommandExecutor {

    private final SkyDefender plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final ConfigManager configManager;

    public CommandSd(SkyDefender plugin, GameManager gameManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamManager = plugin.getTeamManager();
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /sd <infos|start|banner|defenseur|tpplate|revive|scenarios>");
            return true;
        }

        if (args[0].equalsIgnoreCase("infos")) {
            sender.sendMessage(ChatColor.YELLOW + "Plugin version: " + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.YELLOW + "Auteurs: " + plugin.getDescription().getAuthors());
            return true;
        }

        if (args[0].equalsIgnoreCase("scenarios")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Seul un joueur peut voir les scénarios.");
                return true;
            }
            if (this.gameManager.getGameState() != GameState.PLAYING) {
                sender.sendMessage("La partie n'est pas lancée.");
                return true;
            }
            new ScenariosListGui(plugin.getScenarioManager()).open((Player) sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (gameManager.getBannerLocation() == null) {
                sender.sendMessage(ChatColor.RED + "La bannière n'a pas été définie ! Utilisez /sd banner en regardant une bannière.");
                return true;
            }

            if (configManager.getLocation("locations.defenseurs_spawn") == null) {
                sender.sendMessage(ChatColor.RED + "Le point de spawn des défenseurs n'a pas été défini ! Utilisez /sd defenseur.");
                return true;
            }

            if (teamManager.getAttackerCount() < 1) {
                sender.sendMessage(ChatColor.RED + "Impossible de lancer la partie : Il faut au moins 1 attaquant.");
                return true;
            }

            if (teamManager.getDefenderCount() < 1) {
                sender.sendMessage(ChatColor.RED + "Impossible de lancer la partie : Il faut au moins 1 défenseur.");
                return true;
            }

            this.gameManager.setGameState(GameState.STARTING);
            return true;
        }

        if (args[0].equalsIgnoreCase("banner")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Seul un joueur peut définir la bannière.");
                return true;
            }
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlockExact(5);

            if (targetBlock != null && targetBlock.getState() instanceof Banner) {
                this.gameManager.setBannerLocation(targetBlock.getLocation());
                player.sendMessage(ChatColor.GREEN + "La bannière a été définie avec succès !");
            } else {
                player.sendMessage(ChatColor.RED + "Vous devez regarder une bannière.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("defenseur")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Seul un joueur peut définir le spawn.");
                return true;
            }
            Player player = (Player) sender;
            Optional<GameTeam> teamOpt = teamManager.getTeamByName("Defenseurs");

            if (teamOpt.isPresent()) {
                teamOpt.get().setSpawnLocation(player.getLocation());
                configManager.setLocation("locations.defenseurs_spawn", player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Le point de spawn des défenseurs a été défini !");
            } else {
                player.sendMessage(ChatColor.RED + "L'équipe Defenseurs n'existe pas.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("tpplate")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Seul un joueur peut définir les plaques.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /sd tpplate <high|low>");
                return true;
            }

            String type = args[1].toLowerCase();
            if (!type.equals("high") && !type.equals("low")) {
                sender.sendMessage(ChatColor.RED + "Type invalide. Utilisez 'high' ou 'low'.");
                return true;
            }

            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlockExact(5);

            if (targetBlock != null && targetBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                configManager.setLocation("locations.tp_plate." + type, targetBlock.getLocation());
                player.sendMessage(ChatColor.GREEN + "La plaque de tp " + type + " a été définie !");
            } else {
                player.sendMessage(ChatColor.RED + "Vous devez regarder une plaque de pression en or (Light Weighted).");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("revive")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /sd revive <joueur> [équipe]");
                return true;
            }

            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Le joueur " + playerName + " n'est pas connecté.");
                return true;
            }

            String teamName;
            if (args.length >= 3) {
                teamName = args[2];
            } else {
                teamName = teamManager.getPreviousTeam(target);
                if (teamName == null) {
                    sender.sendMessage(ChatColor.RED + "Impossible de trouver l'ancienne équipe de " + target.getName() + ". Veuillez spécifier une équipe.");
                    return true;
                }
            }

            if (teamManager.addPlayerToTeam(target, teamName, true)) {
                target.setGameMode(GameMode.SURVIVAL);
                target.setHealth(20);
                target.setFoodLevel(20);
                target.getInventory().clear();
                sender.sendMessage(ChatColor.GREEN + target.getName() + " a été ressuscité dans l'équipe " + teamName + " !");

                Location deathLoc = teamManager.getDeathLocation(target);
                if (deathLoc != null) {
                    target.teleport(deathLoc);
                    sender.sendMessage(ChatColor.GREEN + "Le joueur a été téléporté sur son lieu de mort.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "L'équipe " + teamName + " n'existe pas.");
            }
            return true;
        }

        return false;
    }
}
