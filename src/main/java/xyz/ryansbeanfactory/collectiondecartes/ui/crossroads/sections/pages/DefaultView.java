package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;

import java.sql.SQLException;

public class DefaultView extends VBox {

    public DefaultView() {
        WordRepository wordRepository = new WordRepository(
                CarteApplication.getInstance().getDatabaseManager(),
                AppSession.getInstance().getDeckLanguage()
        );
        PhraseRepository phraseRepository = new PhraseRepository(
                CarteApplication.getInstance().getDatabaseManager(),
                AppSession.getInstance().getDeckLanguage()
        );

        Label words = new Label("Words: ");
        Label phrases = new Label("Phrases: ");
        long wordCount = 0;
        long phraseCount = 0;

        try {
            wordCount = wordRepository.count();
            phraseCount = phraseRepository.count();
        } catch (SQLException _) {}

        GridPane grid = new GridPane();
        grid.add(words, 0, 0);
        grid.add(phrases, 0, 1);
        grid.add(new Label(String.valueOf(wordCount)), 1, 1);
        grid.add(new Label(String.valueOf(phraseCount)), 1, 2);

        this.getChildren().add(grid);

    }
}
