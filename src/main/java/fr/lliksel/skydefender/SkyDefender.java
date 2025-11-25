package fr.lliksel.skydefender;

import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.manager.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyDefender extends JavaPlugin implements Listener {

    private TeamManager teamManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        // 1. Initialisation des managers
        this.teamManager = new TeamManager();
        this.gameManager = new GameManager(this);

        // 2. Message dans la console
        getLogger().info(ChatColor.GREEN + "========================================");
        getLogger().info(ChatColor.GREEN + "SkyDefender (Test Build) est chargé ! (v 0.1)");
        getLogger().info(ChatColor.GREEN + "========================================");
        getLogger().warning(ChatColor.RED + "Ce plugin est actuellement en développement, il peut contenir des bugs et des fonctionnalités manquantes. Referez vous au depot pour plus d'information: https://github.com/lorenzolarc/Sky-Defender");

        // 3. Enregistrement des événements
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "SkyDefender s'éteint.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(player.getDisplayName() + " vient de rejoindre ! " + "(" + this.getServer().getOnlinePlayers().size() + "/" + this.getServer().getMaxPlayers() + ")");
        // Met à jour le nom du joueur s'il était déjà dans une équipe
        teamManager.updatePlayerDisplayName(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "sd":
                return handleSdCommand(sender, args);
            case "createteam":
                return handleCreateTeamCommand(sender, args);
            case "jointeam":
                return handleJoinTeamCommand(sender, args);
            default:
                return false;
        }
    }

    private boolean handleSdCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /sd <test|infos>");
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
            sender.sendMessage(ChatColor.YELLOW + "Plugin version: " + getDescription().getVersion());
            sender.sendMessage(ChatColor.YELLOW + "Auteurs: " + getDescription().getAuthors());
            return true;
        }
        return false;
    }

    private boolean handleCreateTeamCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /createteam <nom> <couleur> <maxJoueurs>");
            return true;
        }

        String name = args[0];
        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Le nom de l'équipe ne peut pas dépasser 16 caractères.");
            return true;
        }

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

    private boolean handleJoinTeamCommand(CommandSender sender, String[] args) {
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

        teamManager.addPlayerToTeam(player, teamName);
        return true;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
