package handler;

import handler.fx.uifx.FXWindow;
import handler.ui.Window;
import javafx.application.Application;

import javax.swing.*;

public class Main {

    public static void main(String... args) throws Exception {
        boolean useLegacy = false;

        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("-legacy"))
                useLegacy = true;
        }

//        SwingUtilities.invokeLater(() -> new Window().show());

        if(useLegacy)
            SwingUtilities.invokeLater(Window::new);
        else Application.launch(FXWindow.class, args);
    }
    
}
