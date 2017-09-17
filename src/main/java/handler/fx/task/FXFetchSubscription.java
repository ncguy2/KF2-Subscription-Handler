package handler.fx.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.IconLoader;
import handler.fx.Icons;
import handler.fx.uifx.DetailController;
import handler.fx.uifx.FXWindow;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class FXFetchSubscription extends FXBackgroundTask {

    private final BiConsumer<Subscription, TitledPane> addSubPane;

    public FXFetchSubscription(FXWindow context, BiConsumer<Subscription, TitledPane> addSubPane) {
        super(context);
        this.addSubPane = addSubPane;
    }

    @Override
    public void run() {

        try {

            int index = 0;
            Map<String, String> map = KF2Files.NativeMaps();
            for (Map.Entry<String, String> s : map.entrySet()) {
                Subscription sub = new Subscription(String.valueOf(index++));
                sub.setName(s.getKey());
                sub.setNative(true);
                sub.setOnDisk(true);

                TitledPane pane = BuildPane(sub);
                pane.textProperty().setValue(s.getValue());
                Node content = pane.getContent();
                if(content instanceof DetailController)
                    ((DetailController)content).SetHasLoaded(true);
//                context.Post(() -> controller.AddSubscriptionPane(sub, pane));
                context.Post(() -> addSubPane.accept(sub, pane));
            }

            Collection<Subscription> values = KF2Files.getSubscriptions().values();
            context.Post(() -> {
                // Unpleasant, but needs to be final to be mutable within the .forEach call
                final int[] unknowns = {controller.removedPanes.size()};
                values.forEach(value -> {
                    TitledPane pane = BuildPane(value);
                    if(pane.getUserData() != null && "Unknown".equalsIgnoreCase(pane.getUserData().toString()))
                        unknowns[0]++;
                    context.Post(() -> addSubPane.accept(value, pane));
//                    controller.AddSubscriptionPane(value, pane);
                });
                if(unknowns[0] > 0)
                    controller.DisplayNotification("Unknown subscriptions detected", "We found " + unknowns[0] + " subscriptions that we could not determine properly.", Color.AQUAMARINE);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected TitledPane BuildPane(Subscription value) {
        final DetailController content = new DetailController();
        content.SetSubscription(value);
        final TitledPane titledPane = new TitledPane(value.getName(), content);
        content.SetTitlePane(titledPane);

        titledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                content.Load();
        });

        if (value.getName().equalsIgnoreCase("[Unknown Subscription]"))
            titledPane.setUserData("Unknown");

//        titledPane.managedProperty().bindBidirectional(titledPane.visibleProperty());
        titledPane.minHeightProperty().bindBidirectional(titledPane.maxHeightProperty());

        if(value.isNative()) {
            titledPane.setGraphic(IconLoader.GetIcon(Icons.IS_NATIVE));
            titledPane.setTooltip(new Tooltip("Vanilla"));
            content.SetRequiresDownload(false);
        }else if(!value.isOnDisk()) {
            titledPane.setGraphic(IconLoader.GetIcon(Icons.IN_CLOUD));
            titledPane.setTooltip(new Tooltip("Not downloaded"));
        }else if(value.NeedsUpdate()) {
            titledPane.setGraphic(IconLoader.GetIcon(Icons.DOWNLOAD_AVAILABLE));
            titledPane.setTooltip(new Tooltip("Needs update"));
        }


        return titledPane;
    }
}
