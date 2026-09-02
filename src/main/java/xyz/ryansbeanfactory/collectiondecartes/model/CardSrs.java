package xyz.ryansbeanfactory.collectiondecartes.model;

import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardState;

import java.time.Instant;
import java.util.Optional;

/**
 * One-to-one mirror of a card_srs row. Immutable — a review produces a
 * brand new CardSrs via the scheduler rather than mutating this one.
 *
 * Rows are created by the trg_words_insert_srs / trg_phrases_insert_srs
 * DB triggers, never inserted directly by application code, so there's
 * deliberately no bare "new card" convenience constructor here.
 */
public record CardSrs(
        long id,
        Optional<Long> wordId,
        Optional<Long> phraseId,
        int repetitions,
        double easeFactor,
        double intervalDays,
        Instant dueAt,
        int lapses,
        CardState state,
        Optional<Instant> lastReviewedAt
) {
    public CardSrs {
        if (wordId.isPresent() == phraseId.isPresent()) {
            throw new IllegalArgumentException(
                    "CardSrs must reference exactly one of wordId or phraseId");
        }
    }
}