package fr.umlv.project.hanabi.gui.components;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

public class CardBackWithActions implements UIComponent {

    private final Runnable discardAction;
    private final Runnable playAction;
    private final boolean canDiscard;
    private UIComponent panel;

    public CardBackWithActions(Runnable discardAction, Runnable playAction, boolean canDiscard) {
        this.discardAction = discardAction;
        this.playAction = playAction;
        this.canDiscard = canDiscard;
    }


    @Override
    public void draw(Graphics canvas) {
        var c = new CardBack();
        Button playButton = new Button("Poser", playAction);
        UIComponent actionsPanel = canDiscard ? SplitPanel.horizontal(List.of(new Button("Déffausser", discardAction), playButton)) : playButton;
        this.panel = SplitPanel.biVerticalPanel8020(c, new MarginComponent(10, actionsPanel));
        panel.draw(canvas);
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        panel.dispatchClick(location); // Forwarding
    }
}
