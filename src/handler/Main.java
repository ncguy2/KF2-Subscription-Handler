package handler;

import handler.fx.uifx.FXWindow;
import handler.ui.Window;
import javafx.application.Application;

import javax.swing.*;

public class Main {

    public static void main(String... args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            new Window().show();
        });

        Application.launch(FXWindow.class, args);
    }
    
}
