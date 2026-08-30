package xyz.ryansbeanfactory.collectiondecartes.session;

import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;

import java.util.Set;

public enum DeckLanguage {
    FRENCH("Collection de cartes", Set.of(Gender.MASCULINE, Gender.FEMININE)),
    ENGLISH("Card Collection", Set.of(Gender.NONE)),
    SPANISH("Colección de cartas", Set.of(Gender.MASCULINE, Gender.FEMININE)),
    GERMAN("Kartensammlung", Set.of(Gender.MASCULINE, Gender.FEMININE, Gender.NEUTER));

    private final String appName;
    private final Set<Gender> genders;

    DeckLanguage(String appName, Set<Gender> genders) {
        this.appName = appName;
        this.genders = genders;
    }

    public String getAppName() {
        return appName;
    }

    public Set<Gender> getGenders() {
        return genders;
    }
}
