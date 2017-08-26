package handler.fx.task;

import handler.fx.uifx.FXWindow;
import javafx.collections.ObservableList;

public class FXRemoveMapFromCycle extends FXBackgroundTask {

    public FXRemoveMapFromCycle(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        ObservableList selected = controller.listCycle.getSelectionModel().getSelectedItems();
        context.Post(() -> controller.listCycle.getItems().removeAll(selected));
    }
}
