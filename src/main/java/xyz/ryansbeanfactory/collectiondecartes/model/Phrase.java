package xyz.ryansbeanfactory.collectiondecartes.model;

public class Phrase {
    private long id;
    private String lemma;
    private String definition;

    public Phrase(String lemma, String definition) {
        this.lemma = lemma;
        this.definition = definition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
