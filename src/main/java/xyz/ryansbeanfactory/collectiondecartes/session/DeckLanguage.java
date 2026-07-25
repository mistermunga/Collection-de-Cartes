package xyz.ryansbeanfactory.collectiondecartes.session;

public enum DeckLanguage {
    FRENCH("Collection de cartes"),
    ENGLISH("Card Collection"),
    SPANISH("Colección de cartas");

    private final String appName;

    DeckLanguage(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }
}
