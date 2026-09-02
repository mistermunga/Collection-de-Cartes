package xyz.ryansbeanfactory.collectiondecartes.model;

/**
 * The result of scheduling one review: the card's new state, plus the
 * log entry that should be appended alongside it. Bundled together on
 * purpose — persisting one without the other would leave the two
 * tables out of sync.
 */
public record ReviewOutcome(CardSrs updatedCard, ReviewLogEntry logEntry) {
}