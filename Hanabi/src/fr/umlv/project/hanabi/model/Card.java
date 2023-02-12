package fr.umlv.project.hanabi.model;

import java.util.*;

/**
 * Représentation d'une carte
 */
public final class Card {

    private final int couleur;
    private final int valeur;

    Card(int couleur, int valeur) {
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public int getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    // Utile pour s'approprier le contexte
    @Override
    public String toString() {
        return "[" + couleur + " - " + valeur + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return couleur == card.couleur &&
                valeur == card.valeur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(couleur, valeur);
    }

    public static Queue<Card> createCardDeck(int nbCouleurs, int[] nbCartesParRang, Random rnd) {
        var result = new LinkedList<Card>();
        for (int i = 0; i < nbCouleurs; i++) {
            for (int j = 0; j < nbCartesParRang.length; j++) {
                var nbCard = nbCartesParRang[j];
                for (int k = 0; k < nbCard; k++) {
                    // La valeur commence à 1
                    result.add(new Card(i, j + 1));
                }
            }
        }
        // On mélange
        Collections.shuffle(result, rnd);

        return result;
    }


}

