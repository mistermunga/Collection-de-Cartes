package xyz.ryansbeanfactory.collectiondecartes.database;

import java.sql.*;
import java.util.Optional;

/**
 * Generic key/value store backed by meta.db's settings table.
 */
public class SettingsRepository {

    private final Connection connection;

    public SettingsRepository(DatabaseManager dbm) {
        connection = dbm.getMetaConnection();
    }

    // ---------------------------------------------------------------
    // Raw string access
    // ---------------------------------------------------------------

    public Optional<String> get(String key) throws SQLException {
        String sql = "SELECT value FROM settings WHERE key = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getString("value"));
                }
            }
        }
        return Optional.empty();
    }

    /** Upsert — inserts the key if new, overwrites the value if it already exists. */
    public void set(String key, String value) throws SQLException {
        String sql = """
                INSERT INTO settings (key, value)
                VALUES (?, ?)
                ON CONFLICT(key) DO UPDATE SET value = excluded.value
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.executeUpdate();
        }
    }

    public boolean delete(String key) throws SQLException {
        String sql = "DELETE FROM settings WHERE key = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            return statement.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Typed convenience helpers
    // ---------------------------------------------------------------

    public double getDouble(String key, double defaultValue) throws SQLException {
        return get(key).map(Double::parseDouble).orElse(defaultValue);
    }

    public void setDouble(String key, double value) throws SQLException {
        set(key, Double.toString(value));
    }

    public int getInt(String key, int defaultValue) throws SQLException {
        return get(key).map(Integer::parseInt).orElse(defaultValue);
    }

    public void setInt(String key, int value) throws SQLException {
        set(key, Integer.toString(value));
    }
}