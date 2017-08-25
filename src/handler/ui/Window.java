package handler.ui;

import handler.task.FetchMapCycle;
import handler.task.FetchSubscription;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;

public class Window {
    
    private final JFrame frame;
    
    public Window() {
        frame = new JFrame(Strings.WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 500));
        frame.setResizable(false);
    }
    
    public void show() {
        Container window = frame.getContentPane();
        window.setLayout(new FlowLayout());
        
        window.add(Components.list(Strings.LIST_SUBSCRIPTION));
        window.add(Components.list(Strings.LIST_CYCLE));
        
        window.add(Components.button(Strings.BUTTON_SUBSCRIPTION, Strings.WINDOW_BUTTON_SUBSCRIPTION, this));
        window.add(Components.button(Strings.BUTTON_CYCLE, Strings.WINDOW_BUTTON_CYCLE, this));
        window.add(Components.button(Strings.BUTTON_ADD, Strings.WINDOW_BUTTON_ADD, this));
        window.add(Components.button(Strings.BUTTON_REMOVE, Strings.WINDOW_BUTTON_REMOVE, this));
        
        window.add(Components.separator());
        
        window.add(Components.textField());
        window.add(Components.button(Strings.BUTTON_SAVE, Strings.WINDOW_BUTTON_SAVE, this));
        window.add(Components.button(Strings.BUTTON_SUBSCRIBE, Strings.WINDOW_BUTTON_SUBSCRIBE, this));
        window.add(Components.button(Strings.BUTTON_SORT, Strings.WINDOW_BUTTON_SORT, this));
        
        launchThreads();
        frame.pack();
        frame.setVisible(true);
    }
    
    private void launchThreads() {
        new FetchMapCycle(this).start();
        new FetchSubscription(this).start();
    }
    
    public void add(Component component) {
        frame.getContentPane().add(component);
        frame.pack();
    }
    
    public Component get(String componentId) {
        for (Component component : frame.getContentPane().getComponents()) {
            if (component.getName().equals(componentId)) {
                return component;
            }
        }
        return null;
    }
    
}
