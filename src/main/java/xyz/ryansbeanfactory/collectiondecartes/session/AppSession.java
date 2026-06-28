package xyz.ryansbeanfactory.collectiondecartes.session;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import xyz.ryansbeanfactory.collectiondecartes.theme.Theme;

public class AppSession {

    private static AppSession instance;

    private DeckLanguage deckLanguage = DeckLanguage.FRENCH;
    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(Theme.DEFAULT);

    private AppSession() {}

    public static synchronized AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    public void setDeckLanguage(DeckLanguage deckLanguage) {
        this.deckLanguage = deckLanguage;
        System.out.println("DeckLanguage: " + this.deckLanguage);
    }

    public DeckLanguage getDeckLanguage() {
        return deckLanguage;
    }

    public void setTheme(Theme theme) {
        this.theme.set(theme);
    }

    public Theme getTheme() {
        return theme.get();
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
    }
}