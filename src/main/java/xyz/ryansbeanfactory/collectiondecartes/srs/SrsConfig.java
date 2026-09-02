package xyz.ryansbeanfactory.collectiondecartes.srs;

/**
 * Tunable knobs for the SM-2 scheduler. Kept separate from the
 * algorithm itself so behavior can change without touching scheduling
 * logic — and so tests can exercise edge cases (e.g. a near-floor
 * ease factor) without hardcoded magic numbers scattered around.
 */
public record SrsConfig(
        double minEaseFactor,
        double firstIntervalDays,
        double secondIntervalDays
) {
    public static SrsConfig defaults() {
        return new SrsConfig(1.3, 1.0, 6.0);
    }
}