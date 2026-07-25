package xyz.ryansbeanfactory.collectiondecartes.util;

import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StreakManager implements AutoCloseable {

    private final Connection connection;

    public StreakManager() {
        DatabaseManager dbm = CarteApplication.getInstance().getDatabaseManager();
        connection = dbm.getMetaConnection();
    }

    public boolean incrementStreak() {
        String sql = """
                INSERT OR IGNORE INTO activity_log(activity_date)
                VALUES (date('now'));
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public int getCurrentStreak() throws SQLException {
        String query = """
                SELECT activity_date
                FROM activity_log
                ORDER BY activity_date DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            LocalDate expected = LocalDate.now();
            int streak = 0;

            while (results.next()) {
                LocalDate date = LocalDate.parse(results.getString("activity_date"));

                if (date.equals(expected)) {
                    streak++;
                    expected = expected.minusDays(1);
                } else if (date.isAfter(expected)) {
                    continue;
                } else {
                    break;
                }
            }

            return streak;
        }
    }

    @Override
    public void close() throws Exception {

    }
}
