package xyz.ryansbeanfactory.collectiondecartes.srs;

import xyz.ryansbeanfactory.collectiondecartes.database.SettingsRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardType;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Grade;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Converts a quiz answer's correctness and response time into a Grade,
 * without asking the user to self-report difficulty.
 *
 * Calibration is self-adjusting: each card type (word/phrase) has a
 * running "ms per word" average in SettingsRepository, seeded with a
 * literature-typical reading speed and nudged by an exponential moving
 * average on every genuine (non-misclick) answer. Expected reading
 * time for a given question is calibratedMsPerWord * wordCount of the
 * shortest answer option shown.
 *
 * Empty result means "misclick" — the caller should recycle the card
 * back into the queue unscored, without touching Sm2Scheduler or
 * either repository.
 */
public class LatencyGrader {

    private static final String SETTINGS_KEY_PREFIX = "srs.avg_ms_per_word.";

    private final SettingsRepository settingsRepository;
    private final LatencyGraderConfig config;

    public LatencyGrader(SettingsRepository settingsRepository) {
        this(settingsRepository, LatencyGraderConfig.defaults());
    }

    public LatencyGrader(SettingsRepository settingsRepository, LatencyGraderConfig config) {
        this.settingsRepository = settingsRepository;
        this.config = config;
    }

    public Optional<Grade> grade(boolean correct, List<String> answerOptions,
                                 Duration elapsed, CardType cardType) throws SQLException {

        int wordCount = shortestWordCount(answerOptions);
        double calibratedMsPerWord = getCalibratedAverage(cardType);
        double expectedReadMs = wordCount * calibratedMsPerWord;
        long observedMs = elapsed.toMillis();

        if (!correct) {
            double misclickThreshold = expectedReadMs * config.misclickLeniency();
            if (observedMs < misclickThreshold) {
                return Optional.empty(); // too fast to be a real attempt — recycle, don't score, don't calibrate
            }

            updateCalibration(cardType, wordCount, observedMs);
            return Optional.of(config.wrongGrade());
        }

        updateCalibration(cardType, wordCount, observedMs);

        if (observedMs < expectedReadMs * config.fastMultiplier()) {
            return Optional.of(Grade.PERFECT);
        } else if (observedMs < expectedReadMs * config.mediumMultiplier()) {
            return Optional.of(Grade.CORRECT_HESITANT);
        } else {
            return Optional.of(Grade.CORRECT_DIFFICULT);
        }
    }

    // ---------------------------------------------------------------
    // Calibration
    // ---------------------------------------------------------------

    private double getCalibratedAverage(CardType cardType) throws SQLException {
        return settingsRepository.getDouble(settingsKey(cardType), config.seedMsPerWord());
    }

    private void updateCalibration(CardType cardType, int wordCount, long observedMs) throws SQLException {
        if (wordCount <= 0) return;

        double observedMsPerWord = (double) observedMs / wordCount;
        double current = getCalibratedAverage(cardType);

        // A wildly slow reading time is more likely a distraction than a
        // signal — don't let one outlier drag the running average around.
        if (observedMsPerWord > current * config.outlierRejectionMultiplier()) {
            return;
        }

        double updated = current + config.emaAlpha() * (observedMsPerWord - current);
        settingsRepository.setDouble(settingsKey(cardType), updated);
    }

    private static String settingsKey(CardType cardType) {
        return SETTINGS_KEY_PREFIX + cardType.name().toLowerCase();
    }

    // ---------------------------------------------------------------
    // Question shape
    // ---------------------------------------------------------------

    private static int shortestWordCount(List<String> options) {
        return options.stream()
                .map(String::trim)
                .filter(option -> !option.isEmpty())
                .mapToInt(option -> option.split("\\s+").length)
                .min()
                .orElse(1);
    }
}