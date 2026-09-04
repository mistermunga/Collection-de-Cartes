package xyz.ryansbeanfactory.collectiondecartes;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;

import java.util.Objects;

public class CarteApplication extends Application {

    private static CarteApplication instance;

    private Stage primaryStage;
    private DatabaseManager databaseManager;

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

        initializeDatabase();
        setUpFonts();

        SceneManager.getInstance().showLanding(databaseManager.getLanguages());
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
            Font font = Font.loadFont(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream(path)
                    ), 12
            );
            System.out.println(fontName + " -> family: " + font.getFamily());
        }
    }

    private void initializeDatabase() throws Exception {
        try {
            databaseManager = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("Database initialization failed\n" + e.getMessage());
            throw new Exception(e);
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}