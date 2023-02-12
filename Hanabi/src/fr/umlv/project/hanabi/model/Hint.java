package fr.umlv.project.hanabi.model;

import java.util.List;

public abstract class Hint {

    private final int gameRound;
    private final List<Integer> concernedIndexes;

    /**
     * An hint is made of two things: the round in which the hint was given
     * and a list of indices to indicate which indexes
     *
     * @param gameRound
     * @param concernedIndexes
     */
    Hint(int gameRound, List<Integer> concernedIndexes) {
        this.gameRound = gameRound;
        this.concernedIndexes = concernedIndexes;
    }

    public int getGameRound() {
        return gameRound;
    }

    public List<Integer> getConcernedIndexes() {
        return List.copyOf(concernedIndexes);
    }
}
