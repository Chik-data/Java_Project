package fr.umlv.project.hanabi.model;

/**
 * Interface only used to get the current state of a game
 * This allows to keep the view to a "read only mode"
 */
public interface SimpleGameData {


    boolean canGiveHint();

    boolean canDiscard();

    boolean isFinished();

    int[] getFireworkState();

    Player[] getPlayers();

    int getScore();

    int currentGameRound();

    Player getCurrentPlayer();

    int getNbColors();

    int getRedTokens();

    int getRemainingBlue();
}
