package fr.umlv.project.hanabi.gui.components;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Component with pixel size margin
 */
public class MarginComponent extends AbstractContainer {

    private int margin;
    private UIComponent child;

    public MarginComponent(int margin, UIComponent child) {
        this.margin = margin;
        this.child = child;
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        int width = bounds.width - 2 * margin;
        int height = bounds.height - 2 * margin;
        register(new Rectangle(margin, margin, width, height), child);
        child.draw(canvas.create(margin, margin, width, height));
    }
}
