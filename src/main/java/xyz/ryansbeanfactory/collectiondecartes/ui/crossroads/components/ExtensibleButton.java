package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.components;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ExtensibleButton extends VBox {

    private final Map<String, Runnable> options;
    private final Button mainButton;

    public ExtensibleButton(String text, Map<String, Runnable> options) {
        this.options = options;
        this.mainButton = new Button(text);
        this.mainButton.setOnAction(event -> onClick());
        this.getChildren().add(mainButton);
    }

    public void onClick() {
        unSelect();
        for (Map.Entry<String, Runnable> entry : options.entrySet()) {
            Button button = new Button(entry.getKey());
            button.setOnAction(event -> {
                entry.getValue().run();
                collapse();
            });
            this.getChildren().add(button);
        }
    }

    private void collapse() {
        this.getChildren().clear();
        this.getChildren().add(mainButton);
    }

    public void unSelect() {
        this.getChildren().clear();
    }
}
