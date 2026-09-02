package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.ReviewLogEntry;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static xyz.ryansbeanfactory.collectiondecartes.database.CardSrsRepository.DB_TIMESTAMP;
import static xyz.ryansbeanfactory.collectiondecartes.database.CardSrsRepository.parseInstant;

/**
 * Append-only review history, plus the two counting queries that gate
 * daily new-card and review limits without needing any extra schema:
 * a card counts as "introduced today" if its first-ever log row was
 * today, regardless of whether that first attempt was a lapse.
 */
public class ReviewLogRepository {

    private final Connection connection;

    public ReviewLogRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    public ReviewLogEntry insert(ReviewLogEntry entry) throws SQLException {
        String sql = """
                INSERT INTO review_log
                    (card_srs_id, reviewed_at, grade, prev_interval, new_interval, prev_ef, new_ef)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, entry.cardSrsId());
            statement.setString(2, DB_TIMESTAMP.format(entry.reviewedAt()));
            statement.setInt(3, entry.grade());
            statement.setDouble(4, entry.prevInterval());
            statement.setDouble(5, entry.newInterval());
            statement.setDouble(6, entry.prevEf());
            statement.setDouble(7, entry.newEf());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    return new ReviewLogEntry(generatedId, entry.cardSrsId(), entry.reviewedAt(),
                            entry.grade(), entry.prevInterval(), entry.newInterval(),
                            entry.prevEf(), entry.newEf());
                }
            }
        }
        return entry;
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    public List<ReviewLogEntry> findByCardSrsId(long cardSrsId) throws SQLException {
        String sql = "SELECT * FROM review_log WHERE card_srs_id = ? ORDER BY reviewed_at ASC";
        List<ReviewLogEntry> entries = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardSrsId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(mapRow(resultSet));
                }
            }
        }
        return entries;
    }

    /** How many reviews (of any kind) have happened today, for daily_review_limit. */
    public long countReviewsToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM review_log WHERE date(reviewed_at) = date('now')";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    /**
     * How many cards were seen for the very first time today, for
     * daily_new_cards_limit. A card counts once, on the day of its
     * earliest review_log row, whatever grade that review got.
     */
    public long countNewCardsIntroducedToday() throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM (
                    SELECT card_srs_id
                    FROM review_log
                    GROUP BY card_srs_id
                    HAVING date(MIN(reviewed_at)) = date('now')
                )
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    // ---------------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------------

    private ReviewLogEntry mapRow(ResultSet resultSet) throws SQLException {
        Instant reviewedAt = parseInstant(resultSet.getString("reviewed_at"));

        return new ReviewLogEntry(
                resultSet.getLong("id"),
                resultSet.getLong("card_srs_id"),
                reviewedAt,
                resultSet.getInt("grade"),
                resultSet.getDouble("prev_interval"),
                resultSet.getDouble("new_interval"),
                resultSet.getDouble("prev_ef"),
                resultSet.getDouble("new_ef")
        );
    }
}