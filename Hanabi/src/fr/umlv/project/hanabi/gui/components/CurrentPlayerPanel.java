package fr.umlv.project.hanabi.gui.components;

import fr.umlv.project.hanabi.model.Card;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CurrentPlayerPanel implements UIComponent {

    private final String name;
    private final List<Card> deck;
    private final Function<Card, Runnable> mkDiscardAction;
    private final Function<Card, Runnable> mkPlayAction;
    private final boolean canDiscard;
    private UIComponent panel;

    public CurrentPlayerPanel(String name, List<Card> deck, Function<Card, Runnable> mkDiscardAction, Function<Card, Runnable> mkPlayAction, boolean canDiscard) {
        this.name = name;
        this.deck = deck;
        this.mkDiscardAction = mkDiscardAction;
        this.mkPlayAction = mkPlayAction;
        this.canDiscard = canDiscard;
    }

    @Override
    public void draw(Graphics canvas) {
        // Almost like Player panel, except we use a Card back, and different actions.
        // TODO factorize code with PlayerPanel !
        var bounds = canvas.getClipBounds();
        var label = Label.createLabel(10, "->" + name);
        var cards = mkCardsPanel();
        panel = SplitPanel.biVerticalPanel2080(label, cards);
        panel.draw(canvas.create(0, 0, bounds.width, bounds.height));
    }

    private UIComponent mkCardsPanel() {
        var cards = new ArrayList<UIComponent>();
        for (Card card : deck) {
            cards.add(new CardBackWithActions(mkDiscardAction.apply(card), mkPlayAction.apply(card), canDiscard));
        }
        return SplitPanel.horizontal(cards);
    }


    @Override
    public void dispatchClick(Point2D.Float location) {
        panel.dispatchClick(location); // Forwarding the event

    }
}
