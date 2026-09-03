package xyz.ryansbeanfactory.collectiondecartes.srs;

import xyz.ryansbeanfactory.collectiondecartes.database.CardSrsRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.DatabaseManager;
import xyz.ryansbeanfactory.collectiondecartes.database.DeckConfigRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.ReviewLogRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.CardSrs;
import xyz.ryansbeanfactory.collectiondecartes.model.ReviewOutcome;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Grade;
import xyz.ryansbeanfactory.collectiondecartes.session.DeckLanguage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * The only thing the UI/flashcard game should talk to. Builds a
 * queue for the day (due reviews first, then new cards, both capped
 * by what's left of today's deck_config limits) and grades cards one
 * at a time via submitReview.
 */
public class StudySession {

    private final Connection connection;
    private final CardSrsRepository cardSrsRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final DeckConfigRepository deckConfigRepository;
    private final Sm2Scheduler scheduler;
    private final DeckLanguage language;

    private Deque<CardSrs> queue;

    public StudySession(DatabaseManager dbm, DeckLanguage language) {
        this(dbm, language, new Sm2Scheduler());
    }

    public StudySession(DatabaseManager dbm, DeckLanguage language, Sm2Scheduler scheduler) {
        this.language = language;
        this.connection = dbm.getConnection(language);
        this.cardSrsRepository = new CardSrsRepository(dbm, language);
        this.reviewLogRepository = new ReviewLogRepository(dbm, language);
        this.deckConfigRepository = new DeckConfigRepository(dbm);
        this.scheduler = scheduler;
    }

    /**
     * (Re)builds today's queue. Call this once at the start of a study
     * session — remaining daily allowance is recomputed from
     * review_log each time, so calling it again mid-session is safe
     * but will re-pull whatever's still due.
     */
    public void buildQueue() throws SQLException {
        DeckConfig config = deckConfigRepository.findByLanguage(language);

        long reviewsToday = reviewLogRepository.countReviewsToday();
        long newToday = reviewLogRepository.countNewCardsIntroducedToday();

        int reviewBudget = (int) Math.max(0, config.dailyReviewLimit() - reviewsToday);
        int newBudget = (int) Math.max(0, config.dailyNewCardsLimit() - newToday);

        List<CardSrs> due = cardSrsRepository.findDue(Instant.now(), reviewBudget);
        List<CardSrs> fresh = cardSrsRepository.findNew(newBudget);

        queue = new ArrayDeque<>();
        queue.addAll(due);
        queue.addAll(fresh);
    }

    public boolean hasNext() {
        return queue != null && !queue.isEmpty();
    }

    public int remaining() {
        return queue == null ? 0 : queue.size();
    }

    public Optional<CardSrs> peekNext() {
        requireQueueBuilt();
        return Optional.ofNullable(queue.peek());
    }

    /**
     * Grades the card at the head of the queue. The card_srs update and
     * review_log insert happen as one transaction on the language's
     * connection — either both land or neither does.
     */
    public ReviewOutcome submitReview(Grade grade) throws SQLException {
        requireQueueBuilt();
        CardSrs current = queue.peek();
        if (current == null) {
            throw new IllegalStateException("Queue is empty — check hasNext() first");
        }

        ReviewOutcome outcome = scheduler.review(current, grade, Instant.now());

        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            cardSrsRepository.update(outcome.updatedCard());
            reviewLogRepository.insert(outcome.logEntry());
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }

        queue.poll();
        return outcome;
    }

    private void requireQueueBuilt() {
        if (queue == null) {
            throw new IllegalStateException("Call buildQueue() before using the session");
        }
    }
}