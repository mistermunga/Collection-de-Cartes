package xyz.ryansbeanfactory.collectiondecartes.database;

import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static String dbDirectory = "databases";
    List<String> languages;

    public DatabaseManager() throws IOException {

        Class<?> clazz = DeckLanguage.class;
        Object[] constants = clazz.getEnumConstants();
        languages = Arrays.stream(constants)
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
            if (!Files.exists(Paths.get(dbDirectory + "/" + language.toLowerCase() + ".db"))) {
                Files.createFile(Paths.get(dbDirectory + "/" + language.toLowerCase() + ".db"));
            }
        }
    }
}
