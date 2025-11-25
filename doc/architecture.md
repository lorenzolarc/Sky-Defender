# Architecture du projet Sky Defender

Voici l'arborescence de packages (dossiers) recommandée. Crée ces packages dans `src/main/java/fr/lliksel/skydefender/`.

## 1. L'Arborescence des fichiers

```
fr.lliksel.skydefender
│
├── SkyDefender.java          // Ta classe Main (extends JavaPlugin). Le point d'entrée.
│
├── manager/                  // Le Cerveau. Contient la logique pure.
│   ├── GameManager.java      // Gère l'état du jeu (Lobby, Jeu, Fin) et la boucle principale (Runnable).
│   ├── TeamManager.java      // Gère la création des équipes, rejoindre, quitter, spawn points.
│   ├── ScenarioManager.java  // Gère l'activation/désactivation des modules (CutClean, etc.).
│   └── ConfigManager.java    // Gère la lecture/écriture dans config.yml.
│
├── model/                    // Les Objets de données (POJO).
│   ├── GameState.java        // Enum (WAITING, STARTING, PLAYING, FINISH).
│   ├── GameTeam.java         // Objet représentant une équipe (Nom, Couleur, List<UUID> membres).
│   └── GamePlayer.java       // (Optionnel) Wrapper autour du joueur Bukkit pour stocker ses stats (kills, rôle).
│
├── scenario/                 // Tes modules de jeu (Polymorphisme ici !).
│   ├── Scenario.java         // Interface ou Classe Abstraite (méthodes: enable(), disable(), onEvent()).
│   ├── impl/                 // Tes implémentations concrètes.
│   │   ├── CutCleanScenario.java
│   │   ├── NoFallScenario.java
│   │   └── ...
│
├── listeners/                // Les "Capteurs". Ils écoutent Spigot et appellent les Managers.
│   ├── PlayerListener.java   // Join, Quit, Chat.
│   ├── GameListener.java     // Death, Damage, Move (si nécessaire).
│   └── MenuListener.java     // Gestion des clics dans les inventaires (GUI).
│
├── commands/                 // Les "Inputs" commandes.
│   ├── CommandSd.java        // Ta commande principale /sd (avec sous-commandes).
│   └── CommandRef.java       // Commandes arbitres si besoin.
│
├── gui/                      // Gestion des Interfaces (Menus).
│   ├── GuiBuilder.java       // Une classe abstraite pour faciliter la création d'inventaires.
│   ├── TeamSelectionGui.java // Le menu de choix d'équipe.
│   └── AdminConfigGui.java   // Le menu OP pour configurer le jeu.
│
└── utils/                    // Outils techniques.
    ├── ItemBuilder.java      // INDISPENSABLE. Pour créer des items complexes en une ligne.
    ├── Title.java            // Pour envoyer des titles/actionbars facilement.
    └── ScoreboardUtils.java  // Wrapper pour gérer le scoreboard sans devenir fou.
```

## 2. Détails des modules clés

### A. Le **GameManager** (Le Chef d'orchestre)
C'est lui qui sait si le jeu est lancé ou non.

*   Il contient une variable `GameState currentState`.
*   Il contient les méthodes `setGameState(GameState newState)`.

**Logique** : Quand tu changes d'état (ex: de `WAITING` à `PLAYING`), le `setGameState` doit déclencher la téléportation des joueurs, le clear des inventaires, le lancement du timer, etc.

### B. Le Système de **Scénarios** (Modularité)
C'est là que ton expérience C++ objet va servir.

*   Crée une classe abstraite `Scenario` qui implémente `Listener` (l'interface Spigot).
*   Elle a un `String name`, `boolean active`, et des méthodes abstraites `onEnable()` / `onDisable()`.
*   Ton `ScenarioManager` tient une `List<Scenario>`.
*   Dans ton menu GUI, tu affiches la liste. Si on clique, tu toggles l'état et tu enregistres/désenregistres les events.

### C. Les **GUIs** (Inventory Menus)
Spigot gère les GUIs de façon très primitive : c'est juste un inventaire de coffre. Pour faire un menu :

*   Tu crées un `Inventory`.
*   Tu mets des items dedans (ex: Laine Rouge pour "Team Rouge").
*   Tu ouvres l'inventaire au joueur.

**Le piège** : Tu dois écouter `InventoryClickEvent`. Si le joueur clique, tu dois `event.setCancelled(true)` (pour pas qu'il vole l'item) et exécuter l'action associée. **Conseil** : Fais une classe abstraite `SkyDefenderGui` qui gère le "anti-vol" d'item de base.

### D. Le **Scoreboard**
Le scoreboard vanilla de Minecraft est complexe (packets ou API verbeuse). Tu devras créer une tâche répétitive (`BukkitRunnable`) qui tourne toutes les secondes (20 ticks) pour rafraîchir les lignes du scoreboard (Temps restant, Kills, Teams en vie).

## 3. Workflow de développement conseillé
Vu que tu pars de zéro en Java Spigot, code dans cet ordre pour ne pas te perdre :

*   **La base Team & State** : Code `GameState` et `TeamManager`. Fais en sorte de pouvoir rejoindre une team via commande.
*   **Le Loop** : Code le `GameManager` qui fait passer le jeu de "Lobby" à "Game" (tp les joueurs, donne des épées).
*   **L'UI (ItemBuilder)** : Code ta classe utilitaire `ItemBuilder` (Fluent API pattern) :

```java
new ItemBuilder(Material.IRON_SWORD).setName("Excalibur").addEnchant(..).toItemStack();
```

    Sans ça, créer des items en Java Spigot prend 15 lignes par item.

*   **Les Menus** : Fais le GUI de sélection d'équipe (clic sur item -> appel `TeamManager`).
*   **Les Scénarios** : Ajoute la couche modulaire à la fin.

## 4. Tips pour un dév C++/C

*   **Pointeurs/Références** : En Java, tout objet est une référence (un pointeur intelligent implicite). Si tu passes `Team` à une méthode et que tu modifies le nom dedans, il est modifié partout. Pas besoin de `&` ou `*`.
*   **Mémoire** : Pas de `free()` ou `delete`. Le Garbage Collector passe quand tu n'as plus de référence vers un objet. Mais attention aux Memory Leaks dans Spigot : si tu stockes un `Player` dans une `static List` et qu'il se déconnecte, le GC ne peut pas le nettoyer.
*   **Best Practice** : Stocke toujours les `UUID` des joueurs, pas les objets `Player` directement dans tes listes persistantes.
*   **Comparaison de String** : JAMAIS `string1 == string2`. En Java, `==` compare les adresses mémoire. Utilise toujours `string1.equals(string2)`.