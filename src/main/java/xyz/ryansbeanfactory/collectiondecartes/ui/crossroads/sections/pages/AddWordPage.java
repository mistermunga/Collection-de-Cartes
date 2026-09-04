package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.NewWordForm;

public class AddWordPage extends VBox {

    public AddWordPage() {
        this.setAlignment(Pos.TOP_CENTER);

        NewWordForm newWordForm = new NewWordForm();
        this.getChildren().add(newWordForm);
    }
}