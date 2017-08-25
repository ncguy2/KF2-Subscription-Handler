package handler.task;

import handler.ui.Strings;
import handler.ui.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class RemoveMapFromCycle extends BackgroundTask {

    public RemoveMapFromCycle(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JScrollPane listCycle = (JScrollPane)window.get(Strings.LIST_CYCLE);
        JList cycle = (JList)listCycle.getViewport().getView();
        List<Object> maps = new ArrayList<>();
        for (int i = 0; i < cycle.getModel().getSize(); i++)
            maps.add(cycle.getModel().getElementAt(i));
        for (Object map : cycle.getSelectedValuesList())
            maps.remove(map);
        listCycle.getViewport().removeAll();
        listCycle.getViewport().add(
            new JList(maps.toArray()));
    }
    
}
