package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;

public class CardFront implements UIComponent {

    public static UIComponent cardWithMargin(Color color, Color numberColor, String txt, int margin) {
        var card = new CardFront(color, numberColor, txt);
        return new MarginComponent(margin, card);
    }

    private final Color color;
    private final Color numberColor;
    private final String text;

    public CardFront(Color color, Color numberColor, String txt) {
        this.color = color;
        this.numberColor = numberColor;
        this.text = txt;
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        // Compute max possible square
        canvas.setColor(color);
        // Check size
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
        canvas.setColor(Color.BLACK);
        canvas.drawRoundRect(x, y, w, h, arc, arc);

        // Number now
        float fontSize = 0.15f * cardSize;
        var font = canvas.getFont().deriveFont(fontSize);
        canvas.setFont(font);
        FontRenderContext fontRenderContext = ((Graphics2D) canvas).getFontRenderContext();
        var textBounds = font.getStringBounds(text, fontRenderContext);
        //var gv = font.layoutGlyphVector(fontRenderContext, text.toCharArray(), 0, text.length(), GlyphVector.FLAG_MASK);
        //var b = gv.getVisualBounds();
        var b = textBounds;
        var textX = bounds.width / 2 - b.getBounds().width / 2;
        var textY = bounds.height / 2 + b.getBounds().height / 2;

        canvas.setColor(numberColor);
        canvas.drawString(text, textX, textY);
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        // Handle click on me !
        System.out.println("Click sur carte " + color.toString() + " de valeur " + text);
    }
}
