package xyz.ryansbeanfactory.collectiondecartes.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import xyz.ryansbeanfactory.collectiondecartes.SceneManager;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.LargeButton;

import java.util.List;

public class LandingPage extends VBox {

    private static final double ISLAND_RADIUS = 12;
    private static final Color ISLAND_BG     = Color.web("#0d1526");
    private static final Color ISLAND_BORDER = Color.web("#1e2a44");

    private final List<String> languages;

    public LandingPage(List<String> languages) {
        this.languages = languages;
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

        CornerRadii radii = new CornerRadii(ISLAND_RADIUS);
        container.setBackground(new Background(
                new BackgroundFill(ISLAND_BG, radii, Insets.EMPTY)
        ));
        container.setBorder(new Border(
                new BorderStroke(ISLAND_BORDER, BorderStrokeStyle.SOLID, radii, new BorderWidths(1))
        ));

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