package fr.umlv.project.hanabi.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {

    /**
     *
     */
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    private final String name;
    private final List<Card> ownDeck;
    private final int id;
    private final List<Hint> receivedHints;

    public Player(String name) {
        this.name = name;
        this.ownDeck = new LinkedList<>();
        this.id = idGenerator.getAndIncrement();
        this.receivedHints = new LinkedList<>();
    }

    /**
     * Jette la carte à l'indice c dans la main du joueur
     *
     * @param c
     * @return la carte que l'on jette
     */
    Card dropCard(int c) {
        var card = ownDeck.get(c);
        // On enlève la carte de la main du joueur
        ownDeck.remove(card);
        return card;
    }

    Card dropCard(Card c) {
        ownDeck.remove(c);
        return c;
    }

    void addCard(Card c) {
        ownDeck.add(c);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int countCards() {
        return ownDeck.size();
    }

    /**
     * Retourne une copie du jeu du joueur, qui ne peut pas être modifiée
     */
    public List<Card> getDeck() {
        return List.copyOf(ownDeck);
    }

    public void addHint(Hint hint) {
        this.receivedHints.add(hint);
    }

    public List<Hint> getReceivedHints() {
        return List.copyOf(receivedHints);
    }

    /**
     * Equality is only based on id field
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
