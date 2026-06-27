package xyz.ryansbeanfactory.collectiondecartes;

import javafx.application.Application;
import javafx.stage.Stage;

public class CarteApplication extends Application {

    private static CarteApplication instance;
    private Stage primaryStage;

    static {
        System.setProperty("file.encoding", "UTF-8");
    }

    public static CarteApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;

        SceneManager.getInstance().showLanding();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
