package handler.fx.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.uifx.FXWindow;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FXFetchMapCycle extends FXBackgroundTask {

    public FXFetchMapCycle(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        controller.btnRefreshCycle.setDisable(true);
        context.Post(controller.listCycle.getItems()::clear);
        try {
            List<String> ids = KF2Files.getMapCycle();
            Map<String, Subscription> subscriptions = KF2Files.getSubscriptions();

            Function<String, Subscription> GetSub = id -> subscriptions.values().stream().filter(sub -> sub.getName().equalsIgnoreCase(id)).findFirst().orElse(null);

            for (String id : ids) {

                Subscription sub = null;
                if(KF2Files.IsNativeMap(id)) {
                    sub = new Subscription(String.valueOf(KF2Files.NativeIndex(id)));
                    sub.setName(id);
                }else sub = GetSub.apply(id);

                if(sub != null) {
                    Subscription finalSub = sub;
                    context.Post(() -> controller.listCycle.getItems().add(finalSub));
                } else System.out.println("[WARN] No subscription found with id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.btnRefreshCycle.setDisable(false);
    }
}
