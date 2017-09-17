package handler.fx.task;

import handler.fx.uifx.FXWindow;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class FXRemoveMapFromCycle extends FXBackgroundTask {

    private final ListView targetList;

    public FXRemoveMapFromCycle(FXWindow context, ListView targetList) {
        super(context);
        this.targetList = targetList;
    }

    @Override
    public void run() {
        ObservableList selected = targetList.getSelectionModel().getSelectedItems();
        context.Post(() -> targetList.getItems().removeAll(selected));
    }
}
