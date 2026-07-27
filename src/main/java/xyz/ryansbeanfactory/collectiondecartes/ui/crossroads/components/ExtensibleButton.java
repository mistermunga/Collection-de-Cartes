package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.components;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtensibleButton extends VBox {

    private final Button mainButton;
    private final List<Button> optionButtons = new ArrayList<>();

    private boolean expanded = false;

    public ExtensibleButton(String text, Map<String, Runnable> options) {

        mainButton = new Button(text);
        mainButton.setOnAction(event -> toggle());

        getChildren().add(mainButton);

        for (Map.Entry<String, Runnable> entry : options.entrySet()) {
            Button button = new Button(entry.getKey());

            button.setVisible(false);
            button.setManaged(false);

            button.setOnAction(event -> {
                entry.getValue().run();
                collapse();
            });

            optionButtons.add(button);
            getChildren().add(button);
        }
    }

    private void toggle() {
        if (expanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        expanded = true;

        for (Button button : optionButtons) {
            button.setVisible(true);
            button.setManaged(true);
        }
    }

    public void collapse() {
        expanded = false;

        for (Button button : optionButtons) {
            button.setVisible(false);
            button.setManaged(false);
        }
    }

    public void unSelect() {
        collapse();
    }
}
