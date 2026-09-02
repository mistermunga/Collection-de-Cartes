package xyz.ryansbeanfactory.collectiondecartes.model.refs;

/**
 * Mirrors the CHECK constraint on card_srs.state ('new','learning','review','relearning').
 * Never set directly — always derived from (repetitions, lapses) by the scheduler.
 */
public enum CardState {
    NEW, LEARNING, REVIEW, RELEARNING;

    public String dbValue() {
        return name().toLowerCase();
    }

    public static CardState fromDbValue(String value) {
        return CardState.valueOf(value.toUpperCase());
    }

    /**
     * Derives the lifecycle state from a card's repetition/lapse counters.
     * See design notes: NEW = untouched, LEARNING = first successful pass,
     * REVIEW = graduated, RELEARNING = repetitions reset by a lapse.
     */
    public static CardState derive(int repetitions, int lapses) {
        if (repetitions == 0 && lapses == 0) return NEW;
        if (repetitions == 0) return RELEARNING;
        if (repetitions == 1) return LEARNING;
        return REVIEW;
    }
}