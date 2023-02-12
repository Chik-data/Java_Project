package fr.umlv.project.hanabi.gui.components;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContainer implements UIComponent {

    private Map<Rectangle, UIComponent> renderedComponents = new HashMap<>();


    @Override
    public final void dispatchClick(Point2D.Float location) {
        var key = renderedComponents.keySet().stream().filter(it -> it.contains(location)).findFirst();
        key.ifPresent(k -> {
            var component = renderedComponents.get(k);
            component.dispatchClick(new Point2D.Float(location.x - k.x, location.y - k.y));
        });
    }

    /**
     * Register the component as rendered
     *
     * @param r
     * @param c
     * @return
     */
    protected final UIComponent register(Rectangle r, UIComponent c) {
        this.renderedComponents.put(r, c);
        return c;
    }
}
