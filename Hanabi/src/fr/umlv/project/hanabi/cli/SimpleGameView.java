package fr.umlv.project.hanabi.cli;

import fr.umlv.project.hanabi.model.*;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleGameView {

    private final Game game;
    private final String[] colors;
    private final Scanner s = new Scanner(System.in);

    // Expressions régulières pour les commandes
    // Example : "poser 4" => signifie poser la 4ème carte de la main sur le firework
    private static final Pattern addCardSyntax = Pattern.compile("(poser\\s+(\\d+))");
    // Example: "indiquer à toto chiffre 3" => signifie donner une indication à toto pour toutes les cartes contenant le chiffre 3
    private static final Pattern giveHintNumberSyntax = Pattern.compile("(indiquer\\s+à\\s+(\\w+)\\schiffre\\s+(\\d+))");
    // Exemple: "indiquer à jean couleur rouge"=> signifie donner une indication à jean pour toutes les cartes de couleur rouge
    private static final Pattern giveHintColorSyntax = Pattern.compile("(indiquer\\s+à\\s+(\\w+)\\scouleur\\s+(\\w+))");
    // Exemple: "defausser 3" => défausser la 3ème carte de la main
    private static final Pattern discardSyntax = Pattern.compile("(defausser\\s+(\\d+))");

    public SimpleGameView(Game game, String[] colors) {
        this.game = game;
        this.colors = colors;
    }

    void printPlayerDeck(Player p) {
        System.out.println("Main de " + p.getName());
        var builder = new StringBuilder("| ");
        for (Card card : p.getDeck()) {
            builder.append(card.getValeur()).append(' ').append(colors[card.getCouleur()]).append(" | ");
        }
        System.out.println(builder.toString());
    }

    void printFirework() {
        int[] state = game.getFireworkState();
        StringBuilder builder = new StringBuilder("| ");
        for (int i = 0; i < state.length; i++) {
            builder.append(colors[i]).append(":").append(state[i]).append(" | ");

        }
        System.out.println("Firework:");
        System.out.println(builder.toString());
    }

    void render(Player p) {
        System.out.println("Joueur " + p.getName() + " c'est à toi! Tu as " + p.countCards() + " cartes en main.");
        printFirework();
        for (Player player : game.getPlayers()) {
            if (!player.equals(p)) {
                printPlayerDeck(player);
            }
        }
        // Print des indices reçus...
        printHints(p);
    }

    private void printHints(Player p) {
        System.out.println("Indices reçus");
        for (Hint reveivedHint : p.getReceivedHints()) {
            var positions = reveivedHint.getConcernedIndexes().stream().map(it -> String.valueOf(it + 1)).collect(Collectors.joining(","));

            if (reveivedHint instanceof ColorHint) {
                var hint = (ColorHint) reveivedHint;
                var color = colors[hint.getColor()];
                System.out.println("Reçu au tour " + hint.getGameRound() + ": les cartes en position " + positions + " sont " + color);
            } else {
                var hint = (NumberHint) reveivedHint;
                var num = hint.getNumber();
                System.out.println("Reçu au tour " + hint.getGameRound() + ": les cartes en position " + positions + " sont des " + num);
            }
        }

    }

    void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    /**
     * Une vraie valeur de retour serait une structure de donnée type Either : Soit Success soit Error. Mais c'est un peu avancé ;)
     *
     * @return la saisie du nombre de joueurs. -1 si saisie invalide erreur.
     */
    int promptNbJoueurs(int maxJoueurs) {

        var nbJoueurs = -1;
        if (s.hasNextInt()) {
            var read = s.nextInt();
            if (read > 1 && read <= maxJoueurs) {
                nbJoueurs = read;
            }
            s.nextLine(); // Necessaire !
        }
        if (nbJoueurs != -1) {
            return nbJoueurs;
        }
        System.out.println("Saisie invalide. Entrez un nombre entre 2 et " + maxJoueurs);
        // Fonction récursive . Cela peut poser des soucis si le joueur se trompe un certain nombre de fois...
        return promptNbJoueurs(maxJoueurs);
    }


    GameAction promptAction(int gameRound, Player p, boolean hintIsPossible, boolean discardIsPossible) {
        System.out.println("Tour " + gameRound + "- Joueur " + p.getId() + ' ' + p.getName() + " que veux tu faire ?");
        GameAction action = null;
        while (action == null) {
            var line = s.nextLine();
            // Checking different commands
            if (discardSyntax.matcher(line).matches()) {
                // Discard ?
                action = checkDiscardAction(line, p, discardIsPossible);
            } else if (addCardSyntax.matcher(line).matches()) {
                action = checkAddCard(line, p);
            } else if (giveHintColorSyntax.matcher(line).matches()) {
                action = checkColorHint(gameRound, line, p, hintIsPossible);
            } else if (giveHintNumberSyntax.matcher(line).matches()) {
                action = checkNumberHint(gameRound, line, p, hintIsPossible);
            } else {
                // Mauvais input
                System.out.println("Entrez une commande valide svp !");
            }
        }
        return action;

    }


    private GiveHint checkColorHint(int gameRound, String command, Player p, boolean hintIsPossible) {
        if (hintIsPossible) {
            var matcher = giveHintColorSyntax.matcher(command);
            matcher.matches();
            var playerName = matcher.group(2);
            var colorName = matcher.group(4);
            // Trouver le joueur et la couleur
            var dest = game.findPlayer(playerName);
            if (dest == null) {
                System.out.println("Le joueur " + playerName + " n'existe pas!");
                return null;
            }
            var color = Arrays.binarySearch(colors, 0, colors.length, colorName);
            if (color == -1) {
                System.out.println("La couleur " + colorName + " n'existe pas!");
                return null;
            }
            return game.createColorHint(playerName, color, gameRound);
        } else {
            System.out.println("Cette action n'est pas possible il n'y a plus de jetons bleus dans la boite!");
        }
        return null;
    }

    private GiveHint checkNumberHint(int gameRound, String command, Player p, boolean hintIsPossible) {
        if (hintIsPossible) {
            var matcher = giveHintNumberSyntax.matcher(command);
            matcher.matches();
            var playerName = matcher.group(2);
            var number = Integer.parseInt(matcher.group(3));
            // Trouver le joueur et la couleur
            var dest = game.findPlayer(playerName);
            if (dest == null) {
                System.out.println("Le joueur " + playerName + " n'existe pas!");
                return null;
            }

            if (number <= 0 || number > 5) {
                System.out.println("Il faut un chiffre entre 0 et 5. " + number + " n'y est pas!");
                return null;
            }
            return game.createNumberHint(playerName, number, gameRound);
        } else {
            System.out.println("Cette action n'est pas possible il n'y a plus de jetons bleus dans la boite!");
        }
        return null;
    }

    private Discard checkDiscardAction(String command, Player p, boolean discardIsPossible) {
        if (discardIsPossible) {
            // Get the matcher
            var matcher = discardSyntax.matcher(command);
            matcher.matches();
            var card = Integer.parseInt(matcher.group(2)) - 1;
            // Check de l'index
            if (card < p.countCards()) {
                return new Discard(card, p);
            } else {
                printInvalidIndex();
                return null;
            }
        }
        System.out.println("Cette action n'est pas possible: tous les jetons bleus sont dans la boite!");
        return null;
    }

    private AddCard checkAddCard(String command, Player p) {
        var matcher = addCardSyntax.matcher(command);
        matcher.matches();
        var card = Integer.parseInt(matcher.group(2)) - 1; // Pour les joueurs le compte commence à 1, pour nous, à 0
        // Check de l'index
        if (card < p.countCards()) {
            return new AddCard(p, card);
        }
        printInvalidIndex();
        return null;
    }

    private void printInvalidIndex() {
        System.out.println("L'indice de la carte est invalide, tu n'as pas assez de cartes en main!");
    }

    public void printScore() {
        int score = game.getScore();
        System.out.println("Score: " + score);
        if (score <= 5) {
            System.out.println("Horrible");
        } else if (score < 11) {
            System.out.println("Médiocre");
        } else if (score < 16) {
            System.out.println("Honorable");
        } else if (score < 25) {
            System.out.println("Extraordinaire");
        } else {
            System.out.println("Légendaire");
        }
    }
}
