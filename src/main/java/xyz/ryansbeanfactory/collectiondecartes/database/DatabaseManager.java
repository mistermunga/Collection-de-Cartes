package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DatabaseManager {

    private static final String dbDirectory = "databases";
    List<String> languages;

    public DatabaseManager() throws IOException {

        languages = Arrays.stream(DeckLanguage.values())
                .map(Objects::toString)
                .toList();

        initializeDatabases();
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
}
