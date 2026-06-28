module xyz.ryansbeanfactory.collectiondecartes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens xyz.ryansbeanfactory.collectiondecartes to javafx.fxml;
    exports xyz.ryansbeanfactory.collectiondecartes;
    exports xyz.ryansbeanfactory.collectiondecartes.session;
    exports xyz.ryansbeanfactory.collectiondecartes.theme;
    opens xyz.ryansbeanfactory.collectiondecartes.session to javafx.fxml;
}