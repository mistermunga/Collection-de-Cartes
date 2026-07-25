package xyz.ryansbeanfactory.collectiondecartes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;
import xyz.ryansbeanfactory.collectiondecartes.theme.ThemeManager;
import xyz.ryansbeanfactory.collectiondecartes.ui.LandingPage;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.CrossRoads;

import java.util.List;

public class SceneManager {

    private final CarteApplication application;
    private static SceneManager instance;

    private SceneManager() {
        application = CarteApplication.getInstance();
    }

    public static SceneManager getInstance() {
        return instance = instance == null ?
                new SceneManager() :
                instance;
    }

    public void showLanding(List<String> languages) {
        Stage stage = application.getPrimaryStage();
        LandingPage landing = new LandingPage(languages);
        Scene scene = new Scene(landing);

        stage.setMaximized(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public void showCrossRoads(String language) {
        ThemeManager.getInstance().unregisterAllComponents();
        AppSession.getInstance().setDeckLanguage(DeckLanguage.valueOf(language.toUpperCase()));

        Stage stage = application.getPrimaryStage();
        CrossRoads crossRoads = new CrossRoads();
        Scene scene = new Scene(crossRoads);

        stage.setMaximized(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }
}
