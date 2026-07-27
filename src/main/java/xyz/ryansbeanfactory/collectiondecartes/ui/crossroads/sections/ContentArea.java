package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.scene.layout.StackPane;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages.DefaultView;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

public class ContentArea extends StackPane {

    public ContentArea(MainView parent) {}

    private void updateContent(NavOption page) {
        switch (page) {
            case NEW_WORD -> this.getChildren().setAll();
            case NEW_PHRASE ->  this.getChildren().setAll();
            case FLASHCARDS -> this.getChildren().setAll();
            default -> this.getChildren().setAll(new DefaultView());
        }
    }
}
