package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.scene.control.Button;
import xyz.ryansbeanfactory.collectiondecartes.theme.ThemeManager;

import java.util.function.Consumer;

public class LargeButton extends Button {

    public LargeButton() {
        initialize();
    }

    public LargeButton(String text, Runnable onClick) {
        super(text);
        initialize();
        this.setOnAction(_ -> onClick.run());
    }

    public LargeButton(String text, Consumer<String> onClick) {
        super(text);
        initialize();
        this.setOnAction(_ -> onClick.accept(getText()));
    }

    private void initialize() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("element");
    }
}