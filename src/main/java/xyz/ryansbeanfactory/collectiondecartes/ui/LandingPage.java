package xyz.ryansbeanfactory.collectiondecartes.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.SceneManager;
import xyz.ryansbeanfactory.collectiondecartes.theme.ThemeManager;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.LargeButton;

import java.util.List;

public class LandingPage extends VBox {

    private final List<String> languages;

    public LandingPage(List<String> languages) {
        this.languages = languages;

        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().addAll("root", "landing-root");

        this.setAlignment(Pos.CENTER);
        this.setSpacing(40);
        this.setPadding(new Insets(60, 40, 60, 40));
        this.setFillWidth(false);

        showLanguageOptions();

    }

    private void showLanguageOptions() {
        HBox container = new HBox();
        container.getStyleClass().addAll("container", "island");
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);
        container.setPadding(new Insets(30, 30, 30, 30));
        for (String language : languages) {
            LargeButton button = new LargeButton(
                    language,
                    () -> SceneManager.getInstance().showCrossRoads(language)
            );
            container.getChildren().add(button);
        }

        this.getChildren().add(container);
    }

}
