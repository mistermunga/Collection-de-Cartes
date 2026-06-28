package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;

public class WordRepository {

    private final Connection connection;

    public WordRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    public Word insert(Word word) throws SQLException {
        String sql = """
                INSERT INTO words (lemma, definition, part_of_speech, gender)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, word.getLemma());
            statement.setString(2, word.getDefinition());
            statement.setString(3, word.getPartOfSpeech().name());
            statement.setString(4, word.getGender().name());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    word.setId(generatedKeys.getLong(1));
                }
            }
        }
        return word;
    }
}
