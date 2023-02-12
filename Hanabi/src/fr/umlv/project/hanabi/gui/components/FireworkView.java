package fr.umlv.project.hanabi.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class FireworkView implements UIComponent {

    private final int[] state;
    private final Color[] colors;
    private final Color[] numberColor;

    public FireworkView(int[] fireworkState, Color[] colors, Color[] numberColor) {
        this.state = fireworkState;
        this.colors = colors;
        this.numberColor = numberColor;
    }

    @Override
    public void draw(Graphics canvas) {
        // Firework is a split panel composed of CardFrontView
        var components = new ArrayList<UIComponent>();
        for (int i = 0; i < state.length; i++) {
            components.add(CardFront.cardWithMargin(colors[i], numberColor[i], "" + state[i], 10));
        }

        var splitPanel = SplitPanel.horizontal(components);

        splitPanel.draw(canvas);
    }
}
