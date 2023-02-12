package fr.umlv.project.hanabi.model;

import java.util.List;

public final class ColorHint extends Hint {

    private final int color;

    ColorHint(int gameRound, List<Integer> concernedIndexes, int color) {
        super(gameRound, concernedIndexes);
        this.color = color;
    }

    public int getColor() {
        return color;
    }


}
