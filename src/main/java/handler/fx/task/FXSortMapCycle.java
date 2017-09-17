package handler.fx.task;

import handler.fx.uifx.FXWindow;
import javafx.collections.ObservableList;

public class FXSortMapCycle extends FXBackgroundTask {


    private final ObservableList items;

    public FXSortMapCycle(FXWindow context, ObservableList items) {
        super(context);
        this.items = items;
    }

    @Override
    public void run() {
        context.Post(() -> items.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString())));
    }
}
