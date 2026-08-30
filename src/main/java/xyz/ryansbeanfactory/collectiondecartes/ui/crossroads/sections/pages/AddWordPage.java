package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.NewWordForm;

public class AddWordPage extends VBox {

    public AddWordPage() {
        NewWordForm newWordForm = new NewWordForm();
        this.getChildren().add(newWordForm);
    }
}
