package xyz.ryansbeanfactory.collectiondecartes.ui.util;

import javafx.scene.control.Alert;

public final class Dialogs {

    private Dialogs() {}

    public static void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showSuccess(String message) {
        show(Alert.AlertType.INFORMATION, "Success", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.getDialogPane().getStyleClass().add("themed-dialog");
        alert.getDialogPane().getStylesheets().add(
                Dialogs.class.getResource("/xyz/ryansbeanfactory/collectiondecartes/styles/workshop.css").toExternalForm()
        );
        alert.showAndWait();
    }
}