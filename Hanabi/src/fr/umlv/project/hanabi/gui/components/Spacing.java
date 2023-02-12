package fr.umlv.project.hanabi.gui.components;

import java.awt.Graphics;

/**
 * A component whose rendering is nothing (it just fill space)
 */
public class Spacing implements UIComponent {

    public static final Spacing SPACE = new Spacing();

    private Spacing() {
    }

    @Override
    public void draw(Graphics canvas) {
        // Do nothing at all
    }
}
