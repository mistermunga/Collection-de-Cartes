package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.components.ExtensibleButton;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

import java.util.Map;

public class NavBar extends VBox {

    private static final Color BG_PANEL_ALT = Color.web("#121c33");
    private static final Color LINE_DIM     = Color.web("#1e2a44");

    private static final double FIXED_WIDTH = 240;

    public NavBar(MainView parent) {
        this.setPrefWidth(FIXED_WIDTH);
        this.setMinWidth(FIXED_WIDTH);
        this.setMaxWidth(FIXED_WIDTH);

        this.setPadding(new Insets(24, 16, 24, 16));
        this.setSpacing(8);

        this.setBackground(new Background(
                new BackgroundFill(BG_PANEL_ALT, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        this.setBorder(new Border(
                new BorderStroke(
                        LINE_DIM,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 1, 0, 0)
                )
        ));

        ExtensibleButton insertButton = getInsertButton(parent);

        ExtensibleButton flashcardButton = new ExtensibleButton(
                "Flashcards",
                Map.of(
                        "Flashcard Session",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.FLASHCARDS))
                )
        );

        ExtensibleButton dictionaryButton = new ExtensibleButton(
                "Dictionary",
                Map.of(
                        "View Cards",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.DICTIONARY))
                )
        );

        getChildren().addAll(insertButton, flashcardButton, dictionaryButton);
    }

    private static ExtensibleButton getInsertButton(MainView parent) {
        String insertText = switch (AppSession.getInstance().getDeckLanguage()) {
            case ENGLISH -> "New Card";
            case FRENCH -> "Nouvelle carte";
            case SPANISH -> "Nueva carta";
            case GERMAN -> "Neue Karte";
        };

        return new ExtensibleButton(
                insertText,
                Map.of(
                        "New Word",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.NEW_WORD)),
                        "New Phrase",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.NEW_PHRASE))
                )
        );
    }
}