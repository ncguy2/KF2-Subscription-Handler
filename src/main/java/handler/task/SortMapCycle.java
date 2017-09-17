package handler.task;

import handler.ui.Strings;
import handler.ui.Window;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class SortMapCycle extends BackgroundTask {

    public SortMapCycle(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JButton button = (JButton)window.get(Strings.BUTTON_SORT);
        button.setEnabled(false);
        JScrollPane listCycle = (JScrollPane)window.get(Strings.LIST_CYCLE);
        List<Object> maps = arrayToList((JList)listCycle.getViewport().getView());
        Collections.sort(maps, new Comparator<Object>() {
            @Override
            public int compare(Object obj, Object obj2) {
                return obj.toString().compareToIgnoreCase(obj2.toString());
            }
        });
        listCycle.getViewport().removeAll();
        listCycle.getViewport().add(new JList(maps.toArray()));
        button.setEnabled(true);
    }
    
}
