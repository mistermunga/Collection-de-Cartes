package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages.*;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

import java.util.EnumMap;
import java.util.Map;

public class ContentArea extends StackPane {

    private static final Color BG_VOID = Color.web("#070b16");

    private final DefaultView defaultView = new DefaultView();
    private final Map<NavOption, Node> pages = new EnumMap<>(NavOption.class);

    public ContentArea(MainView parent) {
        this.setBackground(new Background(
                new BackgroundFill(BG_VOID, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        this.setPadding(new Insets(40));

        pages.put(NavOption.NEW_WORD, new AddWordPage());
        pages.put(NavOption.NEW_PHRASE, new AddPhrasePage());
        pages.put(NavOption.FLASHCARDS, new FlashCardView());
        pages.put(NavOption.DICTIONARY, new DictionaryView());

        parent.pageProperty().subscribe(this::updateContent);
    }

    private void updateContent(NavOption page) {
        getChildren().setAll(pages.getOrDefault(page, defaultView));
    }
}