package fr.umlv.project.hanabi.model;

import java.util.Arrays;

class Firework {

    private final int[] etat;

    Firework(int nbCouleurs) {
        this.etat = new int[nbCouleurs];
        for (int i = 0; i < nbCouleurs; i++) {
            this.etat[i] = 0;
        }
    }

    /**
     * @param c
     * @return true si l'opération a marché, false sinon
     */
    public boolean ajouterCarte(Card c) {
        // Regarder si la carte est compatible
        var derniereValeurSurCouleur = etat[c.getCouleur()];
        // Si la dernière valeur joue sur la couleur est juste inférieure à celle de la carte à jouer alors, c'est bon
        if (derniereValeurSurCouleur + 1 == c.getValeur()) {
            // On accepte la carte
            etat[c.getCouleur()] = c.getValeur();
            // On retourne ok
            return true;
        } else {
            // Sinon, on a eu tort de jouer cette carte
            return false;
        }
    }


    public int currentScore() {
        int count = 0;
        for (int i : etat) {
            count += i;
        }
        return count;
    }


    public int[] state() {
        return Arrays.copyOf(etat, etat.length);
    }

    public int getNbColors() {
        return state().length;
    }
}
