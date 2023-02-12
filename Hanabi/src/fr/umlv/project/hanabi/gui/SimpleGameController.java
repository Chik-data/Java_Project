package fr.umlv.project.hanabi.gui;

import fr.umlv.project.hanabi.model.Game;
import fr.umlv.project.hanabi.model.Player;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;

import java.awt.Color;
import java.util.Random;

public class SimpleGameController {

    static void simpleGame(ApplicationContext context) {
        // get the size of the screen
        ScreenInfo screenInfo = context.getScreenInfo();
        float width = screenInfo.getWidth();
        float height = screenInfo.getHeight();
        System.out.println("size of the screen (" + width + " x " + height + ")");


        // Initialization
        // Random a besoin d'une seed, pour ne pas rejouer les mêmes parties,
        // On peut utiliser comme seed le temps: le hasard de la partie va dépendre du
        // moment où on lance le programme.
        var rand = new Random(System.currentTimeMillis());

        Color[] cards = new Color[]{Color.MAGENTA, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK};
        Color[] numbers = new Color[]{Color.BLACK, Color.ORANGE, Color.BLACK, Color.BLACK, Color.BLACK};
        String[] names = new String[]{"magenta", "bleu", "jaune", "orange", "rose"};

        // Model
        Player p1 = new Player("Player1");
        Player p2 = new Player("Player2");
        Player[] players = {p1, p2};
        Game game = new Game(players, rand, 8);

        // Prepare a view
        var view = SimpleGameView.builder(width, height)
                .withModel(game)
                .withSettings(cards, numbers, names)
                .onColorHintClick(player -> card -> () -> {
                    if (game.canGiveHint()) {
                        game.sendColorHint(player, card);
                    }
                })
                .onNumberHintClick(player -> card -> () -> {
                    if (game.canGiveHint()) {
                        game.sendNumberHint(player, card);
                    }
                })
                .onDiscardClick(card -> () -> {
                    if (game.canDiscard()) {
                        game.discard(card);
                    }
                })
                .onPlayClick(card -> () -> game.playCard(card))
                .build();

        view.draw(context);


        while (!game.isFinished()) {
            var evt = context.pollOrWaitEvent(10);
            if (evt == null) continue; // Loop again

            // Dispatch event
            var action = evt.getAction();
            // Considering we are only counting clicks with the up event
            // Exit Action: "Q" or "q"
            switch (action) {
                case POINTER_UP:
                    view.dispatchClick(evt.getLocation());
                    break;
                case KEY_PRESSED:
                case KEY_RELEASED:
                    context.exit(0);
                    return;
            }

            view.draw(context);
        }

        System.out.println("Game finished with score: " + game.getScore());

    }

    public static void main(String[] args) {

        Application.run(Color.LIGHT_GRAY, SimpleGameController::simpleGame);

    }
}
