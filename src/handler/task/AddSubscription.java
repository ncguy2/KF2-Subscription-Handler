package handler.task;

import handler.files.KF2Files;
import handler.ui.Strings;
import handler.ui.Window;
import javax.swing.JButton;
import javax.swing.JTextField;

public class AddSubscription extends BackgroundTask {

    public AddSubscription(Window window) {
        super(window);
    }
    
    @Override
    public void run() {
        JButton button = (JButton)window.get(Strings.BUTTON_SUBSCRIBE);
        button.setEnabled(false);
        JTextField textField = (JTextField)window.get(Strings.TEXTFIELD_SUBSCRIPTION);
        try {
            KF2Files.addSubscription(Integer.parseInt(textField.getText()));
            new FetchSubscription(window).start();
            textField.setText("");
        }
        catch (Exception ex) {
            // Handle exception
        }
        button.setEnabled(true);
    }
    
}
