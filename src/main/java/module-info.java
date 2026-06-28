module xyz.ryansbeanfactory.collectiondecartes {
    requires javafx.controls;
    requires javafx.fxml;


    opens xyz.ryansbeanfactory.collectiondecartes to javafx.fxml;
    exports xyz.ryansbeanfactory.collectiondecartes;
    exports xyz.ryansbeanfactory.collectiondecartes.session;
    opens xyz.ryansbeanfactory.collectiondecartes.session to javafx.fxml;
}