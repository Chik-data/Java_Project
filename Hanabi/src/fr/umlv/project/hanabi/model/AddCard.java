package fr.umlv.project.hanabi.model;

@Deprecated
public class AddCard implements GameAction {

    private final Player player;
    private final int card;

    @Deprecated
    public AddCard(Player player, int card) {
        this.player = player;
        this.card = card;
    }

    public AddCard(int card) {
        this.card = card;
        this.player = null;
    }

    @Override
    public boolean execute(Game game) {
        return game.addCardToFirework(card);
    }
}
