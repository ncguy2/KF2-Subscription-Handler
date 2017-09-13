package handler.fx.task;

import handler.files.KF2Files;
import handler.fx.uifx.FXWindow;

import java.util.List;
import java.util.function.Supplier;

public class FXSaveMapCycle extends FXBackgroundTask {

    private final Supplier<List<Object>> mapSupplier;

    public FXSaveMapCycle(FXWindow context, Supplier<List<Object>> mapSupplier) {
        super(context);
        this.mapSupplier = mapSupplier;
    }

    @Override
    public void run() {
        List<Object> maps = mapSupplier.get();
        try {
            KF2Files.setMapCycle(maps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
