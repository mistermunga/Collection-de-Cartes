package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.util.StreakManager;

import java.sql.SQLException;

public class TitleBar extends HBox {

    public TitleBar() {
        this.setAlignment(Pos.CENTER);
        insertContents();
    }

    private void insertContents() {
        Label logo = new Label(
                AppSession.getInstance().getDeckLanguage().getAppName()
        );
        logo.getStyleClass().add("logo");

        Label streak = new Label(
                calculateStreak()
        );
        streak.getStyleClass().add("streak");

        Label settings = new Label("⚙");
        settings.getStyleClass().add("settings");

        this.getChildren().addAll(logo, streak, settings);
    }

    private String calculateStreak() {
        try(StreakManager streakManager = new StreakManager()) {
            return String.valueOf(streakManager.getCurrentStreak());
        } catch (SQLException e) {
            return "Unable to calculate streak";
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
