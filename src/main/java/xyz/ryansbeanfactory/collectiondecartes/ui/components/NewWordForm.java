package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.PartOfSpeech;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;
import xyz.ryansbeanfactory.collectiondecartes.ui.util.Dialogs;

import java.sql.SQLException;

public class NewWordForm extends VBox {

    private final TextField lemmaField = new TextField();
    private final TextArea definitionField = new TextArea();
    private final ComboBox<Gender> genderComboBox = new ComboBox<>();
    private final ComboBox<PartOfSpeech> partOfSpeechComboBox = new ComboBox<>();

    private final VBox genderRow = fieldRow("Gender", genderComboBox);

    private final boolean languageSupportsGenders = !AppSession.getInstance()
            .getDeckLanguage()
            .getGenders()
            .contains(Gender.NONE);

    private final WordRepository wordRepository = new WordRepository(
            CarteApplication.getInstance().getDatabaseManager(),
            AppSession.getInstance().getDeckLanguage()
    );

    public NewWordForm() {
        this.setMaxWidth(480);
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.TOP_LEFT);

        initialiseRows();
    }

    private void initialiseRows() {
        definitionField.setWrapText(true);
        definitionField.setPrefRowCount(3);

        partOfSpeechComboBox.getStyleClass().add("form-control");
        partOfSpeechComboBox.getItems().addAll(PartOfSpeech.values());

        genderComboBox.getStyleClass().add("form-control");
        genderComboBox.getItems().addAll(Gender.values());

        genderRow.setVisible(false);
        genderRow.setManaged(false);

        partOfSpeechComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean shouldShowGender = languageSupportsGenders && newVal == PartOfSpeech.NOUN;
            genderRow.setVisible(shouldShowGender);
            genderRow.setManaged(shouldShowGender);

            if (!shouldShowGender) {
                genderComboBox.setValue(null);
            }
        });

        this.getChildren().addAll(
                fieldRow("Lemma", lemmaField),
                fieldRow("Definition", definitionField),
                fieldRow("Part Of Speech", partOfSpeechComboBox),
                genderRow
        );

        Button submit = new Button("Submit");
        submit.getStyleClass().add("primary-button");
        submit.setOnAction(e -> {
            if (validateForm()) {
                submitWord();
                Dialogs.showSuccess("The word has been successfully submitted.");
                clearFields();
            }
        });

        HBox submitRow = new HBox(submit);
        submitRow.setAlignment(Pos.CENTER_RIGHT);
        this.getChildren().add(submitRow);
    }

    private static VBox fieldRow(String labelText, javafx.scene.Node control) {
        Label label = new Label(labelText.toUpperCase());
        label.getStyleClass().add("field-label");

        if (control instanceof TextField || control instanceof TextArea) {
            control.getStyleClass().add("text-input");
        }
        if (control instanceof javafx.scene.control.Control jfxControl) {
            jfxControl.setMaxWidth(Double.MAX_VALUE);
        }

        VBox row = new VBox(label, control);
        row.setSpacing(8);
        return row;
    }

    private boolean isNounSelected() {
        return partOfSpeechComboBox.getValue() == PartOfSpeech.NOUN;
    }

    private void submitWord() {
        Gender gender = (languageSupportsGenders && isNounSelected())
                ? genderComboBox.getValue()
                : Gender.NONE;

        Word word = new Word(
                lemmaField.getText().trim(),
                definitionField.getText().trim(),
                partOfSpeechComboBox.getValue(),
                gender
        );

        try {
            wordRepository.insert(word);
        } catch (SQLException e) {
            Dialogs.showError("Unable to insert word.");
        }
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
        } else if (partOfSpeechComboBox.getValue() == null) {
            Dialogs.showError("Please select a part of speech.");
            partOfSpeechComboBox.requestFocus();
            valid = false;
        } else if (languageSupportsGenders
                && isNounSelected()
                && genderComboBox.getValue() == null) {
            Dialogs.showError("Please select a gender.");
            genderComboBox.requestFocus();
            valid = false;
        }

        try {
            if (wordRepository.existsByLemma(lemmaField.getText())) {
                valid = false;
                Dialogs.showError("Lemma already exists.");
            }
        } catch (SQLException e) {
            Dialogs.showError("Unable to validate word.");
            valid = false;
        }

        return valid;
    }

    private void clearFields() {
        lemmaField.clear();
        definitionField.clear();
        partOfSpeechComboBox.getSelectionModel().clearSelection();
        partOfSpeechComboBox.setValue(null);
        genderComboBox.getSelectionModel().clearSelection();
        genderComboBox.setValue(null);
    }
}