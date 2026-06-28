package xyz.ryansbeanfactory.collectiondecartes.model;

import xyz.ryansbeanfactory.collectiondecartes.model.refs.*;

public class Word {
    private long id;
    private String lemma;
    private String definition;
    private PartOfSpeech partOfSpeech;
    private Gender gender;

    public Word(String lemma, String definition, PartOfSpeech partOfSpeech, Gender gender) {
        this.lemma = lemma;
        this.definition = definition;
        this.partOfSpeech = partOfSpeech;
        this.gender = gender;
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

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}