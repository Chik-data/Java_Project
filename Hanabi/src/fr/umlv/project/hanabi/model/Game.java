package fr.umlv.project.hanabi.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

public final class Game implements SimpleGameData {
    private int NB_COLORS = 5;// Par défaut, 5 couleurs
    private int[] DEFAULT_DISTRIBUTION = new int[]{3, 2, 2, 2, 1}; // Par défaut, 3 cartes 1, 2 cartes 2, etc.

    private final Queue<Card> deck;
    private final Player[] players;
    private final Firework firework;
    private int givenRedTokens;
    private final int maxPossibleScore;
    private final int totalBlueTokens;
    private int remainingBlue;
    private int currentPlayerIndex = 0; // Le joueur qui va jouer est à l'index 1;
    private int gameRound = 1; // Game starts at round 1
    private boolean lastRound = false;
    private int lastPlayerToPlay;
    private int lastRoundNumber;

    /**
     * Créer une partie
     *
     * @param players
     * @param r
     * @param totalBlueTokens
     */
    public Game(Player[] players, Random r, int totalBlueTokens) {
        Objects.requireNonNull(r);
        Objects.requireNonNull(players);
        if (players.length < 2) {
            throw new IllegalArgumentException("On ne peut pas jouer tout seul. Il faut au moins être 2");
        }
        if (totalBlueTokens < 8) {
            throw new IllegalArgumentException("On a besoin de 8 jetons bleus minimum");
        }
        this.deck = Card.createCardDeck(NB_COLORS, DEFAULT_DISTRIBUTION, r);
        this.totalBlueTokens = totalBlueTokens;
        this.remainingBlue = totalBlueTokens;
        this.players = players;
        this.givenRedTokens = 0;
        this.firework = new Firework(NB_COLORS); // Feu vide
        this.maxPossibleScore = NB_COLORS * DEFAULT_DISTRIBUTION.length; // Pour faciliter l'évoution quand on pourra faire évoluer la distribution
        // Distribution des cartes selon le nombre de joueurs
        distribuer();
    }

    /**
     * Distribue les cartes
     */
    private void distribuer() {
        int nbCards;
        if (players.length == 2 || players.length == 3) {
            nbCards = 4;
        } else {
            nbCards = 5;
        }
        // Distribution des cartes
        for (Player player : players) {
            for (int i = 0; i < nbCards; i++) {
                player.addCard(deck.poll());
            }
        }
    }

    /**
     * Internal method used to simulate fishing in the deck
     *
     * @return a card if possible. null if the deck is empty.
     */
    private Card fish() {
        var card = deck.poll();
        if (card == null && !lastRound) {
            this.lastRound = true;
            this.lastPlayerToPlay = currentPlayerIndex;
            this.lastRoundNumber = gameRound + 1;
        }
        return card;
    }

    /**
     * Indicates if the give hint action is possible
     *
     * @return true if it is, false otherwise
     */
    @Override
    public boolean canGiveHint() {
        return this.remainingBlue != 0;
    }

    /**
     * Indicates if the discard action is possible.
     *
     * @return true if it is possible, false otherwise
     */
    @Override
    public boolean canDiscard() {
        return this.remainingBlue < this.totalBlueTokens;
    }

    /**
     * Sending a hint to the player.
     *
     * @param h the hint
     * @return true if the game is over, false otherwise
     */
    boolean sendHint(Player player, Hint h) {
        // Action seulement possible si il reste des jetons bleus dans le couvercle
        if (canGiveHint()) {
            player.addHint(h);
            this.remainingBlue--;
        } else {
            throw new IllegalStateException("On ne peut pas donner d'indice si il n'y a pas de jetons bleus dans le couvercle.");
        }
        nextPlayer();
        return isFinished();
    }


    /**
     * Returns false while the game is not ended
     *
     * @param cardIndex
     * @return
     */
    @Deprecated
    boolean discard(int cardIndex) {
        Player player = getCurrentPlayer();
        var card = player.getDeck().get(cardIndex);
        return discard(card);
    }

    public boolean discard(Card card) {
        Player player = getCurrentPlayer();
        if (!player.getDeck().contains(card)) {
            throw new IllegalArgumentException("Cannot play a card which is not in the deck");
        }
        if (canDiscard()) {
            player.dropCard(card); // Défausser une carte
            Card c = this.fish(); // Piocher
            // Ajoute la carte dans la main du joueur si il y avait effectivement une pioche
            if (c != null) {
                player.addCard(c);
            }
            // On remet un token bleu dans la boite:
            this.remainingBlue++;
        } else {
            // Dans ce cas, on fait une action interdite:
            throw new IllegalStateException("On ne peut pas défausser quand tous les jetons bleus sont dans le couvercle.");
        }
        nextPlayer();
        // Le jeu peut-être fini si c'était le dernier joueur et la dernière action, on vérifie:
        return isFinished();

    }

