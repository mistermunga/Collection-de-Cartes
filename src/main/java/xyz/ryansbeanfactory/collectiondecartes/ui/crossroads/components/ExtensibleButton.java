package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.components;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtensibleButton extends VBox {

    private static final Color BG_HOVER    = Color.web("#182444");
    private static final Color BG_EXPANDED = Color.web("#121c33");
    private static final Color BORDER_ON   = Color.web("#2c3f66");
    private static final Color TRANSPARENT = Color.TRANSPARENT;

    private static final double RADIUS = 8;
    private static final CornerRadii RADII = new CornerRadii(RADIUS);

    private final Button mainButton;
    private final Label chevron;
    private final List<Button> optionButtons = new ArrayList<>();

    private boolean expanded = false;

    public ExtensibleButton(String text, Map<String, Runnable> options) {
        this.setSpacing(2);

        chevron = new Label("\u2304");
        chevron.getStyleClass().add("nav-sub-label");

        mainButton = buildRowButton(text, chevron, true);
        mainButton.setOnAction(_ -> toggle());
        mainButton.setOnMouseEntered(_ -> {
            if (!expanded) applyState(mainButton, BG_HOVER, BORDER_ON);
        });
        mainButton.setOnMouseExited(_ -> {
            if (!expanded) applyState(mainButton, TRANSPARENT, TRANSPARENT);
        });
        applyState(mainButton, TRANSPARENT, TRANSPARENT);

        getChildren().add(mainButton);

        for (Map.Entry<String, Runnable> entry : options.entrySet()) {
            Button button = buildRowButton(entry.getKey(), null, false);
            button.setVisible(false);
            button.setManaged(false);
            button.setOpacity(0);

            button.setOnMouseEntered(_ -> applyState(button, BG_HOVER, TRANSPARENT));
            button.setOnMouseExited(_ -> applyState(button, TRANSPARENT, TRANSPARENT));
            applyState(button, TRANSPARENT, TRANSPARENT);

            button.setOnAction(_ -> {
                entry.getValue().run();
                collapse();
            });

            optionButtons.add(button);
            getChildren().add(button);
        }
    }

    /**
     * Builds a full-width, borderless Button whose visible content is an
     * HBox bound to the button's own width — this is what lets the label
     * sit flush left and (optionally) a trailing chevron sit flush right,
     * which a plain Labeled graphic/text pairing can't pin to the edge.
     */
    private Button buildRowButton(String text, Label trailing, boolean indentNone) {
        Label label = new Label(text);
        label.getStyleClass().add(indentNone ? "nav-label" : "nav-sub-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(label, spacer);
        row.setAlignment(Pos.CENTER_LEFT);
        if (trailing != null) {
            row.getChildren().add(trailing);
        }

        Button button = new Button();
        button.setGraphic(row);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setCursor(Cursor.HAND);

        Insets padding = indentNone
                ? new Insets(12, 12, 12, 12)
                : new Insets(10, 12, 10, 32);
        button.setPadding(padding);

        row.prefWidthProperty().bind(
                button.widthProperty().subtract(padding.getLeft() + padding.getRight())
        );

        return button;
    }

    private void applyState(Button button, Color background, Color border) {
        button.setBackground(new Background(
                new BackgroundFill(background, RADII, Insets.EMPTY)
        ));
        button.setBorder(new Border(
                new BorderStroke(border, BorderStrokeStyle.SOLID, RADII, new BorderWidths(1))
        ));
    }

    private void toggle() {
        if (expanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        expanded = true;
        applyState(mainButton, BG_EXPANDED, BORDER_ON);
        rotateChevron(180);

        for (Button button : optionButtons) {
            button.setVisible(true);
            button.setManaged(true);
            fade(button, 0, 1);
        }
    }

    public void collapse() {
        expanded = false;
        applyState(mainButton, TRANSPARENT, TRANSPARENT);
        rotateChevron(0);

        for (Button button : optionButtons) {
            FadeTransition fade = fade(button, button.getOpacity(), 0);
            fade.setOnFinished(_ -> {
                button.setVisible(false);
                button.setManaged(false);
            });
        }
    }

    public void unSelect() {
        collapse();
    }

    private void rotateChevron(double toAngle) {
        RotateTransition rotate = new RotateTransition(Duration.millis(150), chevron);
        rotate.setToAngle(toAngle);
        rotate.play();
    }

    private FadeTransition fade(Button button, double from, double to) {
        FadeTransition fade = new FadeTransition(Duration.millis(150), button);
        fade.setFromValue(from);
        fade.setToValue(to);
        fade.play();
        return fade;
    }
}