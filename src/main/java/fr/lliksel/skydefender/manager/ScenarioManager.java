package fr.lliksel.skydefender.manager;

import fr.lliksel.skydefender.SkyDefender;
import fr.lliksel.skydefender.scenario.Scenario;
import fr.lliksel.skydefender.scenario.impl.CutCleanScenario;
import fr.lliksel.skydefender.scenario.impl.NoLavaScenario;
import fr.lliksel.skydefender.scenario.impl.NoPotionScenario;
import fr.lliksel.skydefender.scenario.impl.NoRodScenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioManager {

    private final SkyDefender plugin;
    private final List<Scenario> scenarios;

    public ScenarioManager(SkyDefender plugin) {
        this.plugin = plugin;
        this.scenarios = new ArrayList<>();

        // --- ENREGISTREMENT DES SCÉNARIOS ---
        // C'est ici que tu ajoutes tes nouveaux modes de jeu.
        // L'ordre d'ajout détermine l'ordre dans le menu GUI.
        
        registerScenario(new CutCleanScenario(plugin));
        registerScenario(new NoRodScenario(plugin));
        registerScenario(new NoPotionScenario(plugin));
        registerScenario(new NoLavaScenario(plugin));

    }

    private void registerScenario(Scenario scenario) {
        this.scenarios.add(scenario);
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }
    
    public void disableAll() {
        for (Scenario scenario : scenarios) {
            if (scenario.isActive()) {
                scenario.setActive(false);
            }
        }
    }
}
