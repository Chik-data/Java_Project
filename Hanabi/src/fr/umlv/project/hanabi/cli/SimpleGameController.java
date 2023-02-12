package fr.umlv.project.hanabi.cli;

import fr.umlv.project.hanabi.model.Game;
import fr.umlv.project.hanabi.model.Player;

import java.util.Random;

public class SimpleGameController {

    private static final String[] DEFAULT_COLORS = new String[]{
            "Rouge", "Bleu", "Vert", "Jaune", "Noir"
    };

    private static final int DEFAULT_NB_BLEU = 8;

    public static void main(String[] args) {

        // Initialization
        // Random a besoin d'une seed, pour ne pas rejouer les mêmes parties,
        // On peut utiliser comme seed le temps: le hasard de la partie va dépendre du
        // moment où on lance le programme.
        var rand = new Random(System.currentTimeMillis());

        // Model
        Player p1 = new Player("Player1");
        Player p2 = new Player("Player2");
        Player[] players = {p1, p2};
        Game game = new Game(players, rand, DEFAULT_NB_BLEU);

        // View
        var view = new SimpleGameView(game, DEFAULT_COLORS);

        // Game time !
        // L'algorithme général :
        // Tant que le jeu n'est pas fini, itérer le tableau des joueurs
        // -> afficher l'état
        // -> demander l'action à réaliser
        // -> effectuer cette action
        int round = 1;
        while (!game.isFinished()) {
            // Loop through the players...
            for (Player player : game.getPlayers()) {
                if (!game.isFinished()) {
                    // Print game for this player
                    view.render(player);
                    // Seulement si le jeu n'est pas fini...
                    var action = view.promptAction(round, player, game.canGiveHint(), game.canDiscard());
                    action.execute(game); // Exécuter l'action
                    // Clear screen
                    view.clearScreen(); // Not sure !
                }
            }
            round++;
        }
        view.printScore();

    }

}
