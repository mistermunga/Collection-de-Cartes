package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.components.ExtensibleButton;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

import java.util.Map;

public class NavBar extends VBox {

    public NavBar(MainView parent) {

        String insertText = switch (AppSession.getInstance().getDeckLanguage()) {
            case ENGLISH -> "New Card";
            case FRENCH -> "Nouvelle carte";
            case SPANISH -> "Nueva carta";
            case GERMAN -> "Neue Karte";
        };

        ExtensibleButton insertButton = new ExtensibleButton(
                insertText,
                Map.of(
                        "New Word",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.NEW_WORD)),
                        "New Phrase",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.NEW_PHRASE))
                )
        );

        ExtensibleButton flashcardButton = new ExtensibleButton(
                "Flashcards",
                Map.of(
                        "Flashcard Session",
                        () -> Platform.runLater(() -> parent.setPage(NavOption.FLASHCARDS))
                )
        );

        getChildren().addAll(insertButton, flashcardButton);
    }
}