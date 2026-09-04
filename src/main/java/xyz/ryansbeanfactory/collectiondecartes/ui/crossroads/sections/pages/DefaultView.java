package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;

import java.sql.SQLException;

public class DefaultView extends VBox {

    private static final Color BG_PANEL = Color.web("#0d1526");
    private static final Color BLUE = Color.web("#3f7fd9");
    private static final Color RED = Color.web("#e14b4b");

    private static final double RADIUS = 12;

    public DefaultView() {
        this.setAlignment(Pos.CENTER);

        WordRepository wordRepository = new WordRepository(
                CarteApplication.getInstance().getDatabaseManager(),
                AppSession.getInstance().getDeckLanguage()
        );
        PhraseRepository phraseRepository = new PhraseRepository(
                CarteApplication.getInstance().getDatabaseManager(),
                AppSession.getInstance().getDeckLanguage()
        );

        long wordCount = 0;
        long phraseCount = 0;

        try {
            wordCount = wordRepository.count();
            phraseCount = phraseRepository.count();
        } catch (SQLException e) {
            wordCount = 0;
            phraseCount = 0;
        }

        HBox cards = new HBox(
                buildStatCard("Words", wordCount, BLUE),
                buildStatCard("Phrases", phraseCount, RED)
        );
        cards.setSpacing(20);
        cards.setAlignment(Pos.CENTER);

        this.getChildren().add(cards);
    }

    private VBox buildStatCard(String label, long value, Color accent) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("stat-value");

        Label captionLabel = new Label(label.toUpperCase());
        captionLabel.getStyleClass().add("stat-label");

        VBox card = new VBox(valueLabel, captionLabel);
        card.setSpacing(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPadding(new Insets(32, 24, 32, 24));

        CornerRadii radii = new CornerRadii(RADIUS);
        card.setBackground(new Background(
                new BackgroundFill(BG_PANEL, radii, Insets.EMPTY)
        ));
        card.setBorder(new Border(
                new BorderStroke(accent, BorderStrokeStyle.SOLID, radii, new BorderWidths(1))
        ));

        return card;
    }
}