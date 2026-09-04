package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhraseRepository {

    private final Connection connection;

    public PhraseRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Inserts a new phrase and returns a fresh Phrase record carrying the
     * generated id. The record passed in is never mutated.
     */
    public Phrase insert(Phrase phrase) throws SQLException {
        String sql = """
                INSERT INTO phrases (lemma, definition)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phrase.lemma());
            statement.setString(2, phrase.definition());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    long generatedId = resultSet.getLong(1);
                    return new Phrase(generatedId, phrase.lemma(), phrase.definition());
                }
            }
        }
        return phrase;
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    public Optional<Phrase> findById(long id) throws SQLException {
        String sql = "SELECT * FROM phrases WHERE id = ?";

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

    public Optional<Phrase> findByLemma(String lemma) throws SQLException {
        String sql = "SELECT * FROM phrases WHERE lemma = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lemma);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public List<Phrase> findAll() throws SQLException {
        String sql = "SELECT * FROM phrases ORDER BY lemma";
        List<Phrase> phrases = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                phrases.add(mapRow(resultSet));
            }
        }
        return phrases;
    }

    public boolean existsByLemma(String lemma) throws SQLException {
        String sql = "SELECT 1 FROM phrases WHERE lemma = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lemma);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Random phrases for use as multiple-choice distractors. excludeId is a
     * plain id, not wrapped in Optional — pass a value that can never
     * match a real row (e.g. -1) when no exclusion is needed, such as
     * when topping up distractors from the other card type.
     */
    public List<Phrase> findRandomExcluding(long excludeId, int limit) throws SQLException {
        String sql = "SELECT * FROM phrases WHERE id != ? ORDER BY RANDOM() LIMIT ?";
        List<Phrase> phrases = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, excludeId);
            statement.setInt(2, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    phrases.add(mapRow(resultSet));
                }
            }
        }
        return phrases;
    }

    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM phrases";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Persists every field of the given phrase, matched by its id.
     * Since Phrase is immutable, callers build the desired end-state
     * (typically via findById(...) then constructing a new Phrase with
     * the changed field(s) and the same id) and pass it in here.
     * Returns true if a row was actually updated (i.e. the id existed).
     */
    public boolean update(Phrase phrase) throws SQLException {
        String sql = """
                UPDATE phrases
                SET lemma = ?, definition = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phrase.lemma());
            statement.setString(2, phrase.definition());
            statement.setLong(3, phrase.id());

            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Deletes a phrase by id. The trigger-managed card_srs row is removed
     * automatically via ON DELETE CASCADE.
     */
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM phrases WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteByLemma(String lemma) throws SQLException {
        String sql = "DELETE FROM phrases WHERE lemma = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lemma);
            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------------

    private Phrase mapRow(ResultSet resultSet) throws SQLException {
        return new Phrase(
                resultSet.getLong("id"),
                resultSet.getString("lemma"),
                resultSet.getString("definition")
        );
    }
}