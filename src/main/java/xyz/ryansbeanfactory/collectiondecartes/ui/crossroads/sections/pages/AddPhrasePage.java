package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.NewPhraseForm;

public class AddPhrasePage extends VBox {

    public AddPhrasePage() {
        NewPhraseForm newPhraseForm = new NewPhraseForm();
        this.getChildren().add(newPhraseForm);
    }

}
