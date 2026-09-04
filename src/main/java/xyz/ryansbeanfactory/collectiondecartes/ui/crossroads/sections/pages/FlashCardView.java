package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.CardSrs;
import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.model.ReviewOutcome;
import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardType;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;
import xyz.ryansbeanfactory.collectiondecartes.srs.StudySession;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.Card;
import xyz.ryansbeanfactory.collectiondecartes.ui.components.Option;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The flashcard game screen. Shows one Card at a time with four
 * Option answers, times how long the user takes to answer, and hands
 * that off to StudySession — which decides via LatencyGrader whether
 * it was a genuine answer or a misclick.
 */
public class FlashCardView extends VBox {

    private static final long MINIMUM_CARDS_REQUIRED = 50;
    private static final int DISTRACTOR_COUNT = 3;

    private final WordRepository wordRepository;
    private final PhraseRepository phraseRepository;
    private final StudySession studySession;

    private final Card card = new Card(false);
    private final VBox optionsBox = new VBox(12);

    private long currentTargetId;
    private CardType currentCardType;
    private Instant questionStartedAt;

    public FlashCardView() {
        DatabaseManager dbm = CarteApplication.getInstance().getDatabaseManager();
        DeckLanguage language = AppSession.getInstance().getDeckLanguage();

        this.wordRepository = new WordRepository(dbm, language);
        this.phraseRepository = new PhraseRepository(dbm, language);
        this.studySession = new StudySession(dbm, language);

        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new Insets(32));

        start();
    }

    private void start() {
        try {
            long totalCards = wordRepository.count() + phraseRepository.count();

            if (totalCards < MINIMUM_CARDS_REQUIRED) {
                showGate(totalCards);
                return;
            }

            studySession.buildQueue();
            showNextCard();
        } catch (SQLException e) {
            showError("Couldn't load your deck.");
        }
    }

    // ---------------------------------------------------------------
    // Screen states
    // ---------------------------------------------------------------

    private void showGate(long totalCards) {
        Label heading = new Label("Add a few more words first");
        heading.getStyleClass().add("gate-heading");

        Label detail = new Label(
                "You have " + totalCards + " of the " + MINIMUM_CARDS_REQUIRED
                        + " words and phrases needed before flashcards can pick good distractors."
        );
        detail.setWrapText(true);

        getChildren().setAll(heading, detail);
    }

    private void showNextCard() {
        try {
            if (!studySession.hasNext()) {
                showSessionComplete();
                return;
            }

            CardSrs next = studySession.peekNext().orElseThrow();
            List<Option> options = prepareQuestion(next);
            renderQuestion(options);
        } catch (SQLException e) {
            showError("Couldn't load the next card.");
        }
    }

    private void showSessionComplete() {
        getChildren().setAll(new Label("Nice work — you're done for today."));
    }

    // ---------------------------------------------------------------
    // Question building
    // ---------------------------------------------------------------

    /**
     * Fetches the current card's word/phrase content, updates the Card
     * prompt as a side effect, and builds four shuffled Options
     * (the correct definition plus DISTRACTOR_COUNT wrong ones).
     */
    private List<Option> prepareQuestion(CardSrs cardSrs) throws SQLException {
        String correctDefinition;

        if (cardSrs.wordId().isPresent()) {
            Word word = wordRepository.findById(cardSrs.wordId().get()).orElseThrow();
            currentTargetId = word.id();
            currentCardType = CardType.WORD;
            correctDefinition = word.definition();
            updatePrompt(word);
        } else {
            Phrase phrase = phraseRepository.findById(cardSrs.phraseId().get()).orElseThrow();
            currentTargetId = phrase.id();
            currentCardType = CardType.PHRASE;
            correctDefinition = phrase.definition();
            updatePrompt(phrase);
        }

        List<Option> options = new ArrayList<>();
        options.add(new Option(currentTargetId, currentCardType, correctDefinition));

        List<Option> sameType = (currentCardType == CardType.WORD)
                ? wordRepository.findRandomExcluding(currentTargetId, DISTRACTOR_COUNT).stream()
                .map(w -> new Option(w.id(), CardType.WORD, w.definition()))
                .toList()
                : phraseRepository.findRandomExcluding(currentTargetId, DISTRACTOR_COUNT).stream()
                .map(p -> new Option(p.id(), CardType.PHRASE, p.definition()))
                .toList();
        options.addAll(sameType);

        // A lopsided deck (mostly words, few phrases, or vice versa) might not
        // have enough same-type distractors — top up from the other pool.
        int stillNeeded = DISTRACTOR_COUNT - sameType.size();
        if (stillNeeded > 0) {
            List<Option> otherType = (currentCardType == CardType.WORD)
                    ? phraseRepository.findRandomExcluding(-1, stillNeeded).stream()
                    .map(p -> new Option(p.id(), CardType.PHRASE, p.definition()))
                    .toList()
                    : wordRepository.findRandomExcluding(-1, stillNeeded).stream()
                    .map(w -> new Option(w.id(), CardType.WORD, w.definition()))
                    .toList();
            options.addAll(otherType);
        }

        Collections.shuffle(options);
        return options;
    }

    private void updatePrompt(Word word) {
        card.setLemma(word.lemma());
        card.setMeta(word.gender() == Gender.NONE
                ? word.partOfSpeech().name()
                : word.partOfSpeech().name() + " · " + word.gender().name());
    }

    private void updatePrompt(Phrase phrase) {
        card.setLemma(phrase.lemma());
        card.setMeta("PHRASE");
    }

    // ---------------------------------------------------------------
    // Rendering + answering
    // ---------------------------------------------------------------

    private void renderQuestion(List<Option> options) {
        optionsBox.getChildren().clear();
        for (Option option : options) {
            option.setOnAction(e -> handleAnswer(option, options));
            optionsBox.getChildren().add(option);
        }

        getChildren().setAll(card, optionsBox);

        // Start the clock as close to "on screen" as possible — this is
        // what LatencyGrader treats as the user's response time.
        questionStartedAt = Instant.now();
    }

    private void handleAnswer(Option selected, List<Option> allOptions) {
        allOptions.forEach(option -> option.setDisable(true));

        Duration elapsed = Duration.between(questionStartedAt, Instant.now());
        boolean correct = selected.matches(currentTargetId, currentCardType);
        List<String> answerTexts = allOptions.stream().map(Option::getText).toList();

        try {
            Optional<ReviewOutcome> outcome = studySession.submitReview(correct, answerTexts, elapsed);
            // outcome.isEmpty() means LatencyGrader called this a misclick —
            // the card was recycled unscored, nothing further to do here.
        } catch (SQLException e) {
            showError("Couldn't save that review.");
            return;
        }

        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(400));
        pause.setOnFinished(e -> showNextCard());
        pause.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}