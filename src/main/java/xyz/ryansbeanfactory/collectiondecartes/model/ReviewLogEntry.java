package xyz.ryansbeanfactory.collectiondecartes.model;

import java.time.Instant;

/**
 * One-to-one mirror of a review_log row. id = 0 signals "not yet
 * inserted" — ReviewLogRepository.insert() hands back a copy with the
 * real generated id, same pattern as WordRepository/PhraseRepository.
 */
public record ReviewLogEntry(
        long id,
        long cardSrsId,
        Instant reviewedAt,
        int grade,
        double prevInterval,
        double newInterval,
        double prevEf,
        double newEf
) {
    public ReviewLogEntry(long cardSrsId, Instant reviewedAt, int grade,
                          double prevInterval, double newInterval,
                          double prevEf, double newEf) {
        this(0L, cardSrsId, reviewedAt, grade, prevInterval, newInterval, prevEf, newEf);
    }
}