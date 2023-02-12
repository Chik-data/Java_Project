package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public class CardBack implements UIComponent {

    public CardBack() {
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        canvas.setColor(Color.DARK_GRAY);

        // TODO, add this to a common method for card front & back
        var offset = Math.abs(bounds.height - bounds.width) / 2;
        // Arc is 10% of the size
        int cardSize = Math.min(bounds.height, bounds.height);
        var arc = Math.round(cardSize * 0.1f);
        var x = 0;
        var y = 0;
        var w = bounds.width - 1;
        var h = bounds.height - 1;
        if (bounds.height > bounds.width) {
            // Higher than wider: Adjust vertically
            y = offset;
            h = h - 2 * offset;
        } else {
            // Wider than higher: Adjust horizontally
            x = offset;
            w = w - 2 * offset;
        }
        canvas.fillRoundRect(x, y, w, h, arc, arc);
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        // Nothing to do !
    }
}
