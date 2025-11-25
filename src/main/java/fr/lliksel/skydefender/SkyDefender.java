package fr.lliksel.skydefender;

import fr.lliksel.skydefender.commands.CommandCreateTeam;
import fr.lliksel.skydefender.commands.CommandJoinTeam;
import fr.lliksel.skydefender.commands.CommandSd;
import fr.lliksel.skydefender.listeners.GameListener;
import fr.lliksel.skydefender.listeners.PlayerListener;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.manager.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyDefender extends JavaPlugin {

    private TeamManager teamManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        // 1. Initialisation des managers
        this.teamManager = new TeamManager();
        this.gameManager = new GameManager(this, this.teamManager);

        // Création des équipes par défaut
        teamManager.createTeam("Defenseurs", ChatColor.BLUE, 5);
        teamManager.createTeam("Spectateur", ChatColor.GRAY, Integer.MAX_VALUE);
        teamManager.createTeam("Rouge", ChatColor.RED, 3);

        // 2. Message dans la console
        getLogger().info(ChatColor.GREEN + "========================================");
        getLogger().info(ChatColor.GREEN + "SkyDefender (Test Build) est chargé ! (v 0.1)");
        getLogger().info(ChatColor.GREEN + "========================================");
        getLogger().warning(ChatColor.RED + "Ce plugin est actuellement en développement, il peut contenir des bugs et des fonctionnalités manquantes. Referez vous au depot pour plus d'information: https://github.com/lorenzolarc/Sky-Defender");

        // 3. Enregistrement des événements (Listeners)
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this.teamManager), this);

        // 4. Enregistrement des commandes
        getCommand("sd").setExecutor(new CommandSd(this, this.gameManager));
        getCommand("createteam").setExecutor(new CommandCreateTeam(this.teamManager));
        getCommand("jointeam").setExecutor(new CommandJoinTeam(this.teamManager));

        // 5. Configuration du monde (Lobby)
        for (World world : this.getServer().getWorlds()) {
            world.setPVP(false);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "SkyDefender s'éteint.");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}