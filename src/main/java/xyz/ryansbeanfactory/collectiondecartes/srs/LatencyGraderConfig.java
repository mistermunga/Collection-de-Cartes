package xyz.ryansbeanfactory.collectiondecartes.srs;

import xyz.ryansbeanfactory.collectiondecartes.model.refs.Grade;

/**
 * Tunable knobs for LatencyGrader. Same rationale as SrsConfig: kept
 * separate from the grading logic so behavior can be adjusted (or
 * exposed as a settings screen later) without touching the algorithm.
 */
public record LatencyGraderConfig(
        double seedMsPerWord,          // starting assumption before any calibration exists
        double emaAlpha,               // how fast calibration adapts to recent behavior (0-1)
        double misclickLeniency,       // wrong answers faster than expectedReadMs * this are misclicks
        double fastMultiplier,         // correct answers faster than expectedReadMs * this -> PERFECT
        double mediumMultiplier,       // correct answers faster than expectedReadMs * this -> CORRECT_HESITANT
        double outlierRejectionMultiplier, // observed ms/word above current avg * this is discarded, not learned from
        Grade wrongGrade               // the single grade applied to any genuine (non-misclick) miss
) {
    public static LatencyGraderConfig defaults() {
        return new LatencyGraderConfig(
                240.0,      // ~250 wpm reading speed
                0.2,
                0.55,
                0.8,
                1.4,
                4.0,
                Grade.INCORRECT_FAMILIAR
        );
    }
}