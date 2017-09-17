package handler.fx.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.uifx.FXWindow;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FXRevertMapCycle extends FXBackgroundTask {

    private final Consumer<List<Subscription>> setAll;

    public FXRevertMapCycle(FXWindow context, Consumer<List<Subscription>> setAll) {
        super(context);
        this.setAll = setAll;
    }

    @Override
    public void run() {
        final int[] index = {0};
        final List<Subscription> vanillaSubs = KF2Files.GetNativeMaps().stream()
                .map(str -> {
                    Subscription subscription = new Subscription(String.valueOf(index[0]++));
                    subscription.setNative(true);
                    subscription.setOnDisk(true);
                    subscription.setNeedsUpdate(false);
                    subscription.setName(str);
                    return subscription;
                })
                .collect(Collectors.toList());
        context.Post(() -> {
            setAll.accept(vanillaSubs);
//            controller.listCycle.getItems().setAll(vanillaSubs);
        });
    }
}
