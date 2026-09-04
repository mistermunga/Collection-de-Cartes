package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.scene.control.Button;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.CardType;

/**
 * One multiple-choice answer button, showing a definition pulled from
 * some word or phrase in the deck. Carries both the source id and its
 * CardType — words and phrases each have their own independent id
 * sequence, so an id alone isn't enough to tell whether a selected
 * option matches the card being asked about.
 */
public class Option extends Button {

    private final long targetId;
    private final CardType targetType;

    public Option(long targetId, CardType targetType, String definition) {
        super(definition);
        this.targetId = targetId;
        this.targetType = targetType;

        setWrapText(true);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("option-button");
    }

    public long getTargetId() {
        return targetId;
    }

    public CardType getTargetType() {
        return targetType;
    }

    public boolean matches(long id, CardType type) {
        return targetId == id && targetType == type;
    }
}