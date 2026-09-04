package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.pages.*;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

import java.util.EnumMap;
import java.util.Map;

public class ContentArea extends StackPane {

    private final DefaultView defaultView = new DefaultView();
    private final Map<NavOption, Node> pages = new EnumMap<>(NavOption.class);

    public ContentArea(MainView parent) {
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
