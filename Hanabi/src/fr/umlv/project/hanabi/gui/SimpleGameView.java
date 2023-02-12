package fr.umlv.project.hanabi.gui;

import fr.umlv.project.hanabi.gui.components.*;
import fr.umlv.project.hanabi.model.*;
import fr.umlv.zen5.ApplicationContext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleGameView {

    public static Builder builder(float width, float height) {
        var w = Math.round(width);
        var h = Math.round(height);
        if (w <= 0 || h <= 0) throw new IllegalArgumentException("Cannot make a view on a 0x0 canvas.");
        return new Builder(w, h);
    }

    private final int width;
    private final int height;
    private final SimpleGameData model;
    private final Color[] colors;
    private final Color[] numberColors;
    private final String[] colorNames;
    private UIComponent panel;
    private final Function<Card, Runnable> mkDiscardAction;
    private final Function<Card, Runnable> mkPlayAction;
    private final Function<Player, Function<Card, Runnable>> mkColorHintAction;
    private final Function<Player, Function<Card, Runnable>> mkNumberHintAction;

    private SimpleGameView(int width, int height, SimpleGameData model, Color[] colors, Color[] numberColors,
                           String[] colorNames, Function<Card, Runnable> mkDiscardAction,
                           Function<Card, Runnable> mkPlayAction, Function<Player, Function<Card, Runnable>> mkColorHintAction,
                           Function<Player, Function<Card, Runnable>> mkNumberHintAction) {
        this.width = width;
        this.height = height;
        this.model = model;
        this.colors = colors;
        this.numberColors = numberColors;
        this.mkDiscardAction = mkDiscardAction;
        this.mkPlayAction = mkPlayAction;
        this.mkColorHintAction = mkColorHintAction;
        this.mkNumberHintAction = mkNumberHintAction;
        this.colorNames = colorNames;
    }

    /**
     * Entry point to display this view.
     * Rendering is done by clearing the view entirely, then rendering the components
     * Rendering uses the supplied model to get data
     * This strategy is slow, as it always recompute the whole frame
     *
     * @param context
     */
    public void draw(ApplicationContext context) {
        context.renderFrame(canvas -> {
            // Step1: clear
            clear(canvas);
            canvas.clipRect(0, 0, width, height);
            // Step 2: Populate components
            panel = computeComponents();
            // Step 3: Set some hints for rendering
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // Step 4: draw what need to be drawn
            panel.draw(canvas);
        });
    }

    public void dispatchClick(Point2D.Float location) {
        // Forward to the split panel...
        panel.dispatchClick(location);
    }

    private void clear(Graphics2D canvas) {
        canvas.clearRect(0, 0, width, height);
    }

    private UIComponent computeComponents() {
        // We split the panel in two main region. One dedicated to cards, the other one for hints
        UIComponent leftPanel = makeLeftPanel();
        UIComponent rightPanel = makeRightPanel();
        // Cards | Hints
        // One line for SCo
        // One line of firework
        var topPart = SplitPanel.biHorizontalPanel8020(leftPanel, rightPanel);
        var bottomPart = makeHintsPanel();
        return SplitPanel.biVerticalPanel8020(topPart, bottomPart);
    }

    private UIComponent makeHintsPanel() {
        // Keeping the last 3 hints only
        List<Hint> allHints = model.getCurrentPlayer().getReceivedHints();
        var toDisplay = allHints.isEmpty() ? List.<Hint>of() : allHints.subList(Math.max(0, allHints.size() - 3), allHints.size());
        List<UIComponent> hints = toDisplay.stream().map(h -> {
            String txt;
            String positions = h.getConcernedIndexes().stream().mapToInt(it -> it + 1).mapToObj(String::valueOf).collect(Collectors.joining(",", "(", ")"));
            if (h instanceof ColorHint) {
                var hint = (ColorHint) h;
                txt = "Reçu au tour " + hint.getGameRound() + ": les cartes en position " + positions + " sont " + colorNames[hint.getColor()];
            } else {
                var hint = (NumberHint) h;
                var num = hint.getNumber();
                txt = "Reçu au tour " + hint.getGameRound() + ": les cartes en position " + positions + " sont des " + num;
            }
            return new Label(txt);
        }).collect(Collectors.toList());

        return SplitPanel.verticalPanel(hints);
    }

    private UIComponent makeLeftPanel() {
        // Left Panel is Split in two: Firework, and players decks
        var fireworkPanel = new FireworkView(this.model.getFireworkState(), colors, numberColors);

        // One player panel by player
        List<UIComponent> playerPanels = Arrays.stream(model.getPlayers()).map(p -> {
            if (p.equals(model.getCurrentPlayer())) {
                // panel is for current player
                return new CurrentPlayerPanel(p.getName(), p.getDeck(), mkDiscardAction, mkPlayAction, model.canDiscard());
            } else {
                return new PlayerPanel(p.getName(), p.getDeck(), colors, numberColors, model.canGiveHint(), mkColorHintAction.apply(p), mkNumberHintAction.apply(p));
            }
        }).collect(Collectors.toList());
        var playersPanel = SplitPanel.verticalPanel(playerPanels);

        return SplitPanel.biVerticalPanel2080(fireworkPanel, playersPanel);
    }

    private UIComponent makeRightPanel() {
        // Left panel is for game info.
        var gameRound = new Label("Tour:" + model.currentGameRound());
        var redTokens = new Label("Rouge: " + model.getRedTokens());
        var blueTokens = new Label("Bleu: " + model.getRemainingBlue());
        var score = new Label("Score :" + model.getScore());
        return SplitPanel.verticalPanel(List.of(score, gameRound, redTokens, blueTokens));
    }


    public static class Builder {
        private int width;
        private int height;
        private SimpleGameData model;
        private Color[] colors;
        private Color[] numberColors;
        private String[] colorNames;
        private Function<Card, Runnable> mkDiscardAction;
        private Function<Card, Runnable> mkPlayAction;
        private Function<Player, Function<Card, Runnable>> mkColorHintAction;
        private Function<Player, Function<Card, Runnable>> mkNumberHintAction;


        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Builder withModel(SimpleGameData model) {
            this.model = model;
            return this;
        }

        public Builder withSettings(Color[] cardColors, Color[] numberColors, String[] colorNames) {
            this.colors = cardColors;
            this.numberColors = numberColors;
            this.colorNames = colorNames;
            return this;
        }

        public Builder onDiscardClick(Function<Card, Runnable> mkDiscardAction) {
            this.mkDiscardAction = mkDiscardAction;
            return this;
        }

        public Builder onPlayClick(Function<Card, Runnable> mkPlayAction) {
            this.mkPlayAction = mkPlayAction;
            return this;
        }

        public Builder onColorHintClick(Function<Player, Function<Card, Runnable>> mkColorHintAction) {
            this.mkColorHintAction = mkColorHintAction;
            return this;
        }

        public Builder onNumberHintClick(Function<Player, Function<Card, Runnable>> mkNumberHintAction) {
            this.mkNumberHintAction = mkNumberHintAction;
            return this;
        }

        public SimpleGameView build() {
            Objects.requireNonNull(model);
            Objects.requireNonNull(mkColorHintAction);
            Objects.requireNonNull(mkDiscardAction);
            Objects.requireNonNull(mkNumberHintAction);
            Objects.requireNonNull(mkPlayAction);
            Objects.requireNonNull(colors);
            Objects.requireNonNull(numberColors);
            Objects.requireNonNull(colorNames);
            var nbColors = model.getNbColors();
            if (nbColors != colors.length || nbColors != numberColors.length || nbColors != colorNames.length)
                throw new IllegalArgumentException("Wrong settings");

            return new SimpleGameView(width, height, model, colors, numberColors, colorNames, mkDiscardAction, mkPlayAction, mkColorHintAction, mkNumberHintAction);
        }

    }


}
