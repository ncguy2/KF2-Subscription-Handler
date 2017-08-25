package handler.task;

import handler.ui.Strings;
import handler.ui.Window;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class AddMapToCycle extends BackgroundTask {

    public AddMapToCycle(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JScrollPane listSubscriptions = (JScrollPane)window.get(Strings.LIST_SUBSCRIPTION);
        JList subscriptions = (JList)listSubscriptions.getViewport().getView();
        JScrollPane listCycle = (JScrollPane)window.get(Strings.LIST_CYCLE);
        JList maps = (JList)listCycle.getViewport().getView();
        List<Object> mapPool = arrayToList(maps);
        for (Object toAdd : subscriptions.getSelectedValuesList()) {
            if (!mapPool.contains(toAdd.toString())) {
                mapPool.add(toAdd.toString());
            }
        }
        listCycle.getViewport().removeAll();
        listCycle.getViewport().add(new JList(mapPool.toArray()));
    }
    
}
