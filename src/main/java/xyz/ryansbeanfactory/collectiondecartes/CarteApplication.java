package xyz.ryansbeanfactory.collectiondecartes;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;

import java.util.List;
import java.util.Objects;

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
        setUpFonts();

        SceneManager.getInstance().showLanding(databases);
    }

    private void setUpFonts() {
        final String[] fonts = {
                "Manrope-Regular.ttf",
                "Manrope-Medium.ttf",
                "Manrope-SemiBold.ttf",
                "Manrope-Bold.ttf"
        };

        for (String fontName : fonts) {
            String path = "/xyz/ryansbeanfactory/collectiondecartes/fonts/" + fontName;
            Font _ = Font.loadFont(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream(path)
                    ), 14
            );
        }
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
