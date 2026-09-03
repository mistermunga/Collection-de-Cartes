package xyz.ryansbeanfactory.collectiondecartes.srs;

/** Mirrors a meta.db deck_config row — the daily caps a StudySession queue respects. */
public record DeckConfig(String language, int dailyNewCardsLimit, int dailyReviewLimit) {
}