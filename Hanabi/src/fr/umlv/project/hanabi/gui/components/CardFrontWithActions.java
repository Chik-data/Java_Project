package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Composite View : A front card and two buttons (for number hint and color hint)
 */
public class CardFrontWithActions implements UIComponent {

    private final Color cardColor;
    private final Color numberColor;
    private final int margin;
    private final Runnable colorHint;
    private final Runnable numberHint;
    private final int valeur;
    private final boolean canGiveHint;
    private UIComponent panel;

    public CardFrontWithActions(int value, Color cardColor, Color numberColor, int margin, Runnable colorHint, Runnable numberHint, boolean canGiveHint) {
        this.cardColor = cardColor;
        this.numberColor = numberColor;
        this.margin = margin;
        this.colorHint = colorHint;
        this.numberHint = numberHint;
        this.valeur = value;
        this.canGiveHint = canGiveHint;

    }


    @Override
    public void draw(Graphics canvas) {
        var c = CardFront.cardWithMargin(cardColor, numberColor, "" + valeur, margin);
        UIComponent actionsPanel = canGiveHint ? SplitPanel.horizontal(List.of(new Button("Chiffre", numberHint), new Button("Couleur", colorHint))) : Spacing.SPACE;
        this.panel = SplitPanel.biVerticalPanel8020(c, new MarginComponent(10, actionsPanel));
        panel.draw(canvas);
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        panel.dispatchClick(location);
    }
}
