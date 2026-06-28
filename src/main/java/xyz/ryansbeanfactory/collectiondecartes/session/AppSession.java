package xyz.ryansbeanfactory.collectiondecartes.session;

public class AppSession {

    private static AppSession instance;
    private DeckLanguage deckLanguage = DeckLanguage.FRENCH;

    public AppSession() {
        instance = AppSession.getInstance();
    }

    public static AppSession getInstance() {
        return instance = instance == null ?
                new AppSession()
                : instance;
    }

    public void setDeckLanguage(DeckLanguage deckLanguage) {
        this.deckLanguage = deckLanguage;
    }

    public DeckLanguage getDeckLanguage() {
        return deckLanguage;
    }

}
