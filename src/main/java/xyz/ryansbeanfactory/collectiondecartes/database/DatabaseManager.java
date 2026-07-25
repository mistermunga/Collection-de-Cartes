package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static final String dbDirectory = "databases";
    private final List<String> languages;
    private final Map<String, Connection> connections = new HashMap<>();

    public DatabaseManager() throws IOException, SQLException {

        languages = Arrays.stream(DeckLanguage.values())
                .map(DeckLanguage::name)
                .toList();

        initializeDatabases();
        openConnections();
    }

    private void initializeDatabases() throws IOException {
        Path path = Paths.get(dbDirectory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        for (String language : languages) {
            Path dbPath = Paths.get(dbDirectory + "/" + language.toLowerCase() + ".db");
            if (!Files.exists(dbPath)) {
                Files.createFile(dbPath);
            }
        }
    }

    private void openConnections() throws SQLException {
        for (String language : languages) {
            String url = "jdbc:sqlite:" + dbDirectory + "/" + language.toLowerCase() + ".db";
            Connection connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            connections.put(language, connection);
            initializeSchema(connection);
        }
    }

    private void initializeSchema(Connection connection) throws SQLException {
        String createWords = """
                CREATE TABLE IF NOT EXISTS words (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    lemma           TEXT NOT NULL UNIQUE,
                    definition      TEXT NOT NULL,
                    part_of_speech  TEXT NOT NULL,
                    gender          TEXT NOT NULL,
                    mastery_level   INTEGER NOT NULL DEFAULT 0,
                    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
                );
                """;

        String createPhrases = """
                CREATE TABLE IF NOT EXISTS phrases (
                    id              INTEGER PRIMARY KEY AUTOINCREMENT,
                    lemma           TEXT NOT NULL UNIQUE,
                    definition      TEXT NOT NULL,
                    mastery_level   INTEGER NOT NULL DEFAULT 0,
                    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
                );
                """;

        try(Statement statement = connection.createStatement()) {
            statement.execute(createWords);
            statement.execute(createPhrases);
        }
    }

    public Connection getConnection(DeckLanguage language) {
        return connections.get(language.name());
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void closeAllConnections() {
        connections.forEach((language, connection) -> {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + language + "\n" + e.getMessage());
            }
        });
    }
}
