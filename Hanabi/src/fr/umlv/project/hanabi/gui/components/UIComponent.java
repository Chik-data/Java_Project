package fr.umlv.project.hanabi.gui.components;


import java.awt.Graphics;
import java.awt.geom.Point2D;

/*
    A component in the UI
    Every component has his own coordinate system. A component is in a bounded rectangle
 */
public interface UIComponent {

    /**
     * Draw the component using the provided graphics
     *
     * @param canvas
     */
    void draw(Graphics canvas);

    /**
     * Forwards a click to the component
     *
     * @param location Location of the click
     */
    default void dispatchClick(Point2D.Float location) {
        // By default, we do nothing at all.
    }

}
