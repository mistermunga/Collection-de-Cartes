package xyz.ryansbeanfactory.collectiondecartes.model.refs;

/**
 * Classic SM-2 response grade. The numeric value is exactly what gets
 * stored in review_log.grade — no translation needed at the DB boundary.
 */
public enum Grade {
    /** Complete blackout, no recollection at all. */
    BLACKOUT(0),
    /** Incorrect, but the correct answer felt familiar once seen. */
    INCORRECT_FAMILIAR(1),
    /** Incorrect, but the correct answer felt easy once seen. */
    INCORRECT_EASY(2),
    /** Correct, but only after significant difficulty. */
    CORRECT_DIFFICULT(3),
    /** Correct, after some hesitation. */
    CORRECT_HESITANT(4),
    /** Correct, recalled instantly and perfectly. */
    PERFECT(5);

    private final int value;

    Grade(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean isLapse() {
        return value < 3;
    }

    public static Grade fromValue(int value) {
        for (Grade grade : values()) {
            if (grade.value == value) return grade;
        }
        throw new IllegalArgumentException("No Grade with value " + value);
    }
}