package handler.fx.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.fx.uifx.DetailController;
import handler.fx.uifx.FXWindow;
import javafx.scene.Node;
import javafx.scene.control.Accordion;

public class FXAddMapToCycle extends FXBackgroundTask {

    public FXAddMapToCycle(FXWindow context) {
        super(context);
    }

    @Override
    public void run() {
        Accordion accordion = controller.accSubscriptions;
        Node content = accordion.getExpandedPane().getContent();
        if(content instanceof DetailController) {
            DetailController details = (DetailController)content;
            context.Post(() -> {
                controller.AddSubToCycle(details.GetSubscription());
            });
        }
    }
}
