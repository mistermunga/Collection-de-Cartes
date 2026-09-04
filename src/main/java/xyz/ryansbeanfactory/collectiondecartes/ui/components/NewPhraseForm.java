package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.ui.util.Dialogs;

import java.sql.SQLException;

public class NewPhraseForm extends VBox {

    private final TextField lemmaField = new TextField();
    private final TextArea definitionField = new TextArea();

    private final PhraseRepository phraseRepository = new PhraseRepository(
            CarteApplication.getInstance().getDatabaseManager(),
            AppSession.getInstance().getDeckLanguage()
    );

    public NewPhraseForm() {
        this.setMaxWidth(480);
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.TOP_LEFT);

        initialise();
    }

    private void initialise() {
        definitionField.setWrapText(true);
        definitionField.setPrefRowCount(3);

        this.getChildren().addAll(
                fieldRow("Lemma", lemmaField),
                fieldRow("Definition", definitionField)
        );

        Button submit = new Button("Submit");
        submit.getStyleClass().add("primary-button");
        submit.setOnAction(e -> {
            if (validateForm()) {
                submitPhrase();
                Dialogs.showSuccess("The phrase has been successfully submitted.");
                clearFields();
            }
        });

        HBox submitRow = new HBox(submit);
        submitRow.setAlignment(Pos.CENTER_RIGHT);
        this.getChildren().add(submitRow);
    }

    private static VBox fieldRow(String labelText, Node control) {
        Label label = new Label(labelText.toUpperCase());
        label.getStyleClass().add("field-label");

        if (control instanceof TextField || control instanceof TextArea) {
            control.getStyleClass().add("text-input");
        }
        if (control instanceof Control jfxControl) {
            jfxControl.setMaxWidth(Double.MAX_VALUE);
        }

        VBox row = new VBox(label, control);
        row.setSpacing(8);
        return row;
    }

    private boolean validateForm() {
        boolean valid = true;

        if (lemmaField.getText() == null || lemmaField.getText().trim().isEmpty()) {
            Dialogs.showError("Lemma cannot be empty.");
            lemmaField.requestFocus();
            valid = false;
        } else if (definitionField.getText() == null
                || definitionField.getText().trim().isEmpty()) {
            Dialogs.showError("Definition cannot be empty.");
            definitionField.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void submitPhrase() {
        Phrase phrase = new Phrase(lemmaField.getText(), definitionField.getText());

        try {
            phraseRepository.insert(phrase);
        } catch (SQLException e) {
            Dialogs.showError("Unable to insert phrase.");
        }
    }

    private void clearFields() {
        lemmaField.clear();
        definitionField.clear();
    }
}