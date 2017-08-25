package handler.task;

import handler.files.KF2Files;
import handler.ui.Strings;
import handler.ui.Window;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class FetchMapCycle extends BackgroundTask {

    public FetchMapCycle(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JButton button = (JButton)window.get(Strings.BUTTON_CYCLE);
        button.setEnabled(false);
        JScrollPane listCycle = (JScrollPane)window.get(Strings.LIST_CYCLE);
        JViewport maps = listCycle.getViewport();
        maps.removeAll();
        try {
            maps.add(new JList(
                KF2Files.getMapCycle().toArray()));
        }
        catch (Exception ex) {
            maps.add(new JList(
                new String[]{Strings.ERROR_NO_CYCLE}));
        }
        button.setEnabled(true);
    }
    
}
