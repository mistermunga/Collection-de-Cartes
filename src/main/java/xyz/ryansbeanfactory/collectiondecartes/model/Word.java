package xyz.ryansbeanfactory.collectiondecartes.model;

import xyz.ryansbeanfactory.collectiondecartes.model.refs.*;

public record Word(long id, String lemma, String definition, PartOfSpeech partOfSpeech, Gender gender) {

    /**
     * Convenience constructor for words that haven't been persisted yet.
     * id = 0 signals "not yet inserted"; the repository hands back a new
     * Word with the real id once it's saved.
     */
    public Word(String lemma, String definition, PartOfSpeech partOfSpeech, Gender gender) {
        this(0L, lemma, definition, partOfSpeech, gender);
    }
}