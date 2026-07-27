package xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.sections;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SplitPane;
import xyz.ryansbeanfactory.collectiondecartes.theme.ThemeManager;
import xyz.ryansbeanfactory.collectiondecartes.ui.crossroads.util.NavOption;

public class MainView extends SplitPane {

    private final ObjectProperty<NavOption> page = new SimpleObjectProperty<>(NavOption.DEFAULT);

    public MainView() {
        NavBar navBar = new NavBar(this);
        ContentArea contentArea = new ContentArea(this);

        this.getItems().addAll(navBar, contentArea);
    }

    public ObservableValue<NavOption> pageProperty() {
        return page;
    }

    public NavOption getPage() {
        return page.get();
    }

    public void setPage(NavOption page) {
        this.page.set(page);
    }
}
