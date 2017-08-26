package handler.fx.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.fx.uifx.DetailController;
import handler.fx.uifx.FXWindow;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Collection;

public class FXFetchSubscription extends FXBackgroundTask {

    public FXFetchSubscription(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        controller.btnSubscribe.setDisable(true);
        controller.btnRefreshSubscriptions.setDisable(true);
        context.Post(() -> {
            controller.accSubscriptions.getPanes().removeIf(pane -> pane != controller.paneUnknowns);
        });
        try {
            Collection<Subscription> values = KF2Files.getSubscriptions().values();
            values.forEach(value -> {
                context.Post(() -> {
                    controller.AddSubscriptionPane(value, BuildPane(value));
                });
            });

            final int unknowns = controller.removedPanes.size();
            context.Post(() -> {
                controller.DisplayNotification("Unknown subscriptions detected", "We found " + unknowns + " subscriptions that we could not determine properly.", Color.AQUAMARINE);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.btnSubscribe.setDisable(false);
        controller.btnRefreshSubscriptions.setDisable(false);
    }

    protected TitledPane BuildPane(Subscription value) {
        DetailController content = new DetailController();
        content.SetSubscription(value);
        TitledPane titledPane = new TitledPane(value.getName(), content);
        content.SetTitlePane(titledPane);
        titledPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            content.Load();
        });

        if (value.getName().equalsIgnoreCase("[Unknown Subscription]"))
            titledPane.setUserData("Unknown");


        titledPane.managedProperty().bind(titledPane.visibleProperty());

        return titledPane;
    }
}
