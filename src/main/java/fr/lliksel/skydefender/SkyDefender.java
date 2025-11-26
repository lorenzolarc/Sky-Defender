package fr.lliksel.skydefender;

import fr.lliksel.skydefender.commands.CommandCreateTeam;
import fr.lliksel.skydefender.commands.CommandJoinTeam;
import fr.lliksel.skydefender.commands.CommandSd;
import fr.lliksel.skydefender.listeners.GameListener;
import fr.lliksel.skydefender.listeners.MenuListener;
import fr.lliksel.skydefender.listeners.PlayerListener;
import fr.lliksel.skydefender.manager.ChatInputManager;
import fr.lliksel.skydefender.manager.ConfigManager;
import fr.lliksel.skydefender.manager.GameConfigManager;
import fr.lliksel.skydefender.manager.GameManager;
import fr.lliksel.skydefender.manager.ScoreboardManager;
import fr.lliksel.skydefender.manager.TeamManager;
import fr.lliksel.skydefender.manager.ScenarioManager;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyDefender extends JavaPlugin {

    private TeamManager teamManager;
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private GameConfigManager gameConfigManager;
    private ChatInputManager chatInputManager;
    private ScenarioManager scenarioManager;

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);
        configManager.loadConfig();

        this.chatInputManager = new ChatInputManager(this);
        this.gameConfigManager = new GameConfigManager(configManager);
        this.teamManager = new TeamManager(configManager); // Uses getPlugin inside
        this.scenarioManager = new ScenarioManager(this);
        this.gameManager = new GameManager(this, this.teamManager, configManager, this.gameConfigManager);
        this.scoreboardManager = new ScoreboardManager(this, this.gameManager, this.teamManager);

        teamManager.createTeam("Defenseurs", ChatColor.BLUE, 5);
        teamManager.createTeam("Spectateur", ChatColor.GRAY, Integer.MAX_VALUE);
        teamManager.createTeam("Rouge", ChatColor.RED, 3);

        getLogger().info(ChatColor.GREEN + " ========================================");
        getLogger().info(ChatColor.GREEN + " Sky Defender est chargé ! (v 1.0)");
        getLogger().info(ChatColor.GREEN + " Auteur: Lorenzo LA ROCCA (lliksel)");
        getLogger().info(ChatColor.GREEN + " GitHub: https://github.com/lorenzolarc/Sky-Defender");
        getLogger().info(ChatColor.GREEN + " ========================================");

        getServer().getPluginManager().registerEvents(new GameListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this.teamManager), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("sd").setExecutor(new CommandSd(this, this.gameManager, configManager));

        for (World world : this.getServer().getWorlds()) {
            world.setPVP(false);
            world.setDifficulty(Difficulty.PEACEFUL);
        }

        this.scoreboardManager.startScoreboardTask();
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

    public GameConfigManager getGameConfigManager() {
        return gameConfigManager;
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }

    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }
}