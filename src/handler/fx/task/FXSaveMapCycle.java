package handler.fx.task;

import handler.files.KF2Files;
import handler.fx.uifx.FXWindow;

import java.util.ArrayList;
import java.util.List;

public class FXSaveMapCycle extends FXBackgroundTask {

    public FXSaveMapCycle(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        controller.btnSaveCycle.setDisable(true);

        List<Object> maps = new ArrayList<>(controller.listCycle.getItems());
        try {
            KF2Files.setMapCycle(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        controller.btnSaveCycle.setDisable(false);
    }



}
