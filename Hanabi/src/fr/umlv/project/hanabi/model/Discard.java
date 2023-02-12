package fr.umlv.project.hanabi.model;

/**
 * Modélise l'action de défausser une carte
 */
@Deprecated
public class Discard implements GameAction {

    private final int cardIndex;
    private final Player player;

    @Deprecated
    public Discard(int cardIndex, Player player) {
        this.cardIndex = cardIndex;
        this.player = player;
    }

    public Discard(int cardIndex) {
        this.cardIndex = cardIndex;
        this.player = null; // TODO delete
    }

    @Override
    public boolean execute(Game game) {
        return game.discard(cardIndex);
    }
}
