package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.Card;

import java.sql.SQLException;
import java.util.List;

/**
 * Every word and phrase in the current deck, laid out as a grid of
 * flippable Cards. No search/filter/sort yet — this is deliberately
 * just "load everything, show it" until those controls get designed.
 */
public class DictionaryView extends ScrollPane {

    private static final double CARD_WIDTH = 180;
    private static final double CARD_HEIGHT = 120;

    private final WordRepository wordRepository;
    private final PhraseRepository phraseRepository;
    private final FlowPane cardGrid = new FlowPane();

    public DictionaryView() {
        DatabaseManager dbm = CarteApplication.getInstance().getDatabaseManager();
        DeckLanguage language = AppSession.getInstance().getDeckLanguage();

        this.wordRepository = new WordRepository(dbm, language);
        this.phraseRepository = new PhraseRepository(dbm, language);

        cardGrid.setHgap(16);
        cardGrid.setVgap(16);
        cardGrid.setPadding(new Insets(24));
        cardGrid.getStyleClass().add("dictionary-grid");

        setContent(cardGrid);
        setFitToWidth(true);
        getStyleClass().add("dictionary-view");

        loadCards();
    }

    /** Re-reads the deck from the DB and rebuilds the grid. */
    public void refresh() {
        loadCards();
    }

    private void loadCards() {
        try {
            List<Word> words = wordRepository.findAll();
            List<Phrase> phrases = phraseRepository.findAll();

            cardGrid.getChildren().clear();
            words.forEach(word -> cardGrid.getChildren().add(buildCard(word)));
            phrases.forEach(phrase -> cardGrid.getChildren().add(buildCard(phrase)));
        } catch (SQLException e) {
            showError("Couldn't load the dictionary.");
        }
    }

    private Card buildCard(Word word) {
        Card card = new Card(true);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setLemma(word.lemma());
        card.setMeta(word.gender() == Gender.NONE
                ? word.partOfSpeech().name()
                : word.partOfSpeech().name() + " · " + word.gender().name());
        card.setDefinition(word.definition());
        return card;
    }

    private Card buildCard(Phrase phrase) {
        Card card = new Card(true);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setLemma(phrase.lemma());
        card.setMeta("PHRASE");
        card.setDefinition(phrase.definition());
        return card;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}