package handler.fx.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.fx.uifx.FXWindow;
import handler.fx.uifx.WindowController;
import handler.task.BaseBackgroundTask;

public abstract class FXBackgroundTask extends BaseBackgroundTask<FXWindow> {

    protected final WindowController controller;

    public FXBackgroundTask(FXWindow context) {
        super(context);
        controller = context.GetController();
    }

}
