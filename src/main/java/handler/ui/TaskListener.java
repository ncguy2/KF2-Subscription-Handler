package handler.ui;

import handler.task.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class TaskListener implements ActionListener {
    
    private final Window window;
    
    public TaskListener(Window window) {
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            JButton button = (JButton)ae.getSource();
            switch (button.getName()) {
                
                case Strings.BUTTON_CYCLE:
                    new FetchMapCycle(window).start();
                    break;
                    
                case Strings.BUTTON_SUBSCRIPTION:
                    new FetchSubscription(window).start();
                    break;
                    
                case Strings.BUTTON_ADD:
                    new AddMapToCycle(window).start();
                    break;
                    
                case Strings.BUTTON_REMOVE:
                    new RemoveMapFromCycle(window).start();
                    break;
                    
                case Strings.BUTTON_SUBSCRIBE:
                    new AddSubscription(window).start();
                    break;
                    
                case Strings.BUTTON_SAVE:
                    new SaveMapCycle(window).start();
                    break;
                    
                case Strings.BUTTON_SORT:
                    new SortMapCycle(window).start();
                    break;
                    
            }
        }
        catch (Exception ex) {
            // User clicked something without an ID.
            // This shouldn't happen and thus isn't accounted for.
        }
    }
    
}
