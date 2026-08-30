package xyz.ryansbeanfactory.collectiondecartes.ui.components;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xyz.ryansbeanfactory.collectiondecartes.CarteApplication;
import xyz.ryansbeanfactory.collectiondecartes.database.WordRepository;
import xyz.ryansbeanfactory.collectiondecartes.model.Word;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.Gender;
import xyz.ryansbeanfactory.collectiondecartes.model.refs.PartOfSpeech;
import xyz.ryansbeanfactory.collectiondecartes.session.AppSession;

import java.sql.SQLException;

public class NewWordForm extends VBox {

    private final TextField lemmaField = new TextField();
    private final TextArea definitionField = new TextArea();
    private final ComboBox<Gender> genderComboBox = new ComboBox<>();
    private final ComboBox<PartOfSpeech> partOfSpeechComboBox = new ComboBox<>();

    private final HBox genderRow = new HBox();

    // True only if the deck language actually uses grammatical gender at all.
    private final boolean languageSupportsGenders = !AppSession.getInstance()
            .getDeckLanguage()
            .getGenders()
            .contains(Gender.NONE);

    private final WordRepository wordRepository = new WordRepository(
            CarteApplication.getInstance().getDatabaseManager(),
            AppSession.getInstance().getDeckLanguage()
    );

    public NewWordForm() {
        initialiseRows();
    }

    private void initialiseRows() {
        HBox lemmaRow = new HBox();
        Label lemma = new Label("Lemma:");
        lemmaRow.getChildren().addAll(lemma, lemmaField);
        this.getChildren().add(lemmaRow);

        HBox definitionRow = new HBox();
        Label definition = new Label("Definition:");
        definitionRow.getChildren().addAll(definition, definitionField);
        this.getChildren().add(definitionRow);

        HBox partOfSpeechRow = new HBox();
        Label partOfSpeech = new Label("Part Of Speech:");

        partOfSpeechComboBox.getItems().addAll(PartOfSpeech.values());
        partOfSpeechRow.getChildren().addAll(partOfSpeech, partOfSpeechComboBox);

        this.getChildren().add(partOfSpeechRow);

        // Gender only ever matters for nouns, and only for languages that have genders at all.
        Label gender = new Label("Gender:");
        genderComboBox.getItems().addAll(Gender.values());
        genderRow.getChildren().addAll(gender, genderComboBox);

        // Only show the row once we know the selected part of speech is NOUN.
        setGenderRowVisible(false);

        partOfSpeechComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean shouldShowGender = languageSupportsGenders && newVal == PartOfSpeech.NOUN;
            setGenderRowVisible(shouldShowGender);

            if (!shouldShowGender) {
                genderComboBox.setValue(null);
            }
        });

        this.getChildren().add(genderRow);

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            if (validateForm()) {
                submitWord();
                showSuccess();
                clearFields();
            }
        });

        this.getChildren().add(submit);
    }

    private void setGenderRowVisible(boolean visible) {
        genderRow.setVisible(visible);
        genderRow.setManaged(visible);
    }

    private boolean isNounSelected() {
        return partOfSpeechComboBox.getValue() == PartOfSpeech.NOUN;
    }

    private void submitWord() {
        // Gender only applies to nouns; force it to NONE for everything else,
        // regardless of what's left over in the combo box.
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
            showError("Unable to insert word.");
        }
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

        // Validate part of speech
        else if (partOfSpeechComboBox.getValue() == null) {
            showError("Please select a part of speech.");
            partOfSpeechComboBox.requestFocus();
            valid = false;
        }

        // Validate gender only if the current language uses genders AND the word is a noun
        else if (languageSupportsGenders
                && isNounSelected()
                && genderComboBox.getValue() == null) {

            showError("Please select a gender.");
            genderComboBox.requestFocus();
            valid = false;
        }

        return valid;
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
        partOfSpeechComboBox.getSelectionModel().clearSelection();
        partOfSpeechComboBox.setValue(null);
        genderComboBox.getSelectionModel().clearSelection();
        genderComboBox.setValue(null);
    }
}