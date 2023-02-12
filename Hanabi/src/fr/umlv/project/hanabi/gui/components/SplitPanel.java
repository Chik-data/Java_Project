package fr.umlv.project.hanabi.gui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A panel which splits space in a direction with the provided percentages
 */
public class SplitPanel extends AbstractContainer {

    public static UIComponent verticalPanel(List<UIComponent> components) {
        if (components.isEmpty()) return Spacing.SPACE;
        return new SplitPanel(Direction.VERTICAL, components, computeEquiSize(components));
    }

    public static UIComponent horizontal(List<UIComponent> components) {
        if (components.isEmpty()) return Spacing.SPACE;
        return new SplitPanel(Direction.HORIZONTAL, components, computeEquiSize(components));
    }

    public static SplitPanel biVerticalPanel8020(UIComponent up, UIComponent down) {
        return new SplitPanel(Direction.VERTICAL, List.of(up, down), List.of(0.8, 0.2));
    }

    public static SplitPanel biVerticalPanel2080(UIComponent up, UIComponent down) {
        return new SplitPanel(Direction.VERTICAL, List.of(up, down), List.of(0.2, 0.8));
    }

    public static SplitPanel biHorizontalPanel8020(UIComponent left, UIComponent right) {
        return new SplitPanel(Direction.HORIZONTAL, List.of(left, right), List.of(0.8, 0.2));
    }

    private static List<Double> computeEquiSize(List<UIComponent> components) {
        double size = 1.0 / components.size();
        var percentages = new ArrayList<Double>();
        for (int i = 0; i < components.size(); i++) {
            percentages.add(size);
        }
        return percentages;
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private final Direction direction;
    private final List<UIComponent> components;
    private final List<Double> percentages;

    public SplitPanel(Direction d, List<UIComponent> components, List<Double> percentages) {
        Objects.requireNonNull(components);
        Objects.requireNonNull(percentages);
        if (components.size() != percentages.size()) {
            throw new IllegalArgumentException("This layout relies on percentages to alocate space");
        }
        if (!percentages.stream().allMatch(it -> it > 0.0d && it <= 1.0d)) {
            throw new IllegalArgumentException("Expecting percentages (0.0 to 1.0)");
        }
        if (percentages.stream().mapToDouble(it -> it).sum() > 1.0) {
            throw new IllegalArgumentException("We are expecting a total of 100%, not more.");
        }

        this.direction = Objects.requireNonNull(d);
        this.components = List.copyOf(components);
        this.percentages = List.copyOf(percentages);
    }

    @Override
    public void draw(Graphics canvas) {
        var bounds = canvas.getClipBounds();
        var height = bounds.height;
        var width = bounds.width;
        int x = 0;
        int y = 0;
        for (int i = 0; i < components.size(); i++) {
            // Draw every component in appropriate box
            switch (direction) {
                case VERTICAL:
                    var h = (int) Math.round(height * percentages.get(i));
                    components.get(i).draw(canvas.create(x, y, width, h));
                    // Adding the component to the map
                    register(new Rectangle(x, y, width, h), components.get(i));
                    y += h;
                    break;
                case HORIZONTAL:
                    var w = (int) Math.round(width * percentages.get(i));
                    components.get(i).draw(canvas.create(x, y, w, height));
                    register(new Rectangle(x, y, w, height), components.get(i));
                    x += w;
            }
        }
    }


}
