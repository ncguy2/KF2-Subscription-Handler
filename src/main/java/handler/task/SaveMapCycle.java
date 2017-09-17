package handler.task;

import handler.files.KF2Files;
import handler.ui.Strings;
import handler.ui.Window;

import javax.swing.*;
import java.util.List;

public class SaveMapCycle extends BackgroundTask {

    public SaveMapCycle(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JButton button = (JButton)window.get(Strings.BUTTON_SAVE);
        button.setEnabled(false);
        JScrollPane listCycle = (JScrollPane)window.get(Strings.LIST_CYCLE);
        JList listMaps = (JList)listCycle.getViewport().getView();
        try {
            List<Object> maps = arrayToList(listMaps);
            KF2Files.setMapCycle(formatCycleString(maps));
        }
        catch (Exception ex) {
            // Handle exception
        }
        button.setEnabled(true);
    }
    
    private String formatCycleString(List<Object> maps) {
        return KF2Files.formatCycleString(maps);
    }
    
}
