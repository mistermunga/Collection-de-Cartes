package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads;

import javafx.scene.layout.BorderPane;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.MainView;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections.TitleBar;

public class CrossRoads extends BorderPane {

    public CrossRoads() {
        TitleBar titleBar = new TitleBar();
        this.setTop(titleBar);

        MainView mainView = new MainView();
        this.setCenter(mainView);
    }

}
