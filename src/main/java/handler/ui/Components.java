package handler.ui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public abstract class Components {
    
    public static JLabel label(String labelId, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setName(labelId);
        return label;
    }
    
    public static JScrollPane list(String listId) {
        JList component = new JList();
        JScrollPane textPane = new JScrollPane(component, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textPane.setPreferredSize(new Dimension(290, 200));
        textPane.setName(listId);
        return textPane;
    }
    
    public static JButton button(String buttonId, String buttonText, Window window) {
        JButton button = new JButton(buttonText);
        button.addActionListener(new TaskListener(window));
        button.setPreferredSize(new Dimension(290, 30));
        button.setName(buttonId);
        return button;
    }
    
    public static JTextField textField() {
        JTextField textField = new JTextField("Steam Workshop ID here");
        textField.setName(Strings.TEXTFIELD_SUBSCRIPTION);
        textField.setPreferredSize(new Dimension(290, 30));
        textField.setHorizontalAlignment(JTextField.CENTER);
        return textField;
    }
    
    public static Component separator() {
        Component box = Box.createVerticalBox();
        box.setName("#placeholderSeparator");
        box.setPreferredSize(new Dimension(600, 116));
        return box;
    }
    
}
