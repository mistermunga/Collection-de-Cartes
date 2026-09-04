package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.util.StreakManager;

import java.sql.SQLException;

public class TitleBar extends HBox {

    private static final Color BG_PANEL     = Color.web("#0d1526");
    private static final Color LINE_DIM     = Color.web("#1e2a44");
    private static final Color RED          = Color.web("#e14b4b");
    private static final Color RED_BRIGHT   = Color.web("#ff5f5f");
    private static final Color RED_TINT     = Color.web("#e14b4b", 0.12);

    private static final double PILL_RADIUS = 999;

    public TitleBar() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(16, 32, 16, 32));
        this.setSpacing(16);

        this.setBackground(new Background(
                new BackgroundFill(BG_PANEL, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        this.setBorder(new Border(
                new BorderStroke(
                        LINE_DIM,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 0, 1, 0)
                )
        ));

        insertContents();
    }

    private void insertContents() {
        Label logo = new Label(
                AppSession.getInstance().getDeckLanguage().getAppName()
        );
        logo.getStyleClass().add("logo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label streak = buildStreakBadge();

        Label settings = new Label("\u2699");
        settings.getStyleClass().add("settings-icon");

        this.getChildren().addAll(logo, spacer, streak, settings);
    }

    private Label buildStreakBadge() {
        Label streak = new Label("\uD83D\uDD25 " + calculateStreak());
        streak.getStyleClass().addAll("font-semibold");
        streak.setStyle("-fx-font-size: 13px;");
        streak.setTextFill(RED_BRIGHT);
        streak.setPadding(new Insets(6, 14, 6, 14));

        CornerRadii radii = new CornerRadii(PILL_RADIUS);
        streak.setBackground(new Background(
                new BackgroundFill(RED_TINT, radii, Insets.EMPTY)
        ));
        streak.setBorder(new Border(
                new BorderStroke(RED, BorderStrokeStyle.SOLID, radii, new BorderWidths(1))
        ));

        return streak;
    }

    private String calculateStreak() {
        try (StreakManager streakManager = new StreakManager()) {
            return String.valueOf(streakManager.getCurrentStreak());
        } catch (SQLException e) {
            return "--";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}