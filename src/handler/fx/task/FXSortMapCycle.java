package handler.fx.task;

import handler.fx.uifx.FXWindow;

public class FXSortMapCycle extends FXBackgroundTask {

    public FXSortMapCycle(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        context.Post(() -> {
            controller.btnSortCycleAlpha.setDisable(true);
            controller.listCycle.getItems().sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
            controller.btnSortCycleAlpha.setDisable(false);
        });
    }
}
