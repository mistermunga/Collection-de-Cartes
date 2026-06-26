module xyz.ryansbeanfactory.collectiondecartes {
    requires javafx.controls;
    requires javafx.fxml;


    opens xyz.ryansbeanfactory.collectiondecartes to javafx.fxml;
    exports xyz.ryansbeanfactory.collectiondecartes;
}