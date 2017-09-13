package handler.fx.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.domain.Subscription;
import handler.fx.uifx.DetailController;
import handler.fx.uifx.FXWindow;
import javafx.scene.Node;
import javafx.scene.control.Accordion;

import java.util.function.Consumer;

public class FXAddMapToCycle extends FXBackgroundTask {

    private final Accordion accordion;
    private final Consumer<Subscription> addSub;

    public FXAddMapToCycle(FXWindow context, Accordion accordion, Consumer<Subscription> AddSub) {
        super(context);
        this.accordion = accordion;
        addSub = AddSub;
    }

    @Override
    public void run() {
        Node content = accordion.getExpandedPane().getContent();
        if(content instanceof DetailController) {
            DetailController details = (DetailController)content;
            context.Post(() -> addSub.accept(details.GetSubscription()));
        }
    }
}
