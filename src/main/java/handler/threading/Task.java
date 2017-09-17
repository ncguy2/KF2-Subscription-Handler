package handler.threading;

import handler.fx.uifx.FXWindow;

public abstract class Task implements Runnable {

    public Task() {}

    protected String name = null;

    public final void SetName() {
        SetName(null);
    }

    public final void SetName(String name) {
        this.name = name;
    }

    public final String GetName() {
        if(name != null)
            return name;
        return getClass().getSimpleName();
    }

    public Runnable onStart = null;
    public Runnable onEnd = null;

    public void start() {

        final Runnable finalStart = this.onStart;
        final Runnable finalEnd = this.onEnd;

        // Separate task builders for less branching in task invocation
        if(finalStart == null && finalEnd == null) {
            FXWindow.GetMainContext().GetTaskHandler().Post(this);
            return;
        }

        final Task that = this;

        if(finalStart != null && finalEnd == null) {
            FXWindow.GetMainContext().GetTaskHandler().Post(new Task() {
                @Override
                public void run() {
                    finalStart.run();
                    that.run();
                }
            });
            return;
        }

        if(finalStart == null) {
            FXWindow.GetMainContext().GetTaskHandler().Post(new Task() {
                @Override
                public void run() {
                    that.run();
                    finalEnd.run();
                }
            });
            return;
        }

        FXWindow.GetMainContext().GetTaskHandler().Post(new Task() {
            @Override
            public void run() {
                finalStart.run();
                that.run();
                finalEnd.run();
            }
        });

    }

}
