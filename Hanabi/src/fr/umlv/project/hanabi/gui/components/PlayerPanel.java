package fr.umlv.project.hanabi.gui.components;

import fr.umlv.project.hanabi.model.Card;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A logical assembly of UIComponents which consists in the view for a currently non playing player's deck
 * The cards are visible, with two actions to give hints (if possible)
 */
public class PlayerPanel implements UIComponent {

    private final String name;
    private final List<Card> deck;
    private final Color[] cardColors;
    private final Color[] numberColors;
    private final boolean canGiveHint;
    private UIComponent panel;
    private final Function<Card, Runnable> makeColorHint;
    private final Function<Card, Runnable> makeNumberHint;


    public PlayerPanel(String name, List<Card> deck, Color[] cardColors, Color[] numberColors, boolean canGiveHint, Function<Card, Runnable> makeColorHint, Function<Card, Runnable> makeNumberHint) {
        this.name = name;
        this.deck = deck;
        this.cardColors = cardColors;
        this.numberColors = numberColors;
        this.canGiveHint = canGiveHint;
        this.makeColorHint = makeColorHint;
        this.makeNumberHint = makeNumberHint;
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        var label = Label.createLabel(10, name);
        var cards = mkCardsPanel();
        panel = SplitPanel.biVerticalPanel2080(label, cards);
        panel.draw(canvas.create(0, 0, bounds.width, bounds.height));
    }

    private UIComponent mkCardsPanel() {
        var cards = new ArrayList<UIComponent>();
        for (Card card : deck) {
            cards.add(new CardFrontWithActions(card.getValeur(), cardColors[card.getCouleur()], numberColors[card.getCouleur()], 5, makeColorHint.apply(card), makeNumberHint.apply(card), canGiveHint));
        }
        return SplitPanel.horizontal(cards);
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        panel.dispatchClick(location); // Simple forward
    }
}
