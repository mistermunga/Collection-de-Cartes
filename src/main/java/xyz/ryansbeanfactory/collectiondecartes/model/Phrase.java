package xyz.ryansbeanfactory.collectiondecartes.model;

public record Phrase(long id, String lemma, String definition) {

    /**
     * Convenience constructor for phrases that haven't been persisted yet.
     * id = 0 signals "not yet inserted"; the repository hands back a new
     * Phrase with the real id once it's saved.
     */
    public Phrase(String lemma, String definition) {
        this(0L, lemma, definition);
    }
}