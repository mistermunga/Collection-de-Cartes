package xyz.ryansbeanfactory.collectiondecartes.srs;

import xyz.ryansbeanfactory.collectiondecartes.model.CardSrs;
import xyz.ryansbeanfactory.collectiondecartes.model.ReviewLogEntry;
import xyz.ryansbeanfactory.collectiondecartes.model.ReviewOutcome;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardState;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Grade;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Classic SM-2 (Wozniak, 1987), applied directly against a card's
 * current (repetitions, easeFactor, intervalDays, lapses).
 *
 * Pure and stateless. Every branch is a
 * plain function of its inputs, so this can be unit tested exhaustively
 * with nothing but assertions.
 */
public class Sm2Scheduler {

    private final SrsConfig config;

    public Sm2Scheduler() {
        this(SrsConfig.defaults());
    }

    public Sm2Scheduler(SrsConfig config) {
        this.config = config;
    }

    public ReviewOutcome review(CardSrs current, Grade grade, Instant now) {
        double newEf = updatedEaseFactor(current.easeFactor(), grade);

        int newRepetitions;
        int newLapses;
        double newInterval;

        if (grade.isLapse()) {
            newRepetitions = 0;
            newLapses = current.lapses() + 1;
            newInterval = config.firstIntervalDays();
        } else {
            newRepetitions = current.repetitions() + 1;
            newLapses = current.lapses();
            newInterval = switch (current.repetitions()) {
                case 0 -> config.firstIntervalDays();
                case 1 -> config.secondIntervalDays();
                default -> Math.round(current.intervalDays() * newEf * 10.0) / 10.0;
            };
        }

        CardState newState = CardState.derive(newRepetitions, newLapses);
        Instant newDueAt = now.plus(daysToDuration(newInterval));

        CardSrs updatedCard = new CardSrs(
                current.id(),
                current.wordId(),
                current.phraseId(),
                newRepetitions,
                newEf,
                newInterval,
                newDueAt,
                newLapses,
                newState,
                Optional.of(now)
        );

        ReviewLogEntry logEntry = new ReviewLogEntry(
                current.id(),
                now,
                grade.value(),
                current.intervalDays(),
                newInterval,
                current.easeFactor(),
                newEf
        );

        return new ReviewOutcome(updatedCard, logEntry);
    }

    private double updatedEaseFactor(double currentEf, Grade grade) {
        int q = grade.value();
        double delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02);
        return Math.max(config.minEaseFactor(), currentEf + delta);
    }

    private static Duration daysToDuration(double days) {
        return Duration.ofSeconds(Math.round(days * 86_400));
    }
}
