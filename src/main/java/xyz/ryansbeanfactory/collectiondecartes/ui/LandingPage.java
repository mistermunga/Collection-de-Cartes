package xyz.ryansbeanfactory.collectiondecartes.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LandingPage extends VBox {

    public LandingPage() {
        Label text = new Label();
        text.setText("Welcome to Cartes");
        this.getChildren().add(text);
    }

}
