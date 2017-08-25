package handler.task;

import handler.domain.Subscription;
import handler.files.KF2Files;
import handler.ui.Strings;
import handler.ui.Window;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class FetchSubscription extends BackgroundTask {
    
    public FetchSubscription(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JButton button = (JButton)window.get(Strings.BUTTON_SUBSCRIPTION);
        button.setEnabled(false);
        JScrollPane listSubscriptions = (JScrollPane)window.get(Strings.LIST_SUBSCRIPTION);
        JViewport viewPort = listSubscriptions.getViewport();
        viewPort.removeAll();
        try {
            Collection<Subscription> subscriptions = KF2Files.getSubscriptions().values();
            Object[] displaySubscriptions = subscriptions.toArray();
            viewPort.add(new JList(displaySubscriptions));
        }
        catch (Exception ex) {
            viewPort.add(new JList(new String[]{ex.getMessage()}));
        }
        button.setEnabled(true);
    }
    
}
