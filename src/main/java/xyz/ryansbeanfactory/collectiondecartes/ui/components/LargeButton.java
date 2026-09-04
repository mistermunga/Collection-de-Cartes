package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class LargeButton extends Button {

    private static final double CORNER_RADIUS = 8;
    private static final double BORDER_WIDTH = 1;
    private static final CornerRadii RADII = new CornerRadii(CORNER_RADIUS);

    private static final Color BG_IDLE      = Color.web("#121c33");
    private static final Color BG_HOVER     = Color.web("#182444");
    private static final Color BG_ARMED     = Color.web("#0e1729");
    private static final Color BORDER_IDLE  = Color.web("#1e2a44");
    private static final Color BORDER_HOVER = Color.web("#2c3f66");
    private static final Color TEXT_COLOR   = Color.web("#e4ebf7");

    public LargeButton() {
        initialize();
    }

    public LargeButton(String text, Runnable onClick) {
        super(text);
        initialize();
        this.setOnAction(_ -> onClick.run());
    }

    public LargeButton(String text, Consumer<String> onClick) {
        super(text);
        initialize();
        this.setOnAction(_ -> onClick.accept(getText()));
    }

    private void initialize() {
        this.getStyleClass().add("element");

        this.setPadding(new Insets(14, 28, 14, 28));
        this.setTextFill(TEXT_COLOR);
        this.setCursor(Cursor.HAND);

        applyState(BG_IDLE, BORDER_IDLE);

        this.hoverProperty().addListener((_, _, isHovering) ->
                applyState(isHovering ? BG_HOVER : BG_IDLE, isHovering ? BORDER_HOVER : BORDER_IDLE));

        this.armedProperty().addListener((_, _, isArmed) -> {
            if (isArmed) {
                applyState(BG_ARMED, BORDER_HOVER);
            }
        });
    }

    private void applyState(Color background, Color border) {
        this.setBackground(new Background(
                new BackgroundFill(background, RADII, Insets.EMPTY)
        ));
        this.setBorder(new Border(
                new BorderStroke(border, BorderStrokeStyle.SOLID, RADII, new BorderWidths(BORDER_WIDTH))
        ));
    }
}