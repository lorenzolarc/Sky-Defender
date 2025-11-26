# 🛠 Guide Technique : Créer un Scénario Personnalisé

Ce guide explique comment ajouter un nouveau module de jeu (Scénario) au plugin **Sky Defender**.
Le système est conçu pour être **modulaire** : chaque scénario est une classe isolée qui s'active et se désactive à la demande.

## 📂 Emplacement des fichiers

*   **Classe mère (Abstraite)** : `fr.lliksel.skydefender.scenario.Scenario`
*   **Vos implémentations** : `fr.lliksel.skydefender.scenario.impl.*`
*   **Le registre** : `fr.lliksel.skydefender.manager.ScenarioManager`

---

## 📝 Étape 1 : Créer la classe du Scénario

Créez une nouvelle classe Java dans le package `fr.lliksel.skydefender.scenario.impl`.
Par convention, nommez-la `NomDuScenario.java` (ex: `NoFallScenario.java`).

Elle doit hériter de la classe `Scenario`.

```java
package fr.lliksel.skydefender.scenario.impl;

import fr.lliksel.skydefender.scenario.Scenario;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public class MonSuperScenario extends Scenario {

    // Le constructeur doit OBLIGATOIREMENT prendre JavaPlugin en paramètre
    public MonSuperScenario(JavaPlugin plugin) {
        super(plugin, 
            "Nom du Scénario",      // Nom affiché dans le GUI
            Material.DIAMOND_SWORD, // Icône affichée dans le GUI
            Arrays.asList(          // Description (Lore)
                "§7Description ligne 1",
                "§7Description ligne 2"
            )
        );
    }
}
```

## ⚡️ Étape 2 : Ajouter la logique (Les événements)

La classe `Scenario` implémente déjà l'interface `Listener` de Bukkit.
Vous pouvez donc ajouter directement vos méthodes `@EventHandler` dans votre classe.

**La magie du système** : Vous n'avez pas besoin d'enregistrer les events manuellement. La méthode `toggle()` de la classe mère s'occupe d'enregistrer vos listeners quand le scénario s'active, et de les désenregistrer quand il se désactive.

Exemple pour un scénario **NoFall** (pas de dégâts de chute) :

```java
    @EventHandler
    public void onDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        // Vérifier si la cause est la chute
        if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL) {
            // Vérifier si c'est un joueur (optionnel selon le but)
            if (event.getEntity() instanceof org.bukkit.entity.Player) {
                event.setCancelled(true); // Annuler les dégâts
            }
        }
    }
```

### (Optionnel) Méthodes onEnable / onDisable

Si vous avez besoin d'initialiser des variables ou de lancer une tâche répétitive (Runnable) quand le scénario s'active, vous pouvez surcharger ces méthodes :

```java
    @Override
    protected void onEnable() {
        System.out.println("Mon scénario vient de démarrer !");
    }

    @Override
    protected void onDisable() {
        // Nettoyage si besoin
    }
```

---

## 🔗 Étape 3 : Enregistrer le Scénario

Pour que votre scénario apparaisse dans le menu en jeu, vous devez l'ajouter au `ScenarioManager`.

1.  Ouvrez `src/main/java/fr/lliksel/skydefender/manager/ScenarioManager.java`.
2.  Dans le constructeur, ajoutez une ligne `registerScenario` :

```java
    public ScenarioManager(SkyDefender plugin) {
        this.plugin = plugin;
        this.scenarios = new ArrayList<>();

        // --- ENREGISTREMENT DES SCÉNARIOS ---
        registerScenario(new CutCleanScenario(plugin));
        
        // AJOUTEZ VOTRE LIGNE ICI :
        registerScenario(new MonSuperScenario(plugin)); 
    }
```

⚠️ **L'ordre d'enregistrement détermine l'ordre d'affichage dans le menu.**

---

## ✅ Vérification

1.  Compilez le plugin (`mvn clean package`).
2.  Lancez le serveur.
3.  Prenez l'**Œil de l'Ender** (Menu Admin).
4.  Allez dans **Scénarios**.
5.  Votre scénario doit apparaître. Cliquez dessus pour l'activer (il devient vert et enchanté).
6.  Testez en jeu !
