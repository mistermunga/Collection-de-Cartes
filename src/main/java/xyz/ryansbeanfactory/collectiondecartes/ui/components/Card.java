package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Card extends StackPane {

    private static final Duration FLIP_DURATION = Duration.millis(300);

    private final Label lemmaLabel = new Label();
    private final Label metaLabel = new Label();
    private final Label definitionLabel = new Label();

    private final VBox front = new VBox();
    private final VBox back = new VBox();

    private final boolean flippable;
    private final BooleanProperty showingFront = new SimpleBooleanProperty(true);
    private boolean animating = false;

    public Card(boolean flippable) {
        this.flippable = flippable;
        setAlignment(Pos.CENTER);

        lemmaLabel.getStyleClass().add("card-lemma");
        metaLabel.getStyleClass().add("card-meta");
        definitionLabel.getStyleClass().add("card-definition");
        definitionLabel.setWrapText(true);

        front.setAlignment(Pos.CENTER);
        front.setSpacing(8);
        front.getStyleClass().add("card-face");
        front.getChildren().addAll(lemmaLabel, metaLabel);

        back.setAlignment(Pos.CENTER);
        back.setSpacing(8);
        back.getStyleClass().add("card-face");
        back.getChildren().add(definitionLabel);
        back.setVisible(false);
        back.setRotate(180);

        // both faces need a Y-axis rotation to flip in place
        front.setRotationAxis(Rotate.Y_AXIS);
        back.setRotationAxis(Rotate.Y_AXIS);

        getChildren().addAll(back, front);

        if (flippable) {
            setOnMouseClicked(e -> flip());
        }
    }

    public void setLemma(String lemma) {
        lemmaLabel.setText(lemma);
    }

    public void setMeta(String meta) {
        boolean hasMeta = meta != null && !meta.isBlank();
        metaLabel.setText(hasMeta ? meta : "");
        metaLabel.setVisible(hasMeta);
        metaLabel.setManaged(hasMeta);
    }

    public void setDefinition(String definition) {
        definitionLabel.setText(definition);
    }

    public boolean isShowingFront() {
        return showingFront.get();
    }

    public void flip() {
        if (!flippable || animating) return;
        animating = true;

        VBox current = showingFront.get() ? front : back;
        VBox next = showingFront.get() ? back : front;

        RotateTransition hide = new RotateTransition(FLIP_DURATION.divide(2), current);
        hide.setFromAngle(0);
        hide.setToAngle(90);
        hide.setInterpolator(Interpolator.EASE_IN);

        RotateTransition show = new RotateTransition(FLIP_DURATION.divide(2), next);
        show.setFromAngle(-90);
        show.setToAngle(0);
        show.setInterpolator(Interpolator.EASE_OUT);

        hide.setOnFinished(e -> {
            current.setVisible(false);
            next.setRotate(-90);
            next.setVisible(true);
        });

        SequentialTransition seq = new SequentialTransition(hide, show);
        seq.setOnFinished(e -> {
            showingFront.set(!showingFront.get());
            animating = false;
        });
        seq.play();
    }
}