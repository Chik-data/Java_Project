package fr.umlv.project.hanabi.model;

@Deprecated
public class GiveHint implements GameAction {

    private final Player player;
    private final Hint hint;

    GiveHint(Player p, Hint hint) {
        player = p;
        this.hint = hint;
    }

    @Override
    public boolean execute(Game game) {
        game.sendHint(player, hint);
        return true;
    }
}
