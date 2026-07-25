package xyz.ryansbeanfactory.collectiondecartes.theme;

import javafx.scene.Parent;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeManager {

    private static final String STYLE_FOLDER = "/xyz/ryansbeanfactory/collectiondecartes/styles";

    // every theme subfolder must contain exactly these files
    private static final List<String> STYLESHEET_NAMES = List.of(
            "base.css",
            "layout.css",
            "components.css",
            "flashcards.css"
    );

    private static ThemeManager instance;
    private Theme theme;
    private final List<Parent> registeredComponents = new ArrayList<>();
    private List<String> cachedStylesheetUrls = List.of();

    private ThemeManager() {
        theme = AppSession.getInstance().getTheme();
        cachedStylesheetUrls = loadStylesheetUrls(theme);

        AppSession.getInstance()
                .themeProperty()
                .addListener((observable, oldTheme, newTheme) -> {
                    this.theme = newTheme;
                    this.cachedStylesheetUrls = loadStylesheetUrls(newTheme);
                    updateAllComponents();
                });
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private List<String> loadStylesheetUrls(Theme theme) {
        String themeFolder = STYLE_FOLDER + "/" + theme.name().toLowerCase();
        List<String> urls = new ArrayList<>();

        for (String filename : STYLESHEET_NAMES) {
            String path = themeFolder + "/" + filename;
            URL resource = getClass().getResource(path);
            urls.add(
                    Objects.requireNonNull(resource, "Missing stylesheet: " + path)
                            .toExternalForm()
            );
        }
        return urls;
    }

    private void updateAllComponents() {
        for (Parent component : registeredComponents) {
            updateComponent(component);
        }
    }

    private void updateComponent(Parent component) {
        component.getStylesheets().setAll(cachedStylesheetUrls);
    }

    public void registerComponent(Parent component) {
        if (!registeredComponents.contains(component)) {
            registeredComponents.add(component);
            updateComponent(component);
        }
    }

    public void unregisterComponent(Parent component) {
        registeredComponents.remove(component);
    }

    public void unregisterAllComponents() {
        registeredComponents.clear();
    }
}