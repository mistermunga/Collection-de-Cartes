package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

        Path metaDir = Paths.get(dbDirectory + "/meta");
        if (!Files.exists(metaDir)) {
            Files.createDirectories(metaDir);
        }
        Path metaDbPath = metaDir.resolve("meta.db");
        if (!Files.exists(metaDbPath)) {
            Files.createFile(metaDbPath);
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

        String url = "jdbc:sqlite:" + dbDirectory + "/meta/meta.db";
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        connections.put("meta", connection);
        initializeMetaDB(connection);
        seedDeckConfig(connection);
    }

    private void initializeSchema(Connection connection) throws SQLException {
        String pragmaFk = "PRAGMA foreign_keys = ON;";

        String createWords = """
            CREATE TABLE IF NOT EXISTS words (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                lemma           TEXT NOT NULL UNIQUE,
                definition      TEXT NOT NULL,
                part_of_speech  TEXT NOT NULL,
                gender          TEXT,
                created_at      TEXT NOT NULL DEFAULT (datetime('now'))
            );
            """;

        String createPhrases = """
            CREATE TABLE IF NOT EXISTS phrases (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                lemma           TEXT NOT NULL UNIQUE,
                definition      TEXT NOT NULL,
                created_at      TEXT NOT NULL DEFAULT (datetime('now'))
            );
            """;

        String createCardSrs = """
            CREATE TABLE IF NOT EXISTS card_srs (
                id               INTEGER PRIMARY KEY AUTOINCREMENT,
                word_id          INTEGER REFERENCES words(id)   ON DELETE CASCADE,
                phrase_id        INTEGER REFERENCES phrases(id) ON DELETE CASCADE,

                repetitions      INTEGER NOT NULL DEFAULT 0,
                ease_factor      REAL    NOT NULL DEFAULT 2.5,
                interval_days    REAL    NOT NULL DEFAULT 0,
                due_at           TEXT    NOT NULL DEFAULT (datetime('now')),
                lapses           INTEGER NOT NULL DEFAULT 0,
                state            TEXT    NOT NULL DEFAULT 'new'
                    CHECK (state IN ('new','learning','review','relearning')),
                last_reviewed_at TEXT,

                CHECK (
                    (word_id IS NOT NULL AND phrase_id IS NULL) OR
                    (word_id IS NULL AND phrase_id IS NOT NULL)
                ),
                UNIQUE(word_id),
                UNIQUE(phrase_id)
            );
            """;

        String createCardSrsDueIndex = "CREATE INDEX IF NOT EXISTS idx_card_srs_due ON card_srs(due_at);";

        String createReviewLog = """
            CREATE TABLE IF NOT EXISTS review_log (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                card_srs_id   INTEGER NOT NULL REFERENCES card_srs(id) ON DELETE CASCADE,
                reviewed_at   TEXT NOT NULL DEFAULT (datetime('now')),
                grade         INTEGER NOT NULL,
                prev_interval REAL,
                new_interval  REAL,
                prev_ef       REAL,
                new_ef        REAL
            );
            """;

        String createWordsTrigger = """
            CREATE TRIGGER IF NOT EXISTS trg_words_insert_srs
            AFTER INSERT ON words
            BEGIN
                INSERT INTO card_srs (word_id) VALUES (NEW.id);
            END;
            """;

        String createPhrasesTrigger = """
            CREATE TRIGGER IF NOT EXISTS trg_phrases_insert_srs
            AFTER INSERT ON phrases
            BEGIN
                INSERT INTO card_srs (phrase_id) VALUES (NEW.id);
            END;
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(pragmaFk);
            statement.execute(createWords);
            statement.execute(createPhrases);
            statement.execute(createCardSrs);
            statement.execute(createCardSrsDueIndex);
            statement.execute(createReviewLog);
            statement.execute(createWordsTrigger);
            statement.execute(createPhrasesTrigger);
        }
    }

    private void initializeMetaDB(Connection connection) throws SQLException {
        String createDeckConfig = """
                CREATE TABLE IF NOT EXISTS deck_config (
                    language              TEXT PRIMARY KEY,
                    daily_new_cards_limit INTEGER NOT NULL DEFAULT 20,
                    daily_review_limit    INTEGER NOT NULL DEFAULT 200
                );
                """;

        String createStudySessions = """
                CREATE TABLE IF NOT EXISTS study_sessions (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    language       TEXT NOT NULL,
                    started_at     TEXT NOT NULL DEFAULT (datetime('now')),
                    ended_at       TEXT,
                    cards_reviewed INTEGER NOT NULL DEFAULT 0,
                    correct        INTEGER NOT NULL DEFAULT 0
                );
                """;

        String createSettings = """
                CREATE TABLE IF NOT EXISTS settings (
                    key   TEXT PRIMARY KEY,
                    value TEXT NOT NULL
                );
                """;

        String createActivityLog = """
                CREATE TABLE IF NOT EXISTS activity_log (
                    activity_date TEXT PRIMARY KEY
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createDeckConfig);
            statement.execute(createStudySessions);
            statement.execute(createSettings);
            statement.execute(createActivityLog);
        }
    }

    private void seedDeckConfig(Connection connection) throws SQLException {
        String insertIfMissing = """
                INSERT INTO deck_config (language)
                VALUES (?)
                ON CONFLICT(language) DO NOTHING;
                """;

        try (PreparedStatement statement = connection.prepareStatement(insertIfMissing)) {
            for (String language : languages) {
                statement.setString(1, language);
                statement.executeUpdate();
            }
        }
    }

    public Connection getConnection(DeckLanguage language) {
        return connections.get(language.name());
    }

    public Connection getMetaConnection() {
        return connections.get("meta");
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
