package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;

public class Button implements UIComponent {

    private final Runnable action;
    private final String text;

    public Button(String text, Runnable onClick) {
        this.action = onClick;
        this.text = text;
    }

    @Override
    public void dispatchClick(Point2D.Float location) {
        // Click on the button, runs the action !
        action.run();
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();

        // Draw a rectangle
        canvas.setColor(Color.GRAY);
        canvas.fillRect(0, 0, bounds.width - 1, bounds.height - 1);
        canvas.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bounds.width - 1, bounds.height - 1);

        // Draw text
        // TODO: this is a copy of label. avoid that !
        float fontSize = bounds.height * 0.5f;
        var font = canvas.getFont().deriveFont(fontSize);
        canvas.setFont(font);
        FontRenderContext fontRenderContext = ((Graphics2D) canvas).getFontRenderContext();
        var textBounds = font.getStringBounds(text, fontRenderContext);
        //var gv = font.layoutGlyphVector(fontRenderContext, text.toCharArray(), 0, text.length(), GlyphVector.FLAG_MASK);
        //var b = gv.getVisualBounds();
        var b = textBounds;
        var textX = bounds.width / 2 - b.getBounds().width / 2;
        var textY = bounds.height / 2 + b.getBounds().height / 2;

        canvas.setColor(Color.BLACK);
        canvas.drawString(text, textX, textY);
    }
}
