package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.scene.control.Button;
import xyz.ryansbeanfactory.collectiondecartes.theme.ThemeManager;

import java.util.function.Consumer;
import java.util.function.Function;

public class LargeButton extends Button {

    public LargeButton() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("element");
    }

    public LargeButton(String text, Runnable onClick) {
        super(text);
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("element");

        this.setOnAction(_ -> onClick.run());
    }

    public LargeButton(String text, Consumer<String> onClick) {
        super(text);
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("element");

        this.setOnAction(_ -> onClick.accept(text));
    }
}
