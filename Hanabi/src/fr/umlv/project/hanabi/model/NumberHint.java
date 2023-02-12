package fr.umlv.project.hanabi.model;

import java.util.List;

public final class NumberHint extends Hint {

    private final int number;

    NumberHint(int gameRound, List<Integer> concernedIndexes, int number) {
        super(gameRound, concernedIndexes);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
