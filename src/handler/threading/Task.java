package handler.threading;

import handler.fx.uifx.FXWindow;

public abstract class Task implements Runnable {


    public Task() {}

    public String GetName() {
        return getClass().getSimpleName();
    }

    public void start() {
        FXWindow.GetMainContext().GetTaskHandler().Post(this);
    }

}