    /**
     * Current player adds a card to the firework
     *
     * @param card
     * @return
     */
    @Deprecated
    boolean addCardToFirework(int card) {
        Player player = getCurrentPlayer();
        var c = player.getDeck().get(card);
        return playCard(c);
    }

    @Override
    public boolean isFinished() {
        // Le jeu est terminé si il y a 3 jetons rouge dans la boite
        // Ou si on ne peut plus jouer de cartes (les joueurs n'en ont plus en main)
        return givenRedTokens == 3 || lastRoundEnded() || getScore() == maxPossibleScore;
    }

    private boolean lastRoundEnded() {
        // procédure  de dernier tour lancée, dernier tour atteint et dernier joueur vient de jouer
        return lastRound && gameRound == lastRoundNumber && currentPlayerIndex == lastPlayerToPlay;
    }

    @Override
    public int[] getFireworkState() {
        return firework.state();
    }

    @Override
    public Player[] getPlayers() {
        return players;
    }

    @Override
    public int getScore() {
        return firework.currentScore();
    }

    public Player findPlayer(String playerName) {
        Player p = null;
        for (int i = 0; p == null && i < players.length; i++) {
            if (players[i].getName().equalsIgnoreCase(playerName)) {
                p = players[i];
            }
        }
        return p;
    }

    @Deprecated
    public GiveHint createColorHint(String playerName, int color, int gameRound) {
        var player = findPlayer(playerName);
        if (player == null) {
            throw new IllegalArgumentException(playerName + " n'est pas un joueur.");
        }

        if (color < 0 || color >= firework.getNbColors()) {
            throw new IllegalArgumentException(color + " n'est pas un index de couleur valide");
        }

        // Compute hint
        var matching = new ArrayList<Integer>();
        var deck = player.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            var card = deck.get(i);
            if (card.getCouleur() == color) {
                matching.add(i);
            }
        }
        return new GiveHint(player, new ColorHint(gameRound, matching, color));
    }

    private Hint colorHint(Player player, Card c) {
        var matching = new ArrayList<Integer>();
        var deck = player.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            var card = deck.get(i);
            if (card.getCouleur() == c.getCouleur()) {
                matching.add(i);
            }
        }
        return new ColorHint(gameRound, matching, c.getCouleur());
    }

    private Hint numberHint(Player player, Card c) {
        var matching = new ArrayList<Integer>();
        var deck = player.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            var card = deck.get(i);
            if (card.getValeur() == c.getValeur()) {
                matching.add(i);
            }
        }
        return new NumberHint(gameRound, matching, c.getValeur());
    }

    @Deprecated
    public GiveHint createNumberHint(String playerName, int number, int gameRound) {
        var player = findPlayer(playerName);
        if (player == null) {
            throw new IllegalArgumentException(playerName + " n'est pas un joueur.");
        }

        if (number < 0 || number > 5) {
            throw new IllegalArgumentException(number + " n'est pas un chiffre valide.");
        }

        // Compute hint
        var matching = new ArrayList<Integer>();
        var deck = player.getDeck();
        for (int i = 0; i < deck.size(); i++) {
            var card = deck.get(i);
            if (card.getValeur() == number) {
                matching.add(i);
            }
        }
        return new GiveHint(player, new NumberHint(gameRound, matching, number));
    }

    /**
     * Terminate the current player round by going on to the next player
     * This method must be called at the end of each action.
     */
    private void nextPlayer() {
        // We just cycle through the possible indexes of the player array
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        // Increment game round every time we are back to index 0
        if (currentPlayerIndex == 0) {
            gameRound++;
        }
    }

    @Override
    public int currentGameRound() {
        return gameRound;
    }

    @Override
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    @Override
    public int getNbColors() {
        return NB_COLORS;
    }

    public void sendColorHint(Player player, Card card) {
        sendHint(player, colorHint(player, card));
    }

    public boolean playCard(Card card) {
        // Get card in current player's deck
        Player player = getCurrentPlayer();
        if (!player.getDeck().contains(card)) {
            throw new IllegalArgumentException("Cannot play a card which is not in the deck");
        }

        System.out.println("Adding card to the firework...");

        // Le joueur sort la carte
        var toAdd = player.dropCard(card);
        // On ajoute la carte au feu d'artifice
        boolean success = firework.ajouterCarte(toAdd);
        if (!success) {
            givenRedTokens++;
        }
        // Piocher
        var drawn = fish();
        if (drawn != null) {
            player.addCard(drawn);
        }
        nextPlayer();
        return isFinished();
    }

    public void sendNumberHint(Player player, Card card) {
        sendHint(player, numberHint(player, card));
    }

    @Override
    public int getRedTokens() {
        return givenRedTokens;
    }

    @Override
    public int getRemainingBlue() {
        return remainingBlue;
    }
}
