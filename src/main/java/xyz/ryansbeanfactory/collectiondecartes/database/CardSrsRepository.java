package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.CardSrs;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardState;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reads and updates card_srs rows. Deliberately has no insert(): rows
 * are created by trg_words_insert_srs / trg_phrases_insert_srs the
 * moment a word or phrase is inserted, so application code never
 * creates a card_srs row directly.
 */
public class CardSrsRepository {

    static final DateTimeFormatter DB_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    private final Connection connection;

    public CardSrsRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    public Optional<CardSrs> findById(long id) throws SQLException {
        String sql = "SELECT * FROM card_srs WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<CardSrs> findByWordId(long wordId) throws SQLException {
        String sql = "SELECT * FROM card_srs WHERE word_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, wordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<CardSrs> findByPhraseId(long phraseId) throws SQLException {
        String sql = "SELECT * FROM card_srs WHERE phrase_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, phraseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    /** Cards past due, oldest-overdue-first, for building a review queue. */
    public List<CardSrs> findDue(Instant now, int limit) throws SQLException {
        String sql = """
                SELECT * FROM card_srs
                WHERE state != 'new' AND due_at <= ?
                ORDER BY due_at ASC
                LIMIT ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_TIMESTAMP.format(now));
            statement.setInt(2, limit);

            return mapAll(statement);
        }
    }

    /** Never-studied cards, insertion order, for filling out a new-card quota. */
    public List<CardSrs> findNew(int limit) throws SQLException {
        String sql = """
                SELECT * FROM card_srs
                WHERE state = 'new'
                ORDER BY id ASC
                LIMIT ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            return mapAll(statement);
        }
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Persists every mutable field of the given card, matched by id.
     * Callers get their updated CardSrs from Sm2Scheduler.review(...)
     * and pass it straight through here.
     */
    public boolean update(CardSrs card) throws SQLException {
        String sql = """
                UPDATE card_srs
                SET repetitions = ?, ease_factor = ?, interval_days = ?,
                    due_at = ?, lapses = ?, state = ?, last_reviewed_at = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, card.repetitions());
            statement.setDouble(2, card.easeFactor());
            statement.setDouble(3, card.intervalDays());
            statement.setString(4, DB_TIMESTAMP.format(card.dueAt()));
            statement.setInt(5, card.lapses());
            statement.setString(6, card.state().dbValue());
            statement.setString(7, card.lastReviewedAt().map(DB_TIMESTAMP::format).orElse(null));
            statement.setLong(8, card.id());

            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------------

    private List<CardSrs> mapAll(PreparedStatement statement) throws SQLException {
        List<CardSrs> cards = new ArrayList<>();

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                cards.add(mapRow(resultSet));
            }
        }
        return cards;
    }

    private CardSrs mapRow(ResultSet resultSet) throws SQLException {
        long wordIdColumn = resultSet.getLong("word_id");
        Optional<Long> wordId = resultSet.wasNull() ? Optional.empty() : Optional.of(wordIdColumn);

        long phraseIdColumn = resultSet.getLong("phrase_id");
        Optional<Long> phraseId = resultSet.wasNull() ? Optional.empty() : Optional.of(phraseIdColumn);

        String lastReviewedColumn = resultSet.getString("last_reviewed_at");
        Optional<Instant> lastReviewedAt = (lastReviewedColumn != null)
                ? Optional.of(parseInstant(lastReviewedColumn))
                : Optional.empty();

        return new CardSrs(
                resultSet.getLong("id"),
                wordId,
                phraseId,
                resultSet.getInt("repetitions"),
                resultSet.getDouble("ease_factor"),
                resultSet.getDouble("interval_days"),
                parseInstant(resultSet.getString("due_at")),
                resultSet.getInt("lapses"),
                CardState.fromDbValue(resultSet.getString("state")),
                lastReviewedAt
        );
    }

    static Instant parseInstant(String dbTimestamp) {
        return LocalDateTime.parse(dbTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toInstant(ZoneOffset.UTC);
    }
}