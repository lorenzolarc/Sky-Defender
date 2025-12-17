# Sky Defender 🏰

**Sky Defender** est un plugin Minecraft Spigot (1.16.5) qui automatise le célèbre mode de jeu où une équipe de **Défenseurs** doit protéger une bannière dans un château volant (ou au sol) contre des vagues d'**Attaquants**.

![Version](https://img.shields.io/badge/Version-1.16.5-orange) ![Java](https://img.shields.io/badge/Java-8%2B-blue) ![Build](https://img.shields.io/badge/Build-Maven-green)

Le plugin est disponible sur [SpigotMC](https://www.spigotmc.org/resources/sky-defender.130861/) !

## 📋 Fonctionnalités

*   **Gestion Automatique** : Lobby, Démarrage, Téléportation, PvP Timer, Fin de partie.
*   **Configuration In-Game (GUI)** : Presque tout est configurable via un menu (Item Œil de l'Ender, il faut être op).
    *   Gestion des équipes (Création, Suppression, Couleurs, Taille).
    *   Éditeur de Kits (Glisser-déposer les items dans un inventaire).
    *   Paramètres de jeu (Taille de la map, Dispersion des téléportations, Temps PvP).
*   **Système de Scénarios Modulaire** : Activez des modules comme **CutClean** (Cuisson auto), NoFall, etc.
*   **Mode UHC** : Option pour désactiver la régénération naturelle.
*   **Gameplay** :
    *   Bannière à détruire pour gagner.
    *   Scoreboard dynamique (Kills, Timer, Boussole vers la bannière).
    *   Plaques de téléportation (Ascenseurs) pour les défenseurs.

## 🚀 Installation

1.  Téléchargez le fichier `.jar` (ou compilez-le).
2.  Placez-le dans le dossier `plugins/` de votre serveur Spigot 1.16.5.
3.  Relancez le serveur.

## ⚙️ Configuration d'une partie

Une fois connecté en tant qu'Opérateur (OP) :

1.  **Le Lobby** : À la connexion, vous recevez une boussole (Choix d'équipe) et un Œil de l'Ender (Config Admin).
2.  **Définir la zone de jeu** :
    *   Posez une bannière quelque part.
    *   Regardez-la et tapez : `/sd banner`
    *   Placez-vous au spawn des défenseurs et tapez : `/sd defenseur`
3.  **Configurer les équipes & Kits** :
    *   Faites un Clic-Droit avec l'**Œil de l'Ender**.
    *   Configurez les kits de départ, activez les scénarios (ex: CutClean) et ajustez la bordure.
4.  **Lancer la partie** :
    *   Tapez `/sd start`.

### Plaques de téléportation (Optionnel)
Pour créer des ascenseurs rapides pour les défenseurs :
1.  Posez une plaque de pression en or (**Light Weighted Pressure Plate**).
2.  Regardez la plaque du BAS et tapez `/sd tpplate low`.
3.  Regardez la plaque du HAUT et tapez `/sd tpplate high`.
4.  Les défenseurs peuvent maintenant se téléporter instantanément entre les deux.

## 🛠 Commandes

| Commande                     | Description                                                                                               | Permission |
|:-----------------------------|:----------------------------------------------------------------------------------------------------------| :--- |
| `/sd start`                  | Lance le compte à rebours de la partie.                                                                   | OP |
| `/sd banner`                 | Définit la bannière ciblée comme objectif.                                                                | OP |
| `/sd defenseur`              | Définit votre position comme spawn défenseur.                                                             | OP |
| `/sd tpplate <high/low>`     | Configure les plaques de TP.                                                                              | OP |
| `/sd revive <joueur> [team]` | Ressuscite un joueur mort. et l'ajoute dans la team (si spécifié)                                         | OP |
| `/sd invsee <joueur>`        | Permet d'ouvrir l'inventaire du joueur et de le modifier (uniquement si l'OP est dans la team spectateur) | OP |

## 🏗 Architecture du code (Pour les dévs)

Le projet suit une architecture stricte pour faciliter la maintenance :

*   `manager/` : Logique métier (GameManager, TeamManager, ScenarioManager).
*   `gui/` : Gestion des inventaires (Menus) avec pattern anti-vol d'item.
*   `listeners/` : Gestion des événements Bukkit.
*   `scenario/` : Système abstrait permettant d'ajouter des règles de jeu facilement (Polymorphisme).
*   `utils/` : Utilitaires (ItemBuilder, etc.).

Vous pouvez ajouter votre scénario juste [ici](doc/creer_un_scenario.md).

## 🛠 Contribution

Vous pouvez contribuer au projet via les [Pull Request](https://github.com/lorenzolarc/Sky-Defender/pulls) de GitHub.
S'il y a un bug/une fonctionnalité que vous voudriez voir, vous avez les [Issues GitHub](https://github.com/lorenzolarc/Sky-Defender/issues)

Vous êtes libre de télécharger/modifier/fork le projet. Cependant, gardez les mentions de mon nom dans le projet (et rajouter les vôtres à côté).

## Compilation
Le projet utilise **Maven**.
```bash
mvn clean package
```

## 📝 Auteurs
Développé par [Lorenzo LA ROCCA](https://github.com/lorenzolarc) en Java/Spigot 1.16.5.
