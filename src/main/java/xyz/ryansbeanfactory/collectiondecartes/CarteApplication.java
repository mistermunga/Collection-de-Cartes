package xyz.ryansbeanfactory.collectiondecartes;

import javafx.application.Application;
import javafx.stage.Stage;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;

import java.util.List;

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
        List<String> databases = initializeDatabases();

        SceneManager.getInstance().showLanding(databases);
    }

    private List<String> initializeDatabases() throws Exception {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            return databaseManager.getLanguages();
        } catch (Exception e) {
            System.err.println("Database initialization failed \n" + e.getMessage());
            throw new Exception();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
