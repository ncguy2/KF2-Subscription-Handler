package handler.task;

import handler.ui.Window;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BackgroundTask extends BaseBackgroundTask<Window> {

    // Retained to maintain compatibility with existing tasks
    protected final Window window;

    public BackgroundTask(Window window) {
        super(window);
        this.window = window;
    }

    protected List<Object> arrayToList(JList arg) {
        List<Object> list = new ArrayList<>();
        try {
            for (int i = 0; i < arg.getModel().getSize(); i++) {
                list.add(arg.getModel().getElementAt(i));
            }
        }
        catch (Exception ex) {
            // Do nothing.
        }
        return list;
    }
    
}
