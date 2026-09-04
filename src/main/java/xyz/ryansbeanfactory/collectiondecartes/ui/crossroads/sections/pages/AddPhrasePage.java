package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.NewPhraseForm;

public class AddPhrasePage extends VBox {

    public AddPhrasePage() {
        this.setAlignment(Pos.TOP_CENTER);

        NewPhraseForm newPhraseForm = new NewPhraseForm();
        this.getChildren().add(newPhraseForm);
    }
}