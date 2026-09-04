package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.PartOfSpeech;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordRepository {

    private final Connection connection;

    public WordRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Inserts a new word and returns a fresh Word record carrying the
     * generated id. The record passed in is never mutated.
     */
    public Word insert(Word word) throws SQLException {
        String sql = """
                INSERT INTO words (lemma, definition, part_of_speech, gender)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, word.lemma());
            statement.setString(2, word.definition());
            statement.setString(3, word.partOfSpeech().name());
            statement.setString(4, word.gender().name());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    return new Word(generatedId, word.lemma(), word.definition(),
                            word.partOfSpeech(), word.gender());
                }
            }
        }
        return word;
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    public Optional<Word> findById(long id) throws SQLException {
        String sql = "SELECT * FROM words WHERE id = ?";

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

    public Optional<Word> findByLemma(String lemma) throws SQLException {
        String sql = "SELECT * FROM words WHERE lemma = ?";

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

    public List<Word> findAll() throws SQLException {
        String sql = "SELECT * FROM words ORDER BY lemma";
        List<Word> words = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                words.add(mapRow(resultSet));
            }
        }
        return words;
    }

    public List<Word> findByPartOfSpeech(PartOfSpeech partOfSpeech) throws SQLException {
        String sql = "SELECT * FROM words WHERE part_of_speech = ? ORDER BY lemma";
        List<Word> words = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, partOfSpeech.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    words.add(mapRow(resultSet));
                }
            }
        }
        return words;
    }

    /**
     * Random words for use as multiple-choice distractors. excludeId is a
     * plain id, not wrapped in Optional — pass a value that can never
     * match a real row (e.g. -1) when no exclusion is needed, such as
     * when topping up distractors from the other card type.
     */
    public List<Word> findRandomExcluding(long excludeId, int limit) throws SQLException {
        String sql = "SELECT * FROM words WHERE id != ? ORDER BY RANDOM() LIMIT ?";
        List<Word> words = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, excludeId);
            statement.setInt(2, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    words.add(mapRow(resultSet));
                }
            }
        }
        return words;
    }

    public boolean existsByLemma(String lemma) throws SQLException {
        String sql = "SELECT 1 FROM words WHERE lemma = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lemma);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM words";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            return resultSet.next() ? resultSet.getLong(1) : 0L;
        }
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Persists every field of the given word, matched by its id.
     * Since Word is immutable, callers build the desired end-state
     * (typically via findById(...) then constructing a new Word with
     * the changed field(s) and the same id) and pass it in here.
     * Returns true if a row was actually updated (i.e. the id existed).
     */
    public boolean update(Word word) throws SQLException {
        String sql = """
                UPDATE words
                SET lemma = ?, definition = ?, part_of_speech = ?, gender = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, word.lemma());
            statement.setString(2, word.definition());
            statement.setString(3, word.partOfSpeech().name());
            statement.setString(4, word.gender().name());
            statement.setLong(5, word.id());

            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Deletes a word by id. The trigger-managed card_srs row is removed
     * automatically via ON DELETE CASCADE.
     */
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM words WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteByLemma(String lemma) throws SQLException {
        String sql = "DELETE FROM words WHERE lemma = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lemma);
            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------------

    private Word mapRow(ResultSet resultSet) throws SQLException {
        String genderColumn = resultSet.getString("gender");
        Gender gender = (genderColumn != null) ? Gender.valueOf(genderColumn) : Gender.NONE;

        return new Word(
                resultSet.getLong("id"),
                resultSet.getString("lemma"),
                resultSet.getString("definition"),
                PartOfSpeech.valueOf(resultSet.getString("part_of_speech")),
                gender
        );
    }
}