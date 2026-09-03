package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.PhraseRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.Phrase;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;

import java.sql.SQLException;

public class NewPhraseForm extends VBox {

    private final TextField lemmaField = new TextField();
    private final TextArea definitionField = new TextArea();

    private final PhraseRepository phraseRepository = new PhraseRepository(
            CarteApplication.getInstance().getDatabaseManager(),
            AppSession.getInstance().getDeckLanguage()
    );

    public NewPhraseForm() {
        initialise();
    }

    private void initialise() {
        HBox lemmaRow = new HBox();
        Label lemma = new Label("Lemma:");
        lemmaRow.getChildren().addAll(lemma, lemmaField);
        this.getChildren().add(lemmaRow);

        HBox definitionRow = new HBox();
        Label definition = new Label("Definition:");
        definitionRow.getChildren().addAll(definition, definitionField);
        this.getChildren().add(definitionRow);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (validateForm()) {
                submitPhrase();
                showSuccess();
                clearFields();
            }
        });

        this.getChildren().add(submit);
    }

    private boolean validateForm() {
        boolean valid = true;

        // Validate lemma
        if (lemmaField.getText() == null || lemmaField.getText().trim().isEmpty()) {
            showError("Lemma cannot be empty.");
            lemmaField.requestFocus();
            valid = false;
        }

        // Validate definition
        else if (definitionField.getText() == null
                || definitionField.getText().trim().isEmpty()) {
            showError("Definition cannot be empty.");
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
            showError("Unable to insert phrase.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("The word has been successfully submitted.");
        alert.showAndWait();
    }

    private void clearFields() {
        lemmaField.clear();
        definitionField.clear();
    }
}
