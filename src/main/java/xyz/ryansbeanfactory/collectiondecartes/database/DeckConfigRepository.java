package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;
import xyz.ryansbeanfactory.collectiondecartes.srs.DeckConfig;

import java.sql.*;

public class DeckConfigRepository {

    private final Connection connection;

    public DeckConfigRepository(DatabaseManager dbm) {
        connection = dbm.getMetaConnection();
    }

    public DeckConfig findByLanguage(DeckLanguage language) throws SQLException {
        String sql = "SELECT * FROM deck_config WHERE language = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, language.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new DeckConfig(
                            resultSet.getString("language"),
                            resultSet.getInt("daily_new_cards_limit"),
                            resultSet.getInt("daily_review_limit")
                    );
                }
            }
        }

        // seedDeckConfig() runs for every DeckLanguage on startup, so this
        // is a bug (missing seed row) rather than a normal empty result.
        throw new SQLException("No deck_config row for language " + language.name());
    }
}