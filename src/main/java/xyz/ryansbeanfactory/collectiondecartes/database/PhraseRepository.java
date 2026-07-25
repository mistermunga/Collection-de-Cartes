package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.*;

public class PhraseRepository {

    private final Connection connection;

    public PhraseRepository(DatabaseManager dbm, DeckLanguage language) {
        connection = dbm.getConnection(language);
    }

    public Phrase insert(Phrase phrase) throws SQLException {
        String sql = """
                INSERT INTO phrases (lemma, definition)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phrase.getLemma());
            statement.setString(2, phrase.getDefinition());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    phrase.setId(resultSet.getLong(1));
                }
            }
        }
        return phrase;
    }
}
