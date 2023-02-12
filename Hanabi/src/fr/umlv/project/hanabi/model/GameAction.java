package fr.umlv.project.hanabi.model;

/**
 * Modélise une action du jeu
 */
@Deprecated
public interface GameAction {


    /**
     * Exécute l'action sur la partie
     *
     * @param game
     * @return true si la partie est finie. false tant que la partie continue
     */
    boolean execute(Game game);


}
