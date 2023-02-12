package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class Label implements UIComponent {

    public static UIComponent createLabel(int margin, String text) {
        var label = new Label(text);
        return new MarginComponent(margin, label);
    }

    private final String label;

    public Label(String label) {
        this.label = label;
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        Rectangle2D textBounds = null;
        float fontSize = bounds.height * 0.5f;

        while (textBounds == null || textBounds.getBounds().width > bounds.width) {
            var font = canvas.getFont().deriveFont(fontSize);
            canvas.setFont(font);
            FontRenderContext fontRenderContext = ((Graphics2D) canvas).getFontRenderContext();
            textBounds = font.getStringBounds(label, fontRenderContext);
            //var gv = font.layoutGlyphVector(fontRenderContext, text.toCharArray(), 0, text.length(), GlyphVector.FLAG_MASK);
            //var b = gv.getVisualBounds();
            fontSize--;
        }
        var b = textBounds;

        var textX = bounds.width / 2 - b.getBounds().width / 2;
        var textY = bounds.height / 2 + b.getBounds().height / 2;

        canvas.setColor(Color.BLACK);
        canvas.drawString(label, textX, textY);

    }
}
